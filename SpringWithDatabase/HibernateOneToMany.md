###### tags: `Hibernate`
# One-to-Many Association

![](https://i.imgur.com/G1wyJoo.png)

## dependencies in pom.xml
```xml
<dependencies>
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>4.2.7.SP1</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.26</version>
    </dependency>
</dependencies> 
```

## Model Class
```java
@Entity
@Table(name = "CATEGORY")
public class Category {
    // attributes name, id ...

    private Set<Product> products;

    // we don't need to initialize id
    //    because @GeneratedValue
    public Category(String name) {
        this.name = name;
    }
    @Id
    @Column(name = "CATEGORY_ID")
    @GeneratedValue
    public long getId() {
        return id;
    }
 

    /**
      *  Although you link 2 tables together, 
      *  only 1 of these tables has a foreign key constraint to the other one.
      *  It allows you to still link 
      *  from the table not containing 
      *  the constraint to the other table.
      */
    @OneToMany(
        mappedBy = "category", 
        cascade = CascadeType.ALL)
    public Set<Product> getProducts() {
        return products;
    }
 
    // other getters and setters...
}
```


```java
@Entity
@Table(name = "PRODUCT")
public class Product {
    // other attributes
 
    private Category category;
    public Product() {}
 
    // here we need to initialize that a product belongs to which category
    public Product(String name, String description, float price,
            Category category) {
            //....
    }
 
    @Id
    @Column(name = "PRODUCT_ID")
    @GeneratedValue
    public long getId() {
        return id;
    }
 
    @ManyToOne
    // reference to the category's pk
    @JoinColumn(name = "CATEGORY_ID")
    public Category getCategory() {
        return category;
    }
 
    // other getters and setters...
}
```


Set up `hibernate.cfg.xml`
```java
<hibernate-configuration>       
  <session-factory>
    <!-- Database connection settings -->
    <property name="connection.driver_class">com.mysql.jdbc.Driver</property>
    <property name="connection.url">jdbc:mysql://localhost:3306/stockdb</property>
    <property name="connection.username">root</property>
    <property name="connection.password">secret</property>
    <property name="dialect">org.hibernate.dialect.MySQLDialect</property>
    <property name="show_sql">true</property>
     
    <mapping class="com.project.hibernate.Category"/>
    <mapping class="com.project.hibernate.Product"/>
       
  </session-factory>
</hibernate-configuration>
```

## Run Program

```java
public class StockManager {
    public static void main(String[] args) {
        // loads configuration and mappings
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder registry = new StandardServiceRegistryBuilder();
        registry.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = registry.buildServiceRegistry();
         
        // builds a session factory from the service registry
        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
         
        // obtains the session
        Session session = sessionFactory.openSession();
        session.beginTransaction();
         
        Category category = new Category("Computer");
         
        Product pc = new Product("DELL PC", "Quad-core PC", 1200, category);
         
        Product laptop = new Product("MacBook", "Apple High-end laptop", 2100, category);
         
        Product phone = new Product("iPhone 5", "Apple Best-selling smartphone", 499, category);
         
        Product tablet = new Product("iPad 3", "Apple Best-selling tablet", 1099, category);
         
        Set<Product> products = new HashSet<Product>();

        products.add(pc);
        products.add(laptop);
        products.add(phone);
        products.add(tablet);
         
        category.setProducts(products);
         
        session.save(category);
         
        session.getTransaction().commit();
        session.close();       
    }
}
```