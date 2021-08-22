###### tags: `Hibernate`
# Deletion of entity from Database
[TOC]

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
    
    /* Prouduct relates Category 
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
```java
session.delete(class instsance);
```

For example
```java
Product product = new Product();
product.setId(12);
session.delete(product);
```

If delete instance that is associated with information in database, the code will throw `ConstraintViolationException` at **run-time**
```java
Category category = new Category();
// Id 18 is existing in the database
category.setId(18)
// you cant delete information
//    in the databse
session.delete(category);
// throwing ConstraintViolationException
```

The given information would look like
```sql
ERROR: Cannot delete or update a parent row: a foreign key constraint fails
(`stockdb`.`product`, CONSTRAINT `fk_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`))
```


## Deleting A **Persistent** Instance

```java
// To access Database of NameOfClass Entity
session.load(NameOfClass.class, id);

/**
 * Or we can create A method 
 * For example ... 
 */
private boolean delete(Class<?> type, Serializable id){
    Object persistentInstance = session.laod(type,id);
    if(persistenInstance != null){
        session.delete(persistentInstance);
        return true;
    }
    return false;
}
```

Example 
```java
Serializable id = new Long(17);
Object persistentInstance = session.laod(Category.class, id);
// check if id : 17 is in the database
if(persistantInstance != null){
    session.delete(persistentInstance)
}
```


## Using HQL 

Delete entities with more flexibility
```java
Query q = session.createQuery("delete Product where price > :maxPreice");

int result = q.executeUpdate();

if(result > 0)
{
    system.out.println("Products were removed");
}
```
> However, it doesn't remove associated instances
> 
