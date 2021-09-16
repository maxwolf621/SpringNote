###### tags: `Hibernate`
  
[hibernate-dynamic-configuration](https://www.codejava.net/frameworks/hibernate/hibernate-programmatic-configuration-example)   
# Hibernate Configuration (Session Factory and Session)

What is Hibernate?   
**Hibernate is a popular Object Relational Mapping (ORM) framework that aims at simplifying database programming for developers**

### Review of [`Session`](HibernateSession.md)
- `SessionFactory` is a factory class for Session objects. 
  > It is available for the whole application while a Session is only available for particular transaction.**
- `Session` is short-lived while `SessionFactory` objects are long-lived.
- `SessionFactory` provides a second level cache and `Session` provides a first level cache.
  > ![](https://i.imgur.com/Hx1qzrX.png)

## XML configuration for Hibernate ( `hibernate.cfg.xml`)
[More Details](https://www.tutorialspoint.com/hibernate/hibernate_configuration.htm)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
		"-//Hibernate/Hibernate Configuration DTD 3.0//EN"
		"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">com.mysql.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/hibernate</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
        <property name="hibernate.connection.characterEncoding">utf-8</property>
         <!-- 指定连接池最大连接数 -->
        <property name="hibernate.c3p0.max_size">20</property>
        <!-- 指定连接池最小连接数 -->
        <property name="hibernate.c3p0.min_size">1</property>
        <!-- 指定连接池里连接超时时长 -->
        <property name="hibernate.c3p0.timeout">5000</property>
        <!-- 指定连接池里做大缓存多少个Statement对象 -->
        <property name="hibernate.c3p0.max_statements">50</property>
        <!-- 是否根据需要自动建表 -->
        <property name="hbm2ddl.auto">update</property>
        <!-- 是否显示sql语句 -->
        <property name="show_sql">true</property>
        <!-- 将SQL脚本进行格式化后再输出 -->
        <property name="hibernate.format_sql">true</property>
        <!-- 设置连接数据库所使用的方言 -->
        <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
        <!-- 罗列所有持久化类名 -->   
        <mapping class="com.mao.PersonMap"/> 
       
    </session-factory>
</hibernate-configuration>
```


## Before Hibernate 4.x

![](https://i.imgur.com/VxKsEk9.png)
- Configure a object of `hibernate.cfg.xml` using `Configuration()`


To Build A Session Factory
```java
SessionFactory sessionFactory =
    new Configuration().configure().buildSessionFactory();
```

Pull out a session from A Session Factory
```java
Session session = sessionFactory.openSession();
```

## After Hibernate 4.x

It first loads mapping and properties from the convention file `hibernate.cfg.xml`

- `ServiceRegistryBuilder` has been Deprecated instead we use `StandardServiceRegistryBuilder`


#### Configuration
```java
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

// Create Configuration 
Configuration configuration = new Configuration().configure();

// Register Service
StandardServiceRegistryBuilder registry = new StandardServiceRegistryBuilder();

// Register Service apply hibernate setting
registry.applySettings(configuration.getProperties()).build();
```


#### Build the Session Factory and pull out a Session from Factory
```java
import org.hibernate.SessionFactory;
// Build A Factory
SessionFactory sessionFactory =   configuration.buildSessionFactory(serviceRegistry);

// Create A session
Session session = sessionFactory.openSession();
```

## HibernateUtil 

We can create a utility class called (hibernateUtil) for build the Session Factory
```java
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
 
public class HibernateUtil {
    private static SessionFactory sessionFactory;
     
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {

            // loads configuration and mappings
            Configuration configuration = new Configuration().configure();
            
            // Register Service
            ServiceRegistry serviceRegistry = 
                            new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
             
            // builds a session factory from the service registry
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);           
        }
         
        return sessionFactory;
    }
}
```


Import our HibernateUtil
```java
// import HibernateUtil;
//...

// build a factory for Sessions
SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

// pull out a session from factory
Session session = sessionFactory.openSession();

session.beginTransaction(); // begin Transaction
//  do Operations ..
session.getTransaction().commit
session.close();
```


# Run-Time Hibernate Configuration


**To Configure *database connection* for a hibernate in a programmatic Way**
- Programmatic Configuration is useful, while updating the connection information at run-time instead of using XML in `hibernate.cfg.xml`  


Basic Configuration in  `hibernate.cfg.xml`
```xml
<hibernate-configuration>
<session-factory>
    <!-- Database connection settings -->
    
    <property name="connection.driver_class">com.mysql.jdbc.Driver</property>
    <property name="connection.url">jdbc:mysql://localhost:3306/newsdb</property>
    <property name="connection.username">root</property>
    <property name="connection.password">secret</property>
    <property name="dialect">org.hibernate.dialect.MySQLDialect</property>
    <property name="show_sql">true</property>

    <mapping class="com.aaa.bbb.User"/>
</session-factory>
</hibernate-configuration>
```

The Following java method help us change these properties in `hibernate.cfg.xml`

### (RUN TIME) Create Database Configuration via JAVA class `Configuration`

We have to new a instance that does the Configuration

Creating database connection configuration 
```java
Configuration config = new Configuration();
```


#### `setProperty` method
Set Property dynamically instead of using XML configuration
```java
config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/newsdb");
config.setProperty("hibernate.connection.username", "root");
config.setProperty("hibernate.connection.password", "password");
config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
```
#### `addAnnotatedClass` method

To use `model1.class`, `model2.class` ...
```java
config.addAnnotatedClass(Model1.class);
config.addAnnotatedClass(Model2.class);
```

#### `addResource` and `addClass` methods

To Specify(to find) the file `xml` and `class`

For example :: `hbm.xml` or `User.class`
```java
// Specify XML Mapping File
config.addResource("com/aaa/bbb/User.hbm.xml")

// Tell Hibernate to find Class Mapping File
config.addClass(com.aaa.bbb.User.class);
```

## Builder pattern for our hibernate.properties

```java
Configuration config = new Configuration().addClass(model.class)
                                          .setProperty(/*...*/)
                                          .setProperty(/*...*/);
```

## Example for Build the Database Connection Dynamically

[Session Factory From Service Registry and Configuration](/3xYG4oxDQHq9u3BHlL8qsg)


Example 1, dynamically configure our (Database) hibernate configuration via JAVA
```java
public static SessionFactory getSessionFactory(String databaseName) {
    Configuration config = new Configuration();
    
    config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
    config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/" + databaseName);
    config.setProperty("hibernate.connection.username", "root");
    config.setProperty("hibernate.connection.password", "password");
    config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
     
    config.addAnnotatedClass(Users.class);
    
    // Create Factory
    SessionFactory sessionFactory = config.buildSessionFactory();
     
    return sessionFactory;
}
```

Create A Session ( POJO -> Persistence -> Database)

Assume we have a entity call Custom,  and now we add a new Custom to our Database via hibernate
```java
public class HibernateDemo1 {
	@Test
	public void demo1() {
		
        // load Hibernate Configuration 
		Configuration configuration = new Configuration().configure();
		
        
        // Manually load up configuration file
		//configuration.addResource("com/PROJECTNAME/hibernate/demo01/Customer.hbm.xml");
		
		// Build Factory (related JDBC pool)
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		
		// Create A Session Object 
        // via sessionFactory (related to JDBC中的Connection)
		Session session = sessionFactory.openSession();
		
		// Start A `transaction`
		Transaction transaction = session.beginTransaction();
		
		//5 Set up Transaction content
		Customer customer = new Customer();
		customer.setCust_name("John Mayer");
		
        // Save it in Session
		session.save(customer);
		
        // Commit transaction to Database
		transaction.commit();
		
        // Close
		session.close();
		sessionFactory.close();
	}
}
```