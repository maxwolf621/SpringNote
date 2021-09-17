# [JPA `@Embedded` and `@Embeddable`](https://www.baeldung.com/jpa-embedded-embeddable)  


the `@Embeddable` annotation to declare that a class will be embedded by other entities.
```java
@Embeddable
public class ContactPerson {

    private String firstName;

    private String lastName;

    private String phone;

    // standard getters, setters
}
```


`@Embedded` is used to embed a type into another entity.
```java
/**
  * <p> To have our entity Company, 
  *     embedding contact person detail
  *      and mapping to a single database table </p>
  */
@Entity
public class Company {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String address;

    private String phone;

    /**
      * class {@code ContactPerson} with annotation {@code Embeddable} so we can use
      * annotation {@code Embedded} to embed {@code ContactPerson}
      */
    @Embedded
    @AttributeOverrides({
        @AttributeOverride( name = "firstName", column = @Column(name = "contact_first_name")),
        @AttributeOverride( name = "lastName", column = @Column(name = "contact_last_name")),
        @AttributeOverride( name = "phone", column = @Column(name = "contact_phone"))
    })
    private ContactPerson contactPerson;
    // standard getters, setters
}
```
- using `@AttributeOverride` to tell JPA how to map these fields to database columns.
