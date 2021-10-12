# Annotation

- `@SpringBootApplication`
   > equals `@Configuration` , `@EnableAutoConfiguration` and `@ComponentScan`    
   >  `@ComponentScan`：spring Boot scans components and add them to Configuration Class to deploy them to our spring project programming context。

- `@Controller`：To handle URL form client (like query parameters in url　...) and passes service layer to actually does task

- `@RestController`： Operates Component(如struts中的action) in Controller, equals `@ResponseBody` + `@Controller`

- `@ResponseBody` (used by method)
  > 表示Method的returned value will be contained in HTTP response body，一般在异步获取数据时使用，用于构建RESTful的api。在使用`@RequestMapping`后，返回值通常解析为跳转路径，加上@ResponseBody后返回结果不会被解析为跳转路径，而是直接写入HTTP response body中。比如异步获取json数据，加上@ResponseBody后，会直接返回json数据。该注解一般会配合@RequestMapping一起使用。

- `@RequestMapping`： 映射(MAP) URL's query parameter to Controller Method's parameter

- `@EnableAutoConfiguration`：SpringBoot's auto-configuration
   - (自動配置) Automatically configure Spring Application Project According to (this spring application project) JAR packages dependencies    
      > e.g. If there exists HSQLDB in Spring Application Project's class path, Programmer has no any configuration of database to connect beans，Spring will auto configure a in-memory database     
   > **我們可以將`@EnableAutoConfiguration`或者`@SpringBootApplication`標註到某個`@Configuration` class**  
   > 同時也可以使用`@EnableAutoConfiguration`的排除(`exclude` property)不要被自動配置的屬性  

- [`@ComponentScan`](https://www.baeldung.com/spring-component-scanning)
  > we use the `@ComponentScan` annotation along with the `@Configuration` annotation to specify the packages that we want to be scanned. 
  > `@ComponentScan` without arguments tells Spring to scan the current package and all of its sub-packages.

- [`@Configuration`](https://www.baeldung.com/configuration-properties-in-spring-boot)：同等於Spring MVC上的`XML` Configuration Files
   > **It's recommended that class with `@Configuration` using `@ImportResource` loads xml Configuration file** for third party class that requires xml configuration files

- `@Import`：import other configuration files(class)

- `@Autowired` : Inject the beans (不需要再`new`一個物件,只需要在宣告的Object中標註`Autowired`就會進行自動注入)
   > `@Autowired`：自動注入配置好的的bean。    
   > with attribute `required=false`，表示就算找不到此bean也不throws exception
   
- `@Service` : component at service layer
- `@Repository`： `@Repository` annotates classes (`DAO`, `POJO` ...) at the persistence layer, which will act as a database repository.(to interact with database)
- `@Value`：Inject the configurations in `application.properties`

- `@Inject`：Same as `@Autowired` but without `required` property

- [`@Component`](https://www.baeldung.com/spring-component-repository-service) : Components無法classify到server, controller Layers時候,使用該註解

- `@Qualifier`：擁有多個同一類型的Bean時，可以用`@Qualifier("name = xxx ")`来指定
  > 1. 與`@Autowired`配合使用   
  > 2. `@Qualifier` specifies more details which bean should be injected 

- `@Resource(name="name",type="type")`
  > 與`@Autowired`功能雷同
  > Default by name

- [`@JsonManagedReference` and `@JsonBackReference`](https://stackoverflow.com/questions/31319358/jsonmanagedreference-vs-jsonbackreference)

- `@RepositoryRestResourcepublic `：配合spring-boot-starter-data-rest使用

- [`@MappedSuperClass`](https://www.baeldung.com/hibernate-inheritance#mappedsuperclass) : Model Class that allows to be inherited

- `@NoRepositoryBean`: As a base class, the spring will not instantiate this class 

- `@Transient`：each field in ORM default by `@Base`, with this annotation it tells ORM do not map this field to database

- `@JsonIgnore`：作用是json序列化时将Java bean中的一些属性忽略掉,序列化和反序列化都受影响。

- `@RequestMapping(param, headers, value, method, consumers, produces )`
  > e.g `@RequestMapping(“/path”)` means the controller will handle all form url `/path`'s requests  
  - params: Require request must contain certain parameters，才可以讓對應到controller's method做處理   
  - headers: Specify request must contain certain header, 才可以讓對應到controller's method做處理  
  - value:   Specify request's actual URL，URL可以是`URI Template`模式  
  - method: `GET`、`POST`、`PUT`、`DELETE` .. etc  
  - consumes: request's Content-Type e.g.`application/json`,`text/html`  
  - produces: 指定response's returned Type，當request header中的`Accept`包含response's returned type才會返回  


[Hibernate and JPA Annotations](https://www.baeldung.com/hibernate-inheritance)

- Attributes `@Column` 
  > `name` : field's name in database  
  > `unique`：Whether the column is a unique key. @see also `@Table`注解中的`@UniqueConstraint`  
  > `nullable` : default is `true`  
  > `insertable` :  allow this column to be inserted new value  
  > `updateable` ： allow this column to be updated
  > **`columnDefinition`: customize the column, 不交由ORM Framework創建自動表格的資料類型(The SQL fragment that is used when generating the DDL for the column)**
    - e.g. we need to manually set up the field (e.g `String date`) which datatype in database should maps to, `TIME` or `TIMESTAMP`
    - e.g. `String` in java maps to `varchar` in database by default if we need to `Blob` or `Text` then we need this attributes to specify.
  > `table`：The name of the table that contains the column.
  > `length`：length for each record of this column, records data type must be `varchar` (default length = 255 (chars))
  > `(int) precision` and `(int) scale` : Applies only if a (database's datatype) decimal column  is used
    - The precision for a decimal(該record的數值位總長) (exact numeric) column.
    - The scale for a decimal(資料顯示到小數點第幾位) (exact numeric) column.
    1.`double` in java maps to `double` in database : precision,scale not allow. but If our field that mapped to database's column is `double` but set up `columnDefinition`'s datatype as `decimal`, then the column which will be mapped define a datatype as `decimal` not `double`   
    2.`BigDecimal` in java maps to `decimal` in database





[REF](https://www.journaldev.com/16966/spring-annotations)

`@Configuration`


In order to Dependency Injection Configuration Class indicates that **A class declares one or more `@Bean` methods**.


These Configuration classes are processed by the **Spring Container** to generate(`new`) bean **definitions** and **server requests at run-time**

![](https://i.imgur.com/CpZdLGY.png)

#### @Bean
- Indicates that a method(`@Bean public object method(...)`) produces a bean to be managed by the Spring container.  
- This is one of the most used and important spring annotation.   
- It also can be used with parameters like **name, initMethod and destroyMethod.**  

For example  :: To Indicate bean method from a bean class (class Computer) in configuration Class
```java
@Configuration
public class AppConfig {
    @Bean(name = "comp", initMethod = "turnOn", destroyMethod = "turnOff")
    Computer computer(){
        return new Computer();
    }
}
```
Bean Class Computer
```java
public class Computer {
/* define Init and destroy Method */
    public void turnOn(){
       //..
    }
    public void turnOff(){
        //...
    }
}
```

There are also other way for parameters `initMethod` and `destroyMethod`
Which are `@PreDestroy` and `@PostConstruct`

```java
 public class Computer {
    @PostConstruct
    public void turnOn(){
        //..
    }

    @PreDestroy
    public void turnOff(){
        //..
    }
}
```
- Second Approach is recommended


#### Layers in Spring Framework

![](https://i.imgur.com/3cmZ6Ro.png)

#### @Component
- Indicates that an annotated class is a **component**.   
- Such classes are considered as candidates for **auto-detection** when using annotation-based configuration and class path scanning.
- Spring only picks up and *registers* beans with `@Component` and doesn't look for `@Service` and `@Repository` in general.  
- Components are registered in **ApplicationContext** because they themselves are annotated with `@Component`
    > ![](https://i.imgur.com/ULOm9bX.png)

#### @ComponentScan
- Configures component scanning directives for use with  Configuration classes.  
- Here we can specify the base packages to scan for spring components.  

#### @Service
- Indicates that an annotated class is at **Service Layer**.
- **This annotation serves as a specialization of `@Component`**, allowing for implementation classes to be autodetected through classpath scanning.  

#### @Repository
- Indicates that an annotated class is at **Repository Layer**.  
- This annotation serves as a specialization of `@Component` and advisable to use with DAO classes.  

#### @Autowired (IoC)

Spring `@Autowired` annotation is used for automatic injection of **beans** in the container.   

bean are created in `@Configuration` class and stored in spring Container (e.g. application)

```java
// normally without annotation
public class Person{
    //..
}
public class UsePerson{
    private Person person;
    
    public UsePerson(Person person_1){
        this.person = new Person();
        this.person = person_1;
        
    }
}
// with annotation
public class UsePerson{
    @Autowired
    private Person person;
    pubic UsePerson(Person person_1){
        this.person = person_1;
    }
}
```
#### @PropertySource
- Provides a simple declarative mechanism for adding a property source to Spring’s Environment.  
- There is a similar annotation for adding an array of property source files   
    > i.e `@PropertySources`
## Security Annotations
`@EnableWebSecurity` is used with `@Configuration` class to have the Spring Security configuration defined


