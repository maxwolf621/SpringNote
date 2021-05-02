###### tags: `Hibernate`
# One-to-Many Association

![](https://i.imgur.com/G1wyJoo.png)

## dependencies in pom.xml
```xml=
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
- `MappedBy` signals hibernate that the key for the relationship is on the other(owning) side.
    > This means that although you link 2 tables together, **only 1 of these tables has a foreign key constraint to the other one.** 
- `MappedBy` allows you to still link from the table not containing the constraint to the other table.

```java=
@Entity
@Table(name = "CATEGORY")
public class Category {
    // attributes name, id ...

    private Set<Product> products;

    // we dont need to initialize id
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
 
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    public Set<Product> getProducts() {
        return products;
    }
 
    // other getters and setters...
}
```


```java=
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


## Set up hibernate.cfg.xml

```java=
<hibernate-configuration>       
  <session-factory>
    <!-- Database connection settings -->
    <property name="connection.driver_class">com.mysql.jdbc.Driver</property>
    <property name="connection.url">jdbc:mysql://localhost:3306/stockdb</property>
    <property name="connection.username">root</property>
    <property name="connection.password">secret</property>
    <property name="dialect">org.hibernate.dialect.MySQLDialect</property>
    <property name="show_sql">true</property>
     
    <mapping class="net.codejava.hibernate.Category"/>
    <mapping class="net.codejava.hibernate.Product"/>
       
  </session-factory>
</hibernate-configuration>
```

## Run Program

[Note for Session Factory](/3xYG4oxDQHq9u3BHlL8qsg)

```java=
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

<style>
html, body, .ui-content {
    background-color: #333;
    color: #AFDCEC;
}

body > .ui-infobar {
    display: none;
}

.ui-view-area > .ui-infobar {
    display: block;
    color: #999;

}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5{
    color: #d1f1a9;
}

.markdown-body h6{
    color: yellow;
}

.markdown-body h1,
.markdown-body h2 {
    border-bottom-color: #ffffff69;
}

.markdown-body h1 .octicon-link,
.markdown-body h2 .octicon-link,
.markdown-body h3 .octicon-link,
.markdown-body h4 .octicon-link,
.markdown-body h5 .octicon-link,
.markdown-body h6 .octicon-link {
    color: #fff;
}

.markdown-body img {
    background-color: transparent;
}

.ui-toc-dropdown .nav>.active:focus>a, .ui-toc-dropdown .nav>.active:hover>a, .ui-toc-dropdown .nav>.active>a {
    color: white;
    border-left: 2px solid white;
}

.expand-toggle:hover, 
.expand-toggle:focus, 
.back-to-top:hover, 
.back-to-top:focus, 
.go-to-bottom:hover, 
.go-to-bottom:focus {
    color: white;
}


.ui-toc-dropdown {
    background-color: #333;
    /*background-image: linear-gradient(90deg, #111, #333;*/
}

.ui-toc-label.btn {
    background-color: #444;
    color: white;
    /*background-image: linear-gradient(10deg, #333, #333);*/
}

.ui-toc-dropdown .nav>li>a:focus, 
.ui-toc-dropdown .nav>li>a:hover {
    color: white;
    border-left: 1px solid white;
    
}


.markdown-body table tr {
    background-color: #2a4a5f;
    color: #66cccc;

}
.markdown-body strong{
    color: #cc6666;
}

.markdown-body mark{
    color:#d54e53;
    background: #ffeead;
}


.markdown-body blockquote {
    color: white ;
    background: ;
}

.markdown-body table tr:nth-child(2n) {
    background-color: #4f4f4f;
}


.markdown-body table tr:hover {
    background-color: #ebaa;
}

.markdown-body code,
.markdown-body tt {
    color: #AFDCEC ;
    background-color: #646D7E ;
}

a,
.open-files-container li.selected a {
    color: #5EB7E0;
}

.markdown-body pre{
    color: #243C5A;
    background-color : #eeeee;
}

/*dropdown Bar*/
.ui-toc-label.btn {
    background-color: #191919;
    color: #eee;
}
/*inside the bar*/
.ui-toc-dropdown .nav>li>a:focus, 
.ui-toc-dropdown .nav>li>a:hover {
    color: gold;
    border-left: 1px solid white;
}

a,.open-files-container li.selected a {
    color: #5EB7E0;
}

/* == == */
.markdown-body mark,
mark 
{
    background-color: #708090 !important ;
    color: gold;
    margin: .1em;
    padding: .1em .2em;
    font-family: Helvetica;
}

/* scroll bar */
.ui-edit-area .ui-resizable-handle.ui-resizable-e {
background-color: #303030;
border: 1px solid #303030;
box-shadow: none;
}
/* info bar */
.ui-infobar {
color: #999;
}

/*----Prism.js -----*/
code[class*="language-"],
pre[class*="language-"] {
color: #DCDCDC;
}

:not(pre)>code[class*="language-"],
pre[class*="language-"] {
background: #1E1E1E;
}

.token.comment,
.token.block-comment,
.token.prolog,
.token.cdata {
color: #57A64A;
}

.token.doctype,
.token.punctuation {
color: #9B9B9B;
}

.token.tag,
.token.entity {
color: #569CD6;
}

.token.attr-name,
.token.namespace,
.token.deleted,
.token.property,
.token.builtin {
color: #9CDCFE;
}

.token.function,
.token.function-name {
color: #dcdcaa;
}

.token.boolean,
.token.keyword,
.token.important {
color: #569CD6;
}

.token.number {
color: #B8D7A3;
}

.token.class-name,
.token.constant {
color: #4EC9B0;
}

.token.symbol {
color: #f8c555;
}

.token.rule {
color: #c586c0;
}

.token.selector {
color: #D7BA7D;
}

.token.atrule {
color: #cc99cd;
}

.token.string,
.token.attr-value {
color: #D69D85;
}

.token.char {
color: #7ec699;
}

.token.variable {
color: #BD63C5;
}

.token.regex {
color: #d16969;
}

.token.operator {
color: #DCDCDC;
background: transparent;
}

.token.url {
color: #67cdcc;
}

.token.important,
.token.bold {
font-weight: bold;
}

.token.italic {
font-style: italic;
}

.token.entity {
cursor: help;
}

.token.inserted {
color: green;
}
</style>