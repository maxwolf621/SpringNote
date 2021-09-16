###### tags: `Hibernate`
# Deletion of entity from Database

![](https://i.imgur.com/g4SWzho.png)

```java
/* Entity category */
public class Category{
    
    //...
    
    /**
     * Category relates Product 
     *    One      TO     MANY     
     */
    @OneToMany(mappedBy="category", cascade = CascadeType.ALL)
    //  Product is maintained by category
    public Product product(){
        return product
    }
}

/* Entity product */
public class Product{
    //..
    
    /* Product relates Category 
     *  MANY      TO      ONE   
     */
    @OneToMany
    /* FK is categor_id         */
    @JoinColumn(name="CATEGORY_ID")
    public Category category{
        return category
    }
}
```

## Deleting A **Transient** Instance

Syntax
```java
session.delete(class<T> entity);
```

For Example
```java
Product product = new Product();
product.setId(12);
session.delete(product);
```

If instance that is associated with information in database, the code will throw `ConstraintViolationException` at **run-time**
```java
Category category = new Category();

// Id 18 is already existing in the database
category.setId(18)

// you cant delete information in the database
session.delete(category);

// throwing ConstraintViolationException
```

The given information in console would look like
```sql
ERROR: Cannot delete or update a parent row: a foreign key constraint fails
(`stockdb`.`product`, CONSTRAINT `fk_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`))
```

## Deleting A **Persistent** Instance


Syntax
```java
/**
  * load a session for communicating with database
  * To retrieve certain data from Database 
  * @param NameOfClass.class : Entity's name
  * @param id : attribute of this entity
  */
session.load(NameOfClass.class, id);

/**
 * Deletion Method
 * Because of persistence , We need {@code Serializable} 
 */
private boolean delete(Class<?> type, Serializable id){
    Object persistentInstance = session.load(type,id);
    if(persistentInstance != null){
        session.delete(persistentInstance);
        return true;
    }
    return false;
}
```

For Example 
```java
Serializable id = new Long(17);
Object persistentInstance = session.load(Category.class, id);
// check if id : 17 is in the database
if(persistentInstance != null){
    session.delete(persistentInstance)
}
```