[Code Java Tutorial](https://www.codejava.net/frameworks/hibernate/java-hibernate-jpa-annotations-tutorial-for-beginners)  


Java Persistence API (JPA) (package `javax.persistence`.)
JPA is a Java API specification for relational data management in applications using Java SE and Java EE.   
JPA defines Java Persistence Query Language (JPQL) which is an object-oriented query language.   
The syntax of JPQL is similar to SQL but it operates against Java objects rather than directly with database tables.

Hibernate Framework:
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

```
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
If the values of the primary column are auto-increment, we need to use this annotation to tell Hibernate knows, along with one of the following strategy types: 
`AUTO, IDENTITY, SEQUENCE, and TABLE.` 

- strategy AUTO implies that the generated values are unique at database level.

```java
/**
  * IDENTITY which specifies that the generated values are unique at table level
  */
@Column(name = "USER_ID")
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
public Integer getId() {
    return id;
}
```
