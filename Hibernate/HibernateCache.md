###### tags: `Hibernate`
# Hibernate Cache
[TOC]
![](https://i.imgur.com/Bl0Iffl.png)

### References 
- [Cache](http://jtechies.in/orm-tools/hibernate/hibernate-caching-strategy.php)  
- [Apache Ignite as a Hibernate second-level cache](https://bogdanstirbat.github.io/jekyll/update/2018/08/05/apache-ignite-hibernate-l2-cache.html)  
- [Code Example](https://openhome.cc/Gossip/HibernateGossip/SecondLevelCache.html)    
## Session Object
![](https://i.imgur.com/7MnFOQ2.png)    

**The Session object is a single-threaded, short-lived object**, usually associated with a session (e.g a web session is mapping a web request).         
So, there is a Session per each web request.    

## First Cache
The first level cache is associated with this object.  
**Once an item is retrieved from the Data Base, it is stored here.**  

**So if the same object is requested in the same web session in the future, it is retrieved from the first level cache and not from Data Base.**  

## Second Cache (SessionFactory level)


With Second Cache

```java

// ... Initialize a factory ...
// ....

/**
  * <p> Our Session Factory creates A session object 
  *     for query user whose id is 1
  */
Session session = sessionFactory.openSession();

// do query 
User user1 = (User) session.load(User.class, new Integer(1));
user1.getName();  

session.close();

/**
  * <p> Here is where the second cache works
  *     because we have already a same query
  *     so this time we don't retrieve data from database
  *     instead we get it from cache </p>
  */
session = sessionFactory.openSession();

User user2 = (User) session.load(User.class, new Integer(1));
user2.getName(); // fetch from cache not database

session.close();  
```
- **Second Cache被同一個`SessionFactory`所建立的Session實例所共享，所以即使關閉了Session，下一個Session仍可使用二級快取**，在查詢時，Session會先在Session level快取中查詢看有無資料，如果沒有就試著從二級快取中查詢資料，查到資料的話就直接返回該筆資料，所以在上例中，第二次無需再向資料庫進行SQL查詢。

- 如果打算清除二級快取的資料，可以使用SessionFactory的`evict()`方法
`sessionFactory.evict(User.class, user.getId());`

- Second level cache is an **optional cache** and **first-level cache will always be consulted before any attempt is made to locate an object in the second-level cache.**
    > check first cache if not found then check second cache 
- **The second-level cache can be configured on a per-class and per-collection basis** and mainly responsible for caching objects across sessions.
- Any third-party cache can be used with Hibernate.  
