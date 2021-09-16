###### tags: `Hibernate`
# OneToOne Mapping
[TOC]


## @GeneratedValue

there are 4 options to generate primary keys

```java
/**
  * option 1 
  * <p>let the persistence provider choose the generation strategy </p>
  */
@Id
@GeneratedValue(strategy = GenerationType.Auto)
private Long id;

/**  
  * option 2
  * Let the Database generate a new value with each insert operation
  * <strong> Drawback : It requires a primary key value for each managed entity and therefore has to perform the insert statement immediately </strong>
  */
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
# 1:1 Association via `@PrimaryKeyJoinColumn`
[Source Code](https://www.codejava.net/frameworks/hibernate/hibernate-one-to-one-association-on-primary-key-annotations-example)   

![](https://i.imgur.com/qJYmIBh.png)
## Model Class
product.java 
```java
// package  ...
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
@Table(name = "PRODUCT")
public class Product 
{
    //..
    private ProductDetail productDetail;
    public Product() {}
    
    @Id
    @GeneratedValue
    @Column(name = "PRODUCT_ID")
    public long getProductId() {
        return productId;
    }
 
    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public ProductDetail getProductDetail() {
        return productDetail;
    }
    //..
}
```
- `@OneToOne` 
   > it tells Hibernate creates a one-to-one association with the `ProductDetail`

- `@PrimaryKeyJoinColumn`
   > it specifies a primary key column that is used as a foreign key to join to another table   
   > ![](https://i.imgur.com/IX9hRG8.png)    


[Difference BTW PrimaryKeyJoinColumn and JoinColumn](https://stackoverflow.com/questions/3417097/jpa-difference-between-joincolumn-and-primarykeyjoincolumn#:~:text=The%20PrimaryKeyJoinColumn%20annotation%20is%20used,in%20which%20the%20primary%20key)   
[CascadeType and FetchType](https://openhome.cc/Gossip/EJB3Gossip/CascadeTypeFetchType.html)    


ProductDetail.java
```java
//.. import persistence.*

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
 
@Entity
@Table(name = "PRODUCT_DETAIL")
public class ProductDetail {
    //..
    private Product product;
    public ProductDetail() {}
 
    @Id
    @GeneratedValue(generator = "foreigngen")
    @GenericGenerator(strategy = "foreign", name="foreigngen",
        parameters = @Parameter(name = "property", value="product"))
    @Column(name = "PRODUCT_ID")
    public long getProductId() {
        return productId;
    }
 
    //.. 
    
    @OneToOne(mappedBy = "productDetail")
    public Product getProduct() {
        return product;
    }
 
    // other getters and setters
}
```

> `@GeneratedValue` 
> : Make a conjunction to map a field as the primary key of the table (normally, PK's values are auto-generated)
>
> `@GenericGenerator` 
> : Specify a foreign key strategy in order to generate values for the `product_id` column as a foreign key, parameter generator in `@GeneratedValue` must be same as parameter name in `@GenericGenerator`


## Configure hibernate.cfg.xml 

for XML descriptor in the `hibernate.cfg.xml` file to tell Hibernate which database to connect

```xml
<hibernate-configuration>
  <session-factory>
    <!-- where your model classes stores -->
    <mapping class="com.xxxx.hibernate.Product"/>
    <mapping class="com.xxxx.hibernate.ProductDetail"/>
  </session-factory>
</hibernate-configuration>
```

# 1:1 Foreign Key Annotations via `@JoinColum`
#### [Code Example](https://www.codejava.net/frameworks/hibernate/hibernate-one-to-one-mapping-with-foreign-key-annotations-example)

![](https://i.imgur.com/wGtr1Dw.png)


## Model Classes
```java
/* Entity of author */
@Entity
@Table(name="AUTHOR")
public class Author{
  //....
  @Id
  @Column(name = "AUTHOR_ID")
  @GeneratedValue
  public long getId() {
      return id;
  }
  //..
}

/* Entity of book */
@Entity
@Table(name = "BOOK")
public class Book {
    // Attributes
    private Author author;
    public Book() {}
 
    @Id
    @Column(name = "BOOK_ID")
    @GeneratedValue
    public long getId() {
        return id;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "PUBLISHED")
    public Date getPublishedDate() {
        return publishedDate;
    }
 
    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }
 
    @OneToOne(cascade = CascadeType.ALL)
    // 1:1 @JoinColum means Each Book Entity references to attributes Author_ID from other entity
    @JoinColumn(name = "AUTHOR_ID")
    public Author getAuthor() {
        return author;
    } 
}
```
- `@Temproal`
    > Must be sure with `util.Data` field to specify the actual SQL type of the column

