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


## Spring MVC EntityManager and EntityManagerFactory

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
            <property name="hibernate.format_sql" value="true" /> <!-- formmat sql statements -->
        </properties>
    </persistence-unit>     
</persistence>
```

Create an EntityManagerFactory from a persistence unit
```java
EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PostDB");
```

Create an EntityManager from the EntityManagerFactory
```java
EntityManager entityManager = entityManagerFactory.createEntityManager();
```

Begin a transaction
```java
entityManager.getTransaction().begin();
```

Manage entity instances (`create`, `update`, `remove`, `find`, `query`, ...)

`EntityManager#persist(Model model)`
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

`EntityManager#merge(Model model)`
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


`EntityManager#remove(Model model)`
```java
Integer primaryKey = 1;
Model reference = entityManager.getReference(Model.class, primaryKey);

entityManager.remove(reference);
```

`EntityManager#find(Model model,  Object<?> primaryKey)`
```java
Integer primaryKey = 1;
Post post = entityManager.find(Post.class, primaryKey);
```



Commit the transaction
```java
entityManager.getTransaction().commit();
```

Close the EntityManager and EntityManagerFactory
```java 
entityManager.close();
entityManagerFactory.close();
```


