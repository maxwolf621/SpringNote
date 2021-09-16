# JPA

[Code Java Tutorial](https://www.codejava.net/frameworks/hibernate/java-hibernate-jpa-annotations-tutorial-for-beginners)  




## Java Persistence API (JPA) (package `javax.persistence`)  
JPA is a Java API specification for relational data management in applications using Java SE and Java EE.      
JPA defines Java Persistence Query Language (JPQL) which is an object-oriented query language.    
The syntax of JPQL is similar to SQL but it operates against Java objects rather than directly with database tables.  

## Hibernate
Hibernate is one of JPA's implementations   
Hibernate is a popular Object Relational Mapping (ORM) framework that aims at simplifying database programming for developers.   
**Hibernate was developed before JPA. And after JPA becomes a standard, Hibernate restructures itself to become an implementation of JPA.**   

## Annotation in Model Class

`@Entity`
This annotation indicates that the class is mapped to a database table.  
> By default, the ORM framework understands that the class name is as same as the table name.　　  

`@Table`
**This annotation is used if the class name is different than the database table name**  

```java
/**
  * Since the class name is User and the table name is Users
  */
@Entity
@Table(name = "USERS")
public class User {
```

`@Column`
This annotation is used to map an instance field of the class to a column in the database table, and it is must placed before the getter method of the field. 
> By default, Hibernate can implicitly infer the mapping based on field name and field type of the class.    
> But if the field name and the corresponding column name are different, we have to use this annotation explicitly.   

```java
/**
  * model class field {@code id} is mapping to
  * {@code USER_ID} in database
  */
@Column(name = "USER_ID")
public Integer getId() {
    return id;
}
```

`@Id`
This annotation specifies that a field is mapped to a primary key column in the table. 
```
@Column(name = "USER_ID")
@Id
public Integer getId() {
    return id;
}
```

`@GeneratedValue`
If the values of the primary column are auto-increment, we need to use this annotation to tell Hibernate

Strategy Types :　`AUTO`, `IDENTITY`, `SEQUENCE`, and `TABLE.` 

```java
/**
  * AUTO implies that the generated values are unique at database level.
  * IDENTITY which specifies that the generated values are unique at table level
  */
@Column(name = "USER_ID")
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
public Integer getId() {
    return id;
}
```
- `SEQUENCE`： PK was hold by database's `sequence`
   >  IDENTITY generation disables batch updates.
- `IDENTITY`： PK was hold by database's  `auto-increment`
  > This generator uses sequences if they're supported by our database, and switches to table generation if they aren't.
  > To customize the sequence name, we can use the @GenericGenerator annotation with SequenceStyleGenerator strategy:
- `AUTO`：PK was created via JPA


### TABLE 
The TableGenerator uses an underlying database table that holds segments of identifier generation values.
```java
/**
  * @TableGenerator to customize GenerationType.TABLE
  */
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, 
      generator = "table-generator")
    @TableGenerator(name = "table-generator", 
      table = "dep_ids", 
      pkColumnName = "seq_id", 
      valueColumnName = "seq_value")
    private long depId;

    // ...
}
```
- In this example, we can see that other attributes such as the pkColumnName and valueColumnName can also be customized.
- The disadvantage of this method is that it doesn't scale well and can negatively affect performance.


## `@GenericGenerator`

Custom PK-generationType via hibernate 

```java
@Entity
public class User {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
      name = "sequence-generator",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @Parameter(name = "sequence_name", value = "user_sequence"),
        @Parameter(name = "initial_value", value = "4"),
        @Parameter(name = "increment_size", value = "1")
        }
    )
    private long userId;
    
    // ...
}
```
- In this example, we've also set an initial value for the sequence, which means the primary key generation will start at 4.
- SEQUENCE is the generation type recommended by the Hibernate documentation.
- The generated values are unique per sequence. If you don't specify a sequence name, Hibernate will re-use the same hibernate_sequence for different types.

[Custom-GenerationType Define](https://www.baeldung.com/hibernate-identifiers#5-custom-generator)   


## Spring MVC `EntityManager` and `EntityManagerFactory`

The Conception of `EntityManager` and `EntityManagerFactory` is kinda like session in hibernate

EntityManager  
**An EntityManager instance is associated with a persistence context, and it is used to interact(`persist()`) with the database.**  
- A persistence context is a set of entity instances, which are actually the objects or instances of the model classes.  
  >　So we use the EntityManager to manage entity instances and their life cycle, such as create entities, update entities, remove entities, find and query entities.  

EntityManagerFactory  
**In Java SE environments, an EntityManagerFactory can be obtained from the Persistence class.**  
- **An EntityManagerFactory is used to create an EntityManager.** 
  >　EntityManagerFactory is associated with a persistence unit. 


### Procession  

First To tell Hibernate how to connect to the database, we need to configure a XML file, for example  `persistence.xml` 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1" xmlns="http://xmlns.jcp.org/xml/ns/persistence"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
        http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    <!-- Which EntityManagerFactory will create --> 
    <persistence-unit name="PostDB">
        <properties>
            <!--Connect to the database-->
            <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/postdb" /> <!--JDBC url points to database-->
            <property name="javax.persistence.jdbc.user" value="root" />
            <property name="javax.persistence.jdbc.password" value="4321" />
            <property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver" />
            <property name="hibernate.show_sql" value="true" /> <!--show logs in terminal-->
            <property name="hibernate.format_sql" value="true" /> <!-- format sql statements -->
        </properties>
    </persistence-unit>     
</persistence>
```

#### Create an EntityManagerFactory from a persistence unit
```java
EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PostDB");
```

#### Create an EntityManager from the EntityManagerFactory
```java
EntityManager entityManager = entityManagerFactory.createEntityManager();
```

#### Begin a transaction
```java
entityManager.getTransaction().begin();
```

### To Manage entity instances (`create`, `update`, `remove`, `find`, `query`, ...)

#### `EntityManager#persist(Model model)`
```java
/**
  * create the post
  */
Post post = Post().builder()
                  .subname("Test")
                  .title("sample for transaction")
                  .description("example")
                  .build();
/** persist the instance {@code post} to underlying database **/
entityManager.persist(post);
```

#### `EntityManager#merge(Model model)`
```java
/**
  * Update the existing post
  */
Post post = Post().builder()
                  .id(1)
                  .subname("Test")
                  .title("sample for transaction")
                  .description("example")
                  .build();
 
entityManager.merge(post);
```


#### `EntityManager#remove(Model model)`
```java
Integer primaryKey = 1;
Model reference = entityManager.getReference(Model.class, primaryKey);

entityManager.remove(reference);
```

#### `EntityManager#find(Model model,  Object<?> primaryKey)`
```java
Integer primaryKey = 1;
Post post = entityManager.find(Post.class, primaryKey);
```


#### Commit the transaction
```java
entityManager.getTransaction().commit();
```

#### Close the EntityManager and EntityManagerFactory
```java 
entityManager.close();
entityManagerFactory.close();
```

## JPA methods 
[Transient operations](https://www.javaguides.net/2018/11/guide-to-jpa-and-hibernate-cascade-types.html)      
![image](https://user-images.githubusercontent.com/68631186/131173715-cb24c972-f2a9-4e45-b823-71fefc874431.png)      

