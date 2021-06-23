###### tags: `Hibernate`
# Hibernate Cache
[TOC]
## References 
[Cache](http://jtechies.in/orm-tools/hibernate/hibernate-caching-strategy.php)
[Apache Ignite as a Hibernate second-level cache](https://bogdanstirbat.github.io/jekyll/update/2018/08/05/apache-ignite-hibernate-l2-cache.html)
[Code Example](https://openhome.cc/Gossip/HibernateGossip/SecondLevelCache.html)

![](https://i.imgur.com/Bl0Iffl.png)

## Session Object
![](https://i.imgur.com/7MnFOQ2.png)

The Session object is ==**a single-threaded, short-lived object**==, usually associated with a session (for example a web session, mapping a web request).  
So, there is a Session per each web request. 

## First Cache
The first level cache is associated with this object.  
Once a item is retrieved from the DataBase, it is stored here.  

Next time the same object is requested in the same web session, it is retrieved from the first level cache and not from DataBase.  

## Second Cache (SessionFactory level)
If a different web request arrives, or the same web web request arrives later, database will be called again. 
==To prevent this behavior, the concept of second level cache was born.==

Without Second Cache
```java=
/* A Session factory betta... */

// factory betta creates A session object
Session session = sessionFactory.openSession();
User user1 = (User) session.load(User.class, new Integer(1));
user1.getName();
session.close();

// betta creates a session object AGAIN
//     Access database AGAIN
session = sessionFactory.openSession();
User user2 = (User) session.load(User.class, new Integer(1));
user2.getName();
session.close();  
```

> With Second Cache
**Second Cache被同一個SessionFactory所建立的Session實例所共享，所以即使關閉了Session，下一個Session仍可使用二級快取**，在查詢時，Session會先在Session level快取中查詢看有無資料，如果沒有就試著從二級快取中查詢資料，查到資料的話就直接返回該筆資料，所以在上例中，第二次無需再向資料庫進行SQL查詢。

如果打算清除二級快取的資料，可以使用SessionFactory的evict()方法
`sessionFactory.evict(User.class, user.getId());`

- Second level cache is an **optional cache** and ***first-level cache will always be consulted before any attempt is made to locate an object in the second-level cache.*** 
    > Query first cache if not found then second cache 
- **The second-level cache can be configured on a per-class and per-collection basis** and mainly responsible for caching objects across sessions.

:::success  
Any third-party cache can be used with Hibernate.  
:::  