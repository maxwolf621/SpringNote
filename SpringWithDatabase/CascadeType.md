# Cascade


Quick Review
- `CascadeType.REMOVE` : if delete entity has child entity then child is deleted too   
- `CascadeType.MERGE` : if an entity is update then the mapped one will be also updated
- `CascadeType.DETACH` : we need it when we need to delete the entity which has the FK (otherwise we cant delete the entity with FK) 
- `CascadeType.REFRESH` : if order associates many item,the order can have many different operation by different persons, so assume userA and userB modify the order at same time and userB flush/save it to database, if userA needs to update, he needs to refresh the data first (to async the data) and modify again and flush it to database after (like `git pull`)


Assume we had models Person and Address 
```java
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Address> addresses;
}

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String street;
    private int houseNumber;
    private String city;
    private int zipCode;
    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;
}
```

## Persist

Cascade Type PERSIST propagates the persist operation from a parent to a child entity.

```java
public void whenParentSavedThenChildSaved() {
    Person person = new Person();
    Address address = new Address();
    
    address.setPerson(person);
    person.setAddresses(Arrays.asList(address));
    
    /**
      * persist person(parent side) only 
      */
    session.persist(person);
    session.flush();
    session.clear();
}
```

After insert a new entity in parent side , child side also is inserted
```sql
Hibernate: insert into Person (name, id) values (?, ?)

-- insert a new entity too
Hibernate: insert into Address (
    city, houseNumber, person_id, street, zipCode, id) values (?, ?, ?, ?, ?, ?)
```

## MERGE

The merge operation **copies the state of the given object** onto the persistent object with the same identifier. 

```java
@Test
public void whenParentSavedThenMerged() {
    int addressId;
    
    /**
      * Create an entity first
      */
    Person person = buildPerson("John");
    Address address = buildAddress(person);
    
    person.setAddresses(Arrays.asList(address));
    
    session.persist(person);
    session.flush();

    addressId = address.getId();
    session.clear(); // clear all transaction in this session

    /**
      * Create New Transaction
      *       fetch the entity from database 
      */
    // Find Address by Id
    Address savedAddressEntity = session.find(Address.class, addressId);
    // Find Person by Address
    Person savedPersonEntity = savedAddressEntity.getPerson();
    
    /**
      * update data
      */
    savedPersonEntity.setName("John mayer");
    savedAddressEntity.setHouseNumber(24);
    
    /**
      * MERGE (now the child entity will be update too)
      */
    session.merge(savedPersonEntity);
    
    session.flush(); // save to database
```

```sql
-- load both entities address and person
Hibernate: select address0_.id as id1_0_0_, 
                  address0_.city as city2_0_0_, 
                  address0_.houseNumber as houseNum3_0_0_, 
                  address0_.person_id as person_i6_0_0_, 
                  address0_.street as street4_0_0_, 
                  address0_.zipCode as zipCode5_0_0_ 
            from Address address0_ 
            where address0_.id=?

Hibernate: select person0_.id as id1_1_0_, 
                  person0_.name as name2_1_0_ 
            from Person person0_ 
            where person0_.id=?

/** 
  * updates both as a result of CascadeType.MERGE.
  **/
Hibernate: update Address 
           set city=?, houseNumber=?, person_id=?, street=?, zipCode=? 
           where id=?

Hibernate: update Person 
           set name=? 
           where id=?
```

## Remove

`CascadeType.REMOVE` propagates the remove operation from parent to child entity. 

**Similar to JPA's CascadeType.REMOVE, we have CascadeType.DELETE, which is specific to Hibernate. There is no difference between the two.**

```java
@Test
public void whenParentRemovedThenChildRemoved() {
    
    /**
      * create entity 
      */
    int personId;
    Person person = buildPerson("joan");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    session.persist(person);
    session.flush();
    personId = person.getId();
    session.clear();

    /**
      * Do CascadeType.REMOVE
      */
    Person savedPersonEntity = session.find(Person.class, personId);
    session.remove(savedPersonEntity);
    session.flush();
}
```
```sql
-- delete child first and then parent
Hibernate: delete from Address where id=?
Hibernate: delete from Person where id=?
```

## Detach

The detach operation removes the entity from the persistent context. 

When we use `CascadeType.DETACH`, the child entity will also get removed from the persistent context.

```java
public void whenParentDetachedThenChildDetached() {
    
    /**
      * create entity
      */
    Person person = buildPerson("joan");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    
    session.persist(person);
    session.flush(); // write persistence to database
     
    assertThat(session.contains(person)).isTrue();
    assertThat(session.contains(address)).isTrue();

    /**
      * Detach person(who has fk)
      */
    session.detach(person);

    // now there are no transaction (parent and child) in persistence layer
    assertThat(session.contains(person)).isFalse();
    assertThat(session.contains(address)).isFalse();
}
```
- After detaching person, neither person nor address exists in the persistent context.


## LOCK

CascadeType.LOCK reattaches the entity and its associated child entity with the persistent context again.

```java
@Test
public void whenDetachedAndLockedThenBothReattached() {
    
    Person person = buildPerson("devender");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    // flush data to database
    session.persist(person);
    session.flush();
    
    assertThat(session.contains(person)).isTrue();
    assertThat(session.contains(address)).isTrue();

    /**
      * detach the persistence 
      */
    session.detach(person);
    assertThat(session.contains(person)).isFalse();
    assertThat(session.contains(address)).isFalse();

    /**
      * reattaches the entity 
      */
    session.unwrap(Session.class).buildLockRequest(new LockOptions(LockMode.NONE)).lock(person);

    assertThat(session.contains(person)).isTrue();
    assertThat(session.contains(address)).isTrue();
}
```
As we can see, when using `CascadeType.LOCK`, we attached the entity `person` and its associated `address` back to the persistent(layer) context.

## REFRESH

Refresh operations reread the value of a given instance from the database. 

**In some cases, we may change an instance after persisting in the database, but later we need to undo those changes.**

- When we use this operation with Cascade Type REFRESH, the child entity also gets reloaded from the database whenever the parent entity is refreshed.

```java
@Test
public void whenParentRefreshedThenChildRefreshed() {
    
    /**
      * create an entity and flush to database
      */
    Person person = buildPerson("joan");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    session.persist(person);
    session.flush();

    

    person.setName("lava");
    address.setHouseNumber(24);
    
    // Cancel the above two operations
    session.refresh(person);

    // now person.gerName will be joan not lava   
    assertThat(person.getName()).isEqualTo("joan");
    assertThat(address.getHouseNumber()).isEqualTo(23);
}
```
Here, we made some changes in the saved entities person and address. When we refresh the person entity, the address also gets refreshed.

## REPLICATE
The replicate operation is used when we have more than one data source and we want the data in sync. 

- a sync operation also propagates to child entities whenever performed on the parent entity.

```java
@Test
public void whenParentReplicatedThenChildReplicated() {
    
    Person person = buildPerson("devender");
    person.setId(2);
    
    Address address = buildAddress(person);
    address.setId(2);
    
    person.setAddresses(Arrays.asList(address));
    
    session.unwrap(Session.class)
           .replicate(person, ReplicationMode.OVERWRITE);
    
    session.flush();
    

    assertThat(person.getId()).isEqualTo(2);
    assertThat(address.getId()).isEqualTo(2);
}
```
- when we replicate the person entity, its associated address also gets replicated with the identifier we set.

## SAVE_UPDATE for hibernate

`CascadeType.SAVE_UPDATE` propagates the same operation to the associated child entity. 

It's useful when we use Hibernate-specific operations like `save`, `update` and `saveOrUpdate`. 

```java
@Test
public void whenParentSavedThenChildSaved() {
    Person person = buildPerson("devender");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    
    /**
      * save father entity only 
      */
    session.saveOrUpdate(person);
    session.flush();
}
```

```sql
Hibernate: insert into Person (name, id) values (?, ?)

-- child entity also is saved
Hibernate: insert into Address (
    city, houseNumber, person_id, street, zipCode, id) values (?, ?, ?, ?, ?, ?)
```