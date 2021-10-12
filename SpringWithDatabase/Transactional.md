# Transaction

Once an entity is actively managed by Hibernate, all changes are going to be automatically propagated to the database.     

Manipulating domain model entities (along with their associations) is much easier than writing and maintaining SQL statements.  
Without an ORM tool, adding a new column requires modifying all associated INSERT/UPDATE statements.  

## The Entity States

#### Related Notes
[Session Factory](/3xYG4oxDQHq9u3BHlL8qsg)  
[Hibernate Session](/ItrVlAdSSEuo7vLEjtXGRw)  
[Reference](https://vladmihalcea.com/a-beginners-guide-to-jpa-hibernate-entity-state-transitions/)  

### JPA and Hibernate Transaction
![](https://i.imgur.com/ueO4FzQ.png)  
![](https://i.imgur.com/BAd8i1q.png)  
 

### `New` (Transient)
A newly created object that hasn’t **ever been associated with a Hibernate Session** (a.k.a Persistence Context) and is not **mapped to any database** table row is considered to be in the New (Transient) state.

To become persisted we need to either explicitly call the `EntityManager#persist` method or make use of the transitive persistence mechanism.

### Persistent (Managed)
**A persistent entity has been associated with a database table row and it’s being managed by the current running Persistence Context.**

During the Session flush-time  
- Any change made to such entity is going to be detected and propagated to the database. 

With Hibernate, we no longer have to execute `INSERT/UPDATE/DELETE` statements. 
Hibernate employs a transactional write-behind working style and changes are synchronized at the very last responsible moment, during the current Session flush-time. 

### Detached
**Once the current running Persistence Context is closed** all the previously managed entities become detached.  
Successive changes will no longer be tracked and no automatic database synchronization is going to happen.  

To associate a detached entity to an active Hibernate Session,  
you can choose one of the following options: 
1. Reattaching
  > Hibernate (but not JPA 2.1) supports reattaching through the `Session#update` method.  
  > **A Hibernate Session can only associate one Entity object for a given database row.**  
  >> This is because the Persistence Context acts as an in-memory cache (first level cache) and only one value (entity) is associated to a given key (entity type and database identifier).  

**An entity can be reattached only if there is no other JVM object (matching the same database row) already associated to the current Hibernate Session.**

2. Merging
  > **The merging operaration is going to copy the detached entity state (source) to a managed entity instance (destination).**  
  > If the merging entity has no equivalent in the current Session, one will be fetched from the database.  
  > The detached object instance will continue to remain detached even after the merge operation.   

## Removed

Although JPA demands that managed entities only are allowed to be removed, Hibernate can also delete detached entities (but only through a Session#delete method call).  
**A removed entity is only scheduled for deletion and the actual database DELETE statement will be executed during Session ==flush-time.==**  
## Begin Transaction 

![](https://i.imgur.com/Nzdy9nA.png)

```java
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import com.journaldev.hibernate.util.HibernateUtil;
public class LoadExample {
   public static void main(String[] args) {
      //get session factory to start transcation
      SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
      Session session = sessionFactory.openSession();
      Transaction tx = session.beginTransaction();
      // A transaction called tx will access the database
      //    To operate something with database
      
      tx.commit() // commit a transaction
      session.close();
      seesionFactory.close();
    }
}
```

To catch an exception 
```java
try {  
    session = sessionFactory.openSession();  
    tx = session.beginTransaction();  
    //some action  

    tx.commit();  
  
}catch (Exception ex) {  
    ex.printStackTrace();  
    tx.rollback();  
}  
finally {
    session.close();
}  
```


## Spring Boot Transaction Annotation

[Reference](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)

### @EnableTransactionManagement 

```java
@Configuration
@EnableTransactionManagement
public class PersistenceJPAConfig{
   @Bean
   public LocalContainerEntityManagerFactoryBean
     entityManagerFactoryBean(){
      //...
   }
   @Bean
   public PlatformTransactionManager transactionManager(){
      JpaTransactionManager transactionManager
        = new JpaTransactionManager();
      transactionManager.setEntityManagerFactory(
        entityManagerFactoryBean().getObject() );
      return transactionManager;
   }
}
```


### @Transactional

![](https://i.imgur.com/aJBdvkV.png)


[CodeFor@Transactional](https://blog.csdn.net/qq_20597727/article/details/84868035)

>  - Spring creates **proxies** for all the classes annotated with `@Transactional`, either on the class or on any of the methods.
>  - Transactional is implemented by AOP (`@Around`)

All Transactionals extends   `org.springframework.transaction.PlatformTransactionManager`  
![](https://i.imgur.com/x4yye9l.png)
```java
public interface PlatformTransactionManager {
  TransactionStatus getTransaction(TransactionDefinition definition)
  throws TransactionException;
  void commit(TransactionStatus status) throws TransactionException;
  void rollback(TransactionStatus status) throws TransactionException;
}
```
- It makes use of the attributes `rollbackFor` or `rollbackFor{ClassName}` to rollback the transactions
- The attributes `noRollbackFor` or `noRollbackFor{ClassName}` to avoid rollback on listed exceptions.


![](https://i.imgur.com/yC8DnR6.png)


By default, **rollback happens for run-time**, unchecked exceptions only.    
**The checked exception does not trigger a rollback of the transaction.**   
We can, of course, configure this behavior with the `rollbackFor{ClassName}` and `noRollbackFor{ClassName}` annotation parameters.   

### Proxy of Transaction
![](https://i.imgur.com/chIcGgL.png)


If the transactional bean is implementing an interface, by default the proxy will be a <u>Java Dynamic Proxy.</u>
- This means that only external method calls that come in through the proxy will be intercepted. 
- A `@Transactional` of an interface class is not recommended
- Any self-invocation calls will not start any transaction, even if the method has the `@Transactional` annotation.

Another cases of using proxies is that only **public methods or classes** should be annotated with `@Transactional`.  

- Methods of any other visibilities(e.g. `private` ... etc ) will simply ignore the annotation silently as these are not proxies.

#### Flow of transactional
[Reference](https://virtualmackem.blog/2019/03/28/transactional-gotchas/)

_<u>When a @Transactional method A calls another @Transactional method B, an uncaught RuntimeException in the second method rolls back the entire transaction</u>_ 
1. Method A is annotated with `@Transactional`. 
     > It calls Method B (in a different class) several times. 
2. Method B is also annotated with `@Transactional`.  
3. Method B can fail, throwing a `RuntimeException`.  
4. Method A therefore catches any `RuntimeExceptions` and tidies things up before completing successfully. 
5. However, you notice that when Method A runs, any `RuntimeExceptions` in Method B always roll back the entire transaction, including the changes made by Method B 
 - It’s because the default propagation on `@Transactional` is `REQUIRED`. 
 - The meaning for `REQUIRED` as follows: Support a current transaction, create a new one if none exists

**The `@Transactional` annotation places `advice` _around_ a method (either with AspectJ or a proxy, more of that later). which means some of the `advice` runs `before` the method and some `after`.**

It works like this:
![](https://i.imgur.com/EdM63ve.png)
- The _before_ advice for Method A creates a transaction then calls Method A
- Method A then calls Method B
- The _before_ advice for Method B checks to see if there’s a transaction and there is Method B then runs and the exception is thrown
- The “after” advice for Method B sees the exception bubble up through it and sets “rollback only” on the transaction. This cannot be undone Thus, the entire transaction (originally created by Method A) is rolled back

### Review Exception before `rollback` and `noRollbackFor`

![](https://i.imgur.com/9W6f7C5.png)

#### Unchecked exception
> The Error , RuntimeException or subClass of them

To handle these exceptions with `try…catch…finally`, but it better that we adjust our code to deal with the exceptions.  

The following Exceptions are some common unchecked exceptions
```java
ArithmetricException
ClassCastException
NullPointerException
```
 
#### Checked exception 
Exceptions that not belong to Error or RuntimeException.

**Checked Exception will cause compiler error, and maintainer must use try-catch or throws to handle the error**

Common Checked Exceptions are
```
SQLException
IOException
ClassNotFoundException
```


### Attribute of `@Transactional`

The annotation `@Transactional` supports further configuration :
- the `Propagation` Type of the transaction
- the `Isolation` Level of the transaction
    > [<u>more details</u>](https://dev.to/ildar10/spring-transaction-management-isolation-and-propagation-483m) 
- a `Timeout` for the operation wrapped by the transaction
- a `readOnly` flag(set as false by default)
    > ![](https://i.imgur.com/0gMnn28.png)
- the `Rollback` rules for the transaction

[Details](https://medium.com/@rameez.s.shaikh/spring-boot-transaction-tutorial-understanding-transaction-propagation-ad553f5d85d4)   

The default rollback behavior in the declarative approach will rollback on run-time exceptions.
```java
// A default behavior
@Transactional
public void createCourseDeclarativeWithRuntimeException(Course course) {
    courseDao.create(course);
    throw new DataIntegrityViolationException("Throwing exception for demoing Rollback!!!");
}
```

#### RollBack for a checked exception
```java
@Transactional(rollbackFor = { SQLException.class })
public void createCourseDeclarativeWithCheckedException(Course course) throws SQLException {
    courseDao.create(course);
    throw new SQLException("Throwing exception for demoing rollback");
}
```

#### noRollback for a unchecked exception
```java
@Transactional(noRollbackFor = { SQLException.class })
public void createCourseDeclarativeWithNoRollBack(Course course) throws SQLException {
    courseDao.create(course);
    throw new SQLException("Throwing exception for demoing rollback");
}
```