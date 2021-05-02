###### tags: `Hibernate`
# Hibernate Session Operation
[TOC]

### Understand <ins>[Note For Session Factory](/3xYG4oxDQHq9u3BHlL8qsg) and  [Note For Transaction](/gGyAt8vwRfqfUTh0akIv6g)</ins> Before reading this note

![](https://i.imgur.com/wUFs3bS.png)

**Hibernate is a popular ==Object Relational Mapping (ORM)== framework that aims at simplifying database programming for developers**

### Session Factory `org.hibernate.SessionFactory`

We can say it's `a factory of session` and `client of Connection Provider`

> Session Factory interface provides factory method to get the object of Session
> ![](https://i.imgur.com/MYDYdd1.png)


## getCurrentSession method

> **Hibernate Session Object are not thread safe, so we should use `getCurrentSession()` in multi-threaded environment**

Before using `SessionFacetory.getCurrentSession()`
We need to configure it in hibernate configuration file
```xml 
<property name="hibernate.current_session_context_class">thread</property>
```

```java
Session currentSession = sessionFactory.getCurrentSession();
//...
```

## openSession method
```java
Session newSession = sessionFactory.openSession();
//...
newSession.close()
```
Thie method always opens a new session.
Remeber to close the session while we are done with all the database operations

For web application frameworks(**mutlti-threaded environment**), we can choose **to open a new session for each request or for each session based on the requirement**

## openStatelessSession method
```java
StatlessSession statelesssession = sessionFactory.openStatlessFactory()
//...
statelesssession.close();
```
The Operation performed through a statless session **bypass Hibernate's event model and interceptor.**
> An instance of StatelessSession does not implement first-level cache and does not interact with any second-level cache  
> - This is good fit in certain situation for loading bulk data into database and **to avoid hibernate session holding huge data in first-level cache memory**. 

We can also use object of` java.sql.Connection` to get a stateless object from hibernate.

## The Session get and load methods

Hibernate Session provide different methods to Fetch Data from DataBase.

Most used are get() and load()
```java
// Consider we get Class employee
public class ex{
  public static void mian(String[] args){
    SessionFactory sessionfactory = HibernateUtil.getSessionFactory();
    Session session = sessionFactory.getSessionFactory();
    Transaction tx = session.beginTransaction();
    
    // using get()
    Employee empG = (Employee) session.get(Employee.class, new Long(2));
    Employee empL = (Employee) session.load(Employee.class,new Long(1));
    
    tx.commit();
    sessionFactory.clsoe();
  }
}
```

- get method 
    > loads the data as soon as it’s called 

- load method 
    > returns a **proxy object** and loads data only when it’s actually required, so load() is better because it support lazy loading.

:::danger  
**load** method throws exception when data is not found
- Use it only when we know the Data we want to fetch is existing.  

Use **get** method when we want to make sure data exists in the database.  
:::   

### [More Details of Get And Load](https://www.tutorialspoint.com/difference-between-get-and-load-in-hibernate#:~:text=In%20hibernate%2C%20get()%20and,throws%20object%20not%20found%20exception.)

## Save Method

It can be used to save entity to database and can be invoked outside a transaction

1. **We should avoid save outside transaction boundary(No exception throwing)**, otherwise mapped entities will not be saved causing data inconsistency. 

- `save()` returns the generated id immediately, this is possible because primary object is saved as soon as save method is invoked.

- If there are other objects mapped from the primary object, they get saved at the time of committing transaction or when we flush the session.

- For objects that are in persistent state, save updates the data through update query. Notice that it happens when transaction is committed. 
    > If there are no changes in the object, there wont be any query fired. 


- **Hibernate save load entity object to persistent context**, if you will update the object properties after the save call but before the transaction is committed, it will be saved into database.


