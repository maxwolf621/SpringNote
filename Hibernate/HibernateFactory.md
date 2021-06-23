###### tags: `Hibernate`

# Session Factory 
[TOC]

**SessionFactory is a factory class for Session objects. 
It is available for the whole application while a Session is only available for particular transaction.**

==Session is short-lived while SessionFactory objects are long-lived.==

> SessionFactory provides a second level cache and Session provides a first level cache.
>![](https://i.imgur.com/Hx1qzrX.png)


## Related Note and Reference

> ### [Note for Programmatic (Run Time) Configuration](/NpuUFawzQcemYBVwHEzN_A)
> 
> ### [Reference](https://www.codejava.net/frameworks/hibernate/hibernate-programmatic-configuration-example)


## `hibernate.cfg.xml`

### [More Details](https://www.tutorialspoint.com/hibernate/hibernate_configuration.htm)

```xml=
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

————————————————
版权声明：本文为CSDN博主「VipMao」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/VipMao/article/details/51340525
```


## Before Hibernate 4.x

>![](https://i.imgur.com/VxKsEk9.png)
> Configure a object of `hibernate.cfg.xml` using Configuration()


To Build A Session Factory
```java=
SessionFactory sessionFactory =
    new Configuration().configure().buildSessionFactory();
```

Pull out a session from A Session Factory
```java=
Session session = sessionFactory.openSession();
```

## After Hibernate 4.x

It first loads mapping and properties from the convention file `hibernate.cfg.xml`

> `ServiceRegistryBulder` has been Deprecated instead we use `StandardServiceRegistryBuilder`

```java=
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
// Create Configuration 
Configuration configuration = 
    new Configuration().configure();
// Register Service
StandardServiceRegistryBuilder registry = 
    new StandardServiceRegistryBuilder();
registry.applySettings(configuration.getProperties()).build();
```


> Build the Session Factory and pull out a Session from Factory
```java=
import org.hibernate.SessionFactory;
// Build A Factory
SessionFactory sessionFactory = 
        configuration.buildSessionFactory(serviceRegistry);
// Create A session
Session session = sessionFactory.openSession();
```

## HibernateUtil

We can create a utility class for build the Session Factory
```java=
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



import It while building the session
```java=
import HibernateUtil;
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


<style>

html,
body, 
.ui-content,
/*Section*/
.ui-toc-dropdown{
    background-color: #1B2631;
    color: #9BCBFC;
}

body > .ui-infobar {
    display: none;
}
.ui-view-area > .ui-infobar {
    display: block ;
    color: #5D6D7E ;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body blockquote{	
    /*#7FFFD4*/
    /*#59FFFF*/
    color: #7FFFD4;
}

/* > */
.markdown-body blockquote {
color: #9BCBFC ;
border-left-color: #B22222 ;
font-size: 16px;
}

.markdown-body h6{
    color: gold;
}
.markdown-body h1,
.markdown-body h2 {
    border-bottom-color: #5D6D7E;
    border-bottom-style: ;
    border-bottom-width: 3px;
}

.markdown-body h1 .octicon-link,
.markdown-body h2 .octicon-link,
.markdown-body h3 .octicon-link,
.markdown-body h4 .octicon-link,
.markdown-body h5 .octicon-link,
.markdown-body h6 .octicon-link {
    color: yellow;
}

.markdown-body img {
    background-color: transparent;
}

.ui-toc-dropdown .nav>.active:focus>a, .ui-toc-dropdown .nav>.active:hover>a, .ui-toc-dropdown .nav>.active>a {
    color: gold;
    border-left: 2px solid white;
}

.expand-toggle:hover, 
.expand-toggle:focus, 
.back-to-top:hover, 
.back-to-top:focus, 
.go-to-bottom:hover, 
.go-to-bottom:focus {
    color: gold;
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
    color: #2C3E50;
    }



/* comment*/
.ui-comment-container .ui-comment-header {
background-color: #2a2a2a;
color: #eee;
border-color: #6d6d6d;
}

.ui-comment-container {
background-color: #2e2e2e;
border-color: #6d6d6d;
}

.ui-comment-container .ui-comments-container .ui-comment .comment-author {
color: #eee;
}

.ui-comment-container .ui-comments-container .ui-comment .timestamp {
color: #aaa;
}

.ui-comment-container .ui-comments-container .ui-comment .comment-menu .comment-dropdown-menu {
background: #222;
color: #eee;
border-color: #555;
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

/* `` */
.markdown-body code,
.markdown-body tt {
    color: #eee;
    background-color: #424a55;
}

/* ``` ``` */
.markdown-body pre {
background-color: #eeee;
border: 1px solid !important;
  color: #dfdfdf;
}

/* table of contents block*/
.ui-toc-dropdown {
width: 42vw;
max-height: 90vh;
overflow: auto;
text-align: inherit;
}

/* table of contents text*/
.ui-toc-dropdown .nav>li>a {
font-size: 14px;
font-weight: bold;
color: #ddd;
}

/* table of contents text: active*/
.ui-toc-dropdown .nav>.active:focus>a,
.ui-toc-dropdown .nav>.active:hover>a,
.ui-toc-dropdown .nav>.active>a {
color: #7bf;
border-left-color: #7bf;
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

