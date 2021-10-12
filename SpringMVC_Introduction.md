# Introudction of Spring MVC

###### tags: `MVC`

[REF](https://www.codejava.net/frameworks/spring/spring-mvc-beginner-tutorial-with-spring-tool-suite-ide#AboutSpringMVC)

## What is MVC

The MVC framework is based on the **Model - View - Controller (MVC)** design pattern which separates the application’s logic into the three layers Mode, View and Controller

![](https://i.imgur.com/amuWjEq.png)

>Spring’s dispatcher Servlet ==(Waiter)==
> : Acts as a ==front controller== between the Spring application and its clients. 
> : **The dispatcher Servlet intercepts all requests coming to the application and consults the Handler Mapping for which controller to be invoked** to handle the requests.
>> Handler Mapping ==(Meal)==
>> : It is responsible to find appropriate controllers that handle specific requests. 
>> The mapping between request URLs and controller classes is done via XML configuration or annotations.
>> 
>> Controller ==(Cook)==
>> : It is responsible **to process the requests by calling other business/service classes.** 
>> The output can be attached to model objects which will be sent (**respond**) to the view (displayed attributes by `Model` class). 
>> : To know which view(web pages) will be rendered, the controller consults the View Resolver.
>> 
>> View Resolver ==(Where to get Guest Order)==
>> : **Finds the physical(in the directory) view files** .
>> 
>> View ==(Guest Order)==
>> : The physical view files (.jsp, .html, ,xml, Velocity template, etc...)  


![](https://i.imgur.com/aoh0v6b.png)![Uploading file..._mzwpdiogu]()


### [Note Dispatcher Servlet](/3Mhn1IeiT8uFrsZU0Ln0bg)


# A basic MVC project 

Would need 
1. Maven Depedencies Configuration (poem.xml)
2. Web depolyment descriptor (web.xml)
3. Controller classes
4. views (web pages)


## Maven Dependencies Configuration XML file 
```xml=
<!-- Context path of the web application-->
<groupId>net.codejava</groupId>
<artifactId>springmvc</artifactId>
...
<groupId>org.springframework</groupId>
<artifactId>spring-context</artifactId>
<version>${org.springframework-version}</version>
...
<groupId>org.springframework</groupId>
<artifactId>spring-webmvc</artifactId>
<version>${org.springframework-version}</version>
```
> value of <artifactId> element </artifactId> 
> : is used as **context path of web application** deploying the project on a server running within the IDE

## 2. Web deployment descriptor File (Handle Mapping)

Web deployment descriptor is the typical configuration for a Spring MVC-based application with declaration for Spring’s `ContextLoaderListener` and
`DispatcherServlet` along with the Spring configuration files 

**It specifies the URL mapping for Spring’s `DispatcherServlet` to handle all request**


For Configuring resolver we need these classes
- `org.springframework.web.servlet.DispatcherServlet`
- `org.springframework.web.WebApplicationInitializer`
### XML based Configuration (web.xml)
`org.springframework.web.servlet.DispatcherServlet`
```xml=
 <servlet>
   <servlet-name>dispatcher</servlet-name>
   <servlet-class>
     org.springframework.web.servlet.DispatcherServlet
   </servlet-class>
   <init-param>
     <param-name>contextConfigLocation</param-name>
     <param-value>/WEB-INF/spring/dispatcher-config.xml</param-value>
   </init-param>
   <load-on-startup>1</load-on-startup>
 </servlet>

 <servlet-mapping>
    <!-- Your servlet name --->
   <servlet-name>YOYOman</servlet-name>
   <!--Your URL-->
   <url-pattern>/</url-pattern>
 </servlet-mapping>
```

### For JAVA based (Spring 3.0) Configuration
`org.springframework.web.WebApplicationInitializer`
```java=
 public class MyWebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) {
      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
      appContext.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");

      ServletRegistration.Dynamic dispatcher =
      container.addServlet("dispatcher", new DispatcherServlet(appContext));
      dispatcher.setLoadOnStartup(1);
      dispatcher.addMapping("/");
    }
 }
```



[Usage](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/WebApplicationInitializer.html)

## 3. Spring MVC Controller Class


You can often see some functions as the following
```java=
@Controller
public class controllerEX{
    private static final Logger logger = LoggerFactory.getLogger(controllerEX.class)
    
    @RequestMapping(value="/", method = RequestMethod.GET)
    public string homePage(Locale locale, Model model){
        logger.info("Hey it's home Page, The client locale is {}", locale)
    }
    //..
    
    model.addAttribute("serverTime", ... );
    return "home"
}
```
> `@RequestMapping` 
> : indicates homePage would handle a GET request with URL `/`
> 
> `model.addAttribute("parameter", Dispay_method)`
> : Display_method will correspond to  ${parameter} in .jsp by using model.addAttribute(...)
>
> return "home"
> : it will be **resolved** by the *view resolver* specified in the `servlet-context.xml` file, <font color=red>to find the actual view file</font>
## 5. Views 

views are stored in /WEB-INF/views

```html
<html>
<head>
    <title>Home</title>
</head>
<h1>
    This is index `/`
</h1>
<p>
  <!-- ${serverTime} <-- will be used object of Model class in controller class-->
    The time on the server is ${serverTime}
</p>
</html>
```
