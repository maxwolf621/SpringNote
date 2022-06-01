# Cascade of JPA and Hibernate 

- [Guide to JPA and Hibernate Cascade Types](https://www.javaguides.net/2018/11/guide-to-jpa-and-hibernate-cascade-types.html)  
[](https://zhuanlan.zhihu.com/p/419473875)  

JPA allows you to propagate the state transition from a parent entity to a child. For this purpose, the JPA `javax.persistence.CascadeType` defines various cascade types:
- `CascadeType.REMOVE` 
  - If removed entity has any child entities then they will be deleted too   
- `CascadeType.MERGE`
  - If an entity is updates then the mapped one will be also updated
- `CascadeType.DETACH`
  - It is required if deleted entity has the FK (otherwise we can't delete the entity with FK) 
- `CascadeType.REFRESH` (e.g. `git pull`) 
  - If order associates many item, the order can have many different operation by different persons, so assume *userA* and *userB* modify the order at same time and userB `flush`/`save` it to database, if *userA* needs to update, *userA* needs to refresh the data first (to async the data) and modify again and flush it to database after 

Additionally, the `CascadeType.ALL` will propagate any Hibernate-specific operation, which is defined by the `org.hibernate.annotations.CascadeType` enum:
- `SAVE_UPDATE` - cascades the entity saveOrUpdate operation.
- `REPLICATE` - cascades the already existing entity replicate(reproduce) operation.
- `LOCK` - reattach entity and its associated entity in the persistence 

**Cascading only makes sense only for Parent-Child associations** (the Parent entity state transition being cascaded to its Child entities).  
- Cascading from Child to Parent is not very useful and usually, it’s a mapping code smell.


### Example 
- [Example](https://www.baeldung.com/jpa-cascade-types)

Assume we had models `Person` and `Address` 
```java
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;

    // It is maintained by column "person"
    // from Table Address
    @OneToMany(mappedBy = "person", 
               cascade = CascadeType.ALL)
    private List<Address> addresses;

    // setter and getter
}

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String street;
    private String city;
    private int zipCode;

    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;

    // setter and getter
}
```

Builder
```java
private Address buildAddress(Person person) {
    Address address = new Address();
    address.setCity("TK");
    address.setStreet("meow");
    address.setZipCode(12345);
    address.setPerson(person);
    return address;
}

private Person buildPerson(String name) {
    Person person = new Person();
    person.setName(name);
    return person;
}
```

## Persist

Cascade Type PERSIST **propagates(pass through)** the persist operation from a parent to a child entity.
```java
public void whenParentSavedThenChildSaved() {
    Person person = new Person());
    Address address = new Address());
    
    address.setPerson(person);
    person.setAddresses(Arrays.asList(address));
    
    // Cascade Persist
    session.persist(person);
    // Commit data in the database
    session.flush();
    session.clear();
}
```

After insert a new entity in parent side , it also is inserted in child side 
```sql
-- father 
Hibernate: insert into Person (name, id) values (?, ?)

-- child
Hibernate: insert into Address (
    city, houseNumber, person_id, street, zipCode, id) values (?, ?, ?, ?, ?, ?)
```

## Merge
The merge operation **copies the state of the given object** onto the persistent object with the same identifier. 

```java
@Test
public void whenParentSavedThenMerged() {
    int addressId;
  
    Person person = buildPerson("John");
    Address address = buildAddress(person);
    
    person.setAddresses(Arrays.asList(address));
    
    session.persist(person);
    session.flush();
    addressId = address.getId();
    session.clear(); // clear all transaction in this session

    
    // fetch data in database
    Address savedAddressEntity = 
            // Find Address by Id
            session.find(Address.class, addressId);
    Person savedPersonEntity = 
            // Find Person by Address
            savedAddressEntity.getPerson();
    
    // update data
    savedPersonEntity.setName("John Mayer");
    savedAddressEntity.setHouseNumber(24);
    
    // Cascade merge 
    session.merge(savedPersonEntity);
    
    session.flush();
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

-- update
Hibernate: update Address 
           set city=?, houseNumber=?, person_id=?, street=?, zipCode=? 
           where id=?

Hibernate: update Person 
           set name=? 
           where id=?
```

## Remove

CascadeType REMOVE propagates the remove operation from parent to child entity. 
- It deletes every child entities which their FK is reference of father entity

```java
@Test
public void whenParentRemovedThenChildRemoved() {
    
    int personId;
    Person person = buildPerson("joan");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    session.persist(person);
    session.flush();
    personId = person.getId();
    session.clear();

    
    // CascadeType.REMOVE
    Person savedPersonEntity = session.find(Person.class, personId);
    session.remove(savedPersonEntity);
    session.flush();
}
```
```sql
-- delete child entity first, parent entity after
Hibernate: delete from Address where id=?
Hibernate: delete from Person where id=?
```

## Detach

The detach operation removes the entity from the persistent context. 
- When we use `CascadeType.DETACH`, the child entity will also get removed from the persistent context.

```java
public void whenParentDetachedThenChildDetached() {
    
    Person person = buildPerson("joan");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    
    session.persist(person);
    session.flush(); // write persistence into database
     
    assertThat(session.contains(person)).isTrue();
    assertThat(session.contains(address)).isTrue();

    // Detach data in the persistence (cache
    session.detach(person);

    // now there are no transaction (parent and child) in persistence layer
    assertThat(session.contains(person)).isFalse();
    assertThat(session.contains(address)).isFalse();
}
```
- After detaching `person`, neither `person` nor `address` exists in the persistent context.

## LOCK

LOCK reattaches the entity and its associated child entity with the persistent context again.

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

    // detach the data in the persistence (cache)
    session.detach(person);
    assertThat(session.contains(person)).isFalse();
    assertThat(session.contains(address)).isFalse();

    // reattaches data back to persistence  
    session.unwrap(Session.class)
           .buildLockRequest(new LockOptions(LockMode.NONE))
           .lock(person);

    assertThat(session.contains(person)).isTrue();
    assertThat(session.contains(address)).isTrue();
}
```
- we attached the entity `person` and its associated `address` back to the persistent(layer) context.

## REFRESH

Refresh operations reread the value of a given instance from the database. 
- **In some cases, we may change an instance after persisting in the database, but later we need to undo those changes.**
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

    // now person.gerName 
    // will be joan not lava   
    assertThat(person.getName()).isEqualTo("joan");
    assertThat(address.getHouseNumber()).isEqualTo(23);
}
```
- we made some changes in the saved entities person and address. When we refresh the person entity, the address also gets refreshed.

## REPLICATE

The replicate operation allows you to synchronize entities coming from different sources of data.
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

```sql
SELECT id
FROM Person
WHERE id = 2

SELECT id
FROM address
WHERE id = 2

UPDATE Person
SET name = 'devender'
WHERE id = 2

UPDATE address
SET person_id = 2
WHERE id = 2
```


## SAVE_UPDATE
`SAVE_UPDATE` propagates the same operation to the associated child entity. 

```java
@Test
public void whenParentSavedThenChildSaved() {
    Person person = buildPerson("devender");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    
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
