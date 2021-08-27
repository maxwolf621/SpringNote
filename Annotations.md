# [Annotations](https://blog.csdn.net/weixin_40753536/article/details/81285046?utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control)


- `@SpringBootApplication`
   >　equals `@Configuration` ,`@EnableAutoConfiguration` and `@ComponentScan`  
   >  `@ComponentScan`：spring Boot scans components and add them to Configuration Class to depoly them to our spring project programming context。

- `@ResponseBody` (used by method)
  > 表示Method的returned value contained in HTTP response body，一般在异步获取数据时使用，用于构建RESTful的api。在使用`@RequestMapping`后，返回值通常解析为跳转路径，加上@Responsebody后返回结果不会被解析为跳转路径，而是直接写入HTTP response body中。比如异步获取json数据，加上@Responsebody后，会直接返回json数据。该注解一般会配合@RequestMapping一起使用。

- `@Controller`：To handle URL form client (like query paramaters in url　...) and passes service layer to acutally does task
- `@RestController`： Operates Component(如struts中的action) in Constoller, equals `@ResponseBody` + `@Controller`

- `@RequestMapping`： 映射(MAP) URL's query paramater與Controller Method's paramater

- `@EnableAutoConfiguration`：SpringBoot's auto-configuration
   > (自動配置) Automatically configure Spring Application Project According to (this spring application project) JAR packages dependencies    
   > e.g. If there exists HSQLDB in Spring Application Project's classpath, Programmer has no any configuration of database to connect beans，Spring will auto configure a in-memory database     
   > 我們可以將`@EnableAutoConfiguration`或者`@SpringBootApplication`標註到某個`@Configuration` class  
   > 同時也可以使用`@EnableAutoConfiguration`的排除(exclude)不要被自動配置的屬性  

- [`@ComponentScan`](https://www.baeldung.com/spring-component-scanning)
  > we use the `@ComponentScan` annotation along with the `@Configuration` annotation to specify the packages that we want to be scanned. 
  > `@ComponentScan` without arguments tells Spring to scan the current package and all of its sub-packages.

- [`@Configuration`](https://www.baeldung.com/configuration-properties-in-spring-boot)：同等於Spring MVC上的`XML` Configuration Files
   > **It's recomandded that class with `@Configuration` using `@ImportResource` loads xml Configuration file** for thrid party class that requires xml configuration files

- `@Import`：import ohter configuration files(class)

- `@Autowired` : Inject the beans (不要再`new`一個物件,只需要在宣告的Object中標註`Autowired`)
   > `@Autowired`：自动导入依赖的bean。byType方式。把配置好的Bean拿来用，完成属性、方法的Auto Dependency，它可以对class's field、methods及constructors進行標註藉此完成Depedency Injections的工作  
   > with attribute `required=false`，表示就算找不到此bean也不throws exception
   
- `@Service` : component at service layer
- `@Repository`： `@Repository` annotates classes (`DAO`, `POJO` ...) at the persistence layer, which will act as a database repository.(to interact with database)
- `@Value`：Inject the configurations in `application.properties`
- `@Inject`：`@Autowired` with no attirbute `required`

- [`@Component`](https://www.baeldung.com/spring-component-repository-service) : Components無法classify到server, controller Layers時候,使用該註解

- `@Qualifier`：擁有多个同一類型的Bean時，可以用`@Qualifier("name = xxx ")`来指定
  > 與`@Autowired`配合使用   
  > `@Qualifier` specifies more details which beans should be injected 

- `@Resource(name="name",type="type")`：没有括号内内容的话，默认byName
  > 與`@Autowired`功能雷同


- [`@JsonManagedReference` and `@JsonBackReference`](https://stackoverflow.com/questions/31319358/jsonmanagedreference-vs-jsonbackreference)
- `@RepositoryRestResourcepublic`：配合spring-boot-starter-data-rest使用。
- [`@MappedSuperClass`](https://www.baeldung.com/hibernate-inheritance#mappedsuperclass) : Model Class that allows to be inherited 
- `@NoRepositoryBean`: As a base class, the spring will not instantialize this class 
- `@Transient`：表示该属性并非一个到数据库表的字段的映射,ORM框架将忽略该属性。如果一个属性并非数据库表的字段映射,就务必将其标示为@Transient,否则,ORM框架默认其注解为@Basic。
- `@JsonIgnore`：作用是json序列化时将Java bean中的一些属性忽略掉,序列化和反序列化都受影响。


`@RequestMapping(param, headers, value, method, consumers,produces`：
>  e.g `@RequestMapping(“/path”)`表示该控制器处理所有`/path`的URL requests  
>  RequestMapping是一个用来处理请求地址映射的注解，可用于类或方法上,用于类上，表示类中的所有响应请求的方法都是以this url as base 

- params: Require request must contain certain paramaters，才可以讓對應到controller's method做處理   
- headers: Specify request must contain certain header, 才可以讓對應到controller's method做處理  
- value:   Specify request's actual URL，URL可以是`URI Template`模式  
- method: `GET`、`POST`、`PUT`、`DELETE` .. etc  
- consumes: request's Content-Type e.g.`application/json`,`text/html`  
- produces: 指定reponse's returned Type，當request header中的`Accept`包含response's returned type才會返回  


[Hibernate Annotations](https://www.baeldung.com/hibernate-inheritance)

- Attributes `@Column` 
  > `name` : field's name in database  
  > `unique`：Whether the column is a unique key. @see also `@Table`注解中的`@UniqueConstraint`  
  > `nullable` : default is `true`  
  > `insertable` :  allow this column to be inserted new value  
  > `updateable` ： allow this column to be updated
  > `columnDefinition`: customize the column, 不交由ORM Framework創建自動表格的資料類型(The SQL fragment that is used when generating the DDL for the column)
    - e.g. we need to manually set up the field (e.g `String date`) which datatype in database should maps to, `TIME` or `TIMESTAMP`
    - e.g. `String` in java maps to `Varchar` in database by deafult if we need to `Blob` or `Text` then we need this attibutes to specify
  > `table`：The name of the table that contains the column.
  > `length`：length for each record of this column, reocrds data type must be `varchar` (default lengt = 255 (chars))
  > `(int) precision` and `(int) scale` : Applies only if a (database's datatype) decimal column  is used
    - The precision for a decimal(該record的數值位總長) (exact numeric) column.
    - The scale for a decimal(資料顯示到小數點第幾位) (exact numeric) column.
    1.`double` in java maps to `double` in database : precision,scale not allow. but If our fieled that mapped to database's column is `double` but set up `columnDefinition`'s datatype as `decimal`, then the colun which will be mapped define a datatype as `decimal` not `double`   
    2.`BigDecimal` in java maps to `decimal` in database
