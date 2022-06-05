# Basic Annotations

## `@Configuration`
- [Spring @Configuration作用](https://matthung0807.blogspot.com/2019/04/spring-configuration_28.html)

In order to Dependency Injection Configuration Class indicates that **A class declares one or more `@Bean`  methods**.   

it indicates that the class has `@Bean` definition methods. 
```java
@Configuration
public class AppConfig {

    @Bean
    public FoodService foodService() {
        return new FoodService();
    }

    @Bean
    public DrinkService drinkSerice(){
       return new drinkService
    }

}
```
- Configuration classes(classes with `@Configuration`) are processed by the **Spring Container** to generate(`new`) bean **definitions** and **server requests at run-time**

![](https://i.imgur.com/CpZdLGY.png)
- Configuration Classes register the beans in Spring Container(same as using Component Class with @ComponentScan)

- **It's recommended that class with `@Configuration` using `@ImportResource` loads xml Configuration file** for third party class that requires xml configuration files

### `@EnableAutoConfiguration`

Automatically configure Spring Application Project According to (this spring application project) JAR packages dependencies    
  > e.g. If there exists HSQLDB in Spring Application Project's class path, Programmer has no any configuration of database to connect beans，Spring will auto configure a in-memory database     

- **我們可以將`@EnableAutoConfiguration`或者`@SpringBootApplication`標註到某個`@Configuration` class**  
- 同時也可以使用`@EnableAutoConfiguration`的排除`exclude : xxx.class`不要被自動配置的屬性  

## @Bean

This is one of the most used and important spring annotation. 

It Indicates that a method be managed by the Spring container.  
```java
@Bean public object method(...)
```   

The annotation also can be used with parameters like **name, initMethod and destroyMethod.**   
- For example :: To Indicate bean method from a bean class (class Computer) in configuration Class
```java
@Configuration
public class AppConfig {
    @Bean(name = "comp", initMethod = "turnOn", destroyMethod = "turnOff")
    Computer computer(){
        return new Computer();
    }
}

//  Bean Class Computer
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

**Using `@PreDestroy` and `@PostConstruct` annotation is recommended to initMethod or destroyMethod**
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
## Layers in Spring Framework

![](https://i.imgur.com/3cmZ6Ro.png)


### @Component
- Indicates that an annotated class is a **component** considered as candidates for **auto-detection** when using annotation-based configuration and class path scanning.
- Spring only picks up and *registers* beans with `@Component` and doesn't look for `@Service` and `@Repository` in general.  
- Components are registered in **ApplicationContext** because they themselves are annotated with `@Component`
    > ![](https://i.imgur.com/ULOm9bX.png)

#### @ComponentScan
- [`@ComponentScan`](https://www.baeldung.com/spring-component-scanning)
- `@ComponentScan` annotation along with the `@Configuration` annotation to specify the packages that we want to be scanned.(it scan the class with `@configuration` annotations)
- Without arguments it tells Spring to scan the current package and all of its sub-packages.
```java
@Configuration
@ComponentScan
public class SpringComponentScanApp {
    private static ApplicationContext applicationContext;
  //...
}

// this component will be scanned
@Component
public class Dog {}
```

#### [`@Import`](https://www.baeldung.com/spring-import-annotation)
- Grouping Configurations instead of controlling dozen of configuration classes within different sources
```java
@Configuration
// Grouping 
@Import({ DogConfig.class, CatConfig.class })
class MammalConfiguration {
  // ...
}
```



### `@Controller`
To handle URL form client (like query parameters in url　...) and passes service layer to actually does task

`@ResponseBody`    
- (REST api) It tells a controller that the object returned is automatically serialized into JSON and passed back into the HttpResponse object   

`@RequestMapping(param, headers, value, method, consumers, produces )`
```java
@RequestMapping(value = "/ex/foos/{id}", method = GET)
@ResponseBody
public String getFoosBySimplePathWithPathVariable(
  @PathVariable("id") long id) {
    return "Get a specific Foo with id=" + id;
}
```
- `@RequestMapping(“/path”)` 
  - the controller will handle url path `/path/xxx` requests  
- params
  - Require request must contain certain parameters，才可以讓對應到controller's method做處理   
- headers
  - specify request must contain certain header, 才可以讓對應到controller's method做處理  
- value
  - Specify request's actual URL，URL可以是`URI Template`模式  
- method 
  - `GET`、`POST`、`PUT`、`DELETE` .. etc  
- consumes
  - Request's Content-Type e.g.`application/json`,`text/html`  
- produces
  - 指定response's returned Type，當request header中的`Accept`包含response's returned type才會返回  



`@RestController`
- equals `@ResponseBody` + `@Controller`

### @Service
- Indicates that an annotated class is at **Service Layer**.
- **This annotation serves as a specialization of `@Component`**, allowing for implementation classes to be auto-detected through class path scanning.  

### @Repository
It Indicates that an annotated class is at **Repository Layer**.

This annotation serves as a specialization of `@Component` and advisable to use with DAO classes.  

---

## Injection Dpendency Annotations

### @Autowired (IoC)

Spring `@Autowired` annotation is used for automatic injection of **beans** in the container.
- (No need to `new` an object,只需要在宣告的Object中標註`Autowired`) 
- Beans are created in `@Configuration` class and stored in spring Container (e.g. application)
- With attribute `required=false`，表示就算找不到此bean也不throws exception

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

#### `@Qualifier`
擁有多個同一類型的Bean時，可以用`@Qualifier("name = xxx ")`来指定   
`@Qualifier` specifies more details which bean should be injected, 與`@Autowired`配合使用   

### `@Resource(name="name",type="type")`
與`@Autowired`功能雷同, Default by name
---

[`@JsonManagedReference` and `@JsonBackReference`](https://stackoverflow.com/questions/31319358/jsonmanagedreference-vs-jsonbackreference)

`@RepositoryRestResourcepublic `
- 配合spring-boot-starter-data-rest使用

[`@MappedSuperClass`](https://www.baeldung.com/hibernate-inheritance#mappedsuperclass) 
- It is allow Model Class to be inherited

`@NoRepositoryBean`
- As a base class, the spring will not instantiate this class 

`@Transient`
- With this annotation it tells ORM do not map this field to database
Each field in ORM set default by `@Base`

`@JsonIgnore`
- 當Json序列化时將Bean中的一些属性忽略掉,序列化和反序列化都受影響

## Application.properties
### `@PropertySource`
- Provides a simple declarative mechanism for adding a property source to Spring’s Environment.  
- There is a similar annotation for adding an array of property source files   
    > i.e `@PropertySources`

#### `@Value`
- Inject the configurations in `application.properties`

## Security Annotations
`@EnableWebSecurity` is used with `@Configuration` class to have the Spring Security configuration defined


## Hibernate and JPA Annotations
[Hibernate Inheritance[Baeldung]](https://www.baeldung.com/hibernate-inheritance)

### `@Column`
Attribute in `@Column` 
1. `name` : field's name in database  
2. `unique`：Whether the column is a unique key. @see also `@Table`注解中的`@UniqueConstraint`  
3. `nullable` : default is `true`  
4. `insertable` :  allow this column to be inserted new value  
5. `updateable` ： allow this column to be updated
6. **`columnDefinition`: Customize the column,不交由ORM Framework創建自動表格的資料類型(The SQL fragment that is used when generating the DDL for the column)**
   - e.g. we need to manually set up the field (e.g `String date`) which datatype in database should maps to, `TIME` or `TIMESTAMP`
   - e.g. `String` in java maps to `varchar` in database by default if we need to `Blob` or `Text` then we need this attributes to specify.
7. `table`：The name of the table that contains this column.
8. `length`：length for each record of this column, records data type must be `varchar` (`length = 255 (chars)` as default)
9. `(int) precision` and `(int) scale` : Applies only if a (database's datatype) decimal column is used
    - The `precision` for a decimal(該record的數值位總長exact numeric)column.
    - The `scale` for a decimal(資料顯示到小數點第幾位) column.
      -  `double` in java maps to `double` in database : precision and scale not allow. but If our field that mapped to database's column is `double` but set up `columnDefinition`'s datatype as `decimal`, then the column which will be mapped define a datatype as `decimal` not `double`   
      - `BigDecimal` in java maps to `decimal` in database
