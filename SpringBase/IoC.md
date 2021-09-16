# Spring Bean and Ioc
###### tags: `Spring`

[TOC]

[REFERENCE_1](https://www.journaldev.com/2461/spring-ioc-bean-example-tutorial)

Before introducing IoC first we have to know function of Spring bean

This Note 
> How to Configure Beans 
> Relationship btw Spring Container and Spring Bean
> What is Spring IoC     
> Spring Scopes          

## Initialize for Controller Class

![](https://i.imgur.com/8mbxEiD.png)

1. `AnnotationConfigApplicationContext(Configuration.Class)` 
    > For the Web Applications using Annotation Based Configuration
2. ClassPathXmlApplicationContext(.xml) 
    > Using XML based Configuration
3. FileSystemXmlApplicationContext : 
    > XML file can be loaded anywhere in the file system
4. `AnnotationConfigWebApplicationContext(Configuration.class) `
    > for web applications using Annotation Based Configuration

- Any object in the Spring framework that we initialize through Spring container is called **Spring Bean**.

## Spring Bean's Scopes

Singleton 
> Only one instance of the bean will be created for each container

Prototype 
> A new instance will be created **every time the bean is requested**

==Request (HTTP)==
> Same as prototype but for HTTP application 

==Session (HTTP)==
> A new bean will be created for each HTTP session by the container

Global-session
> used to create global session beans for PROTECT applications

## Spring Bean Configuration

- Annotation Based Configuration
    > By using `@Service` or `@Component` annotations to Configure. 
Scope details can be provided with `@Scope` annotation.

- ***Java Based Configuration***
    > Since Spring 3.0 we can configure Spring beans using java programs with **annotations used for java based configuration `@Configuration`, `@ComponentScan` and `@Bean`.**

[<font color=gold>Note for Java-Based Configuration</font>](/SkSRBZicTfW9SIJfQKIe3Q)

- XML Based Configuration
    > By creating Spring Configuration XML file to configure the beans. If you are using Spring MVC framework, the XML based configuration can be loaded automatically by writing some boiler plate code in `web.xml` file.


# Inversion of Control (IoC)

![](https://i.imgur.com/qj21dCd.png)
>A principle in software engineering which transfers the control of objects or portions of a program to a **container or framework**.

## Example of Spring IoC
HomeController class (Controller Class) will handle the HTTP requests for the home.html of the application. 

*Inject Spring beans (modalities) to this controller class through WebApplicationContext container.*

### Configuration of Bean and Web Application

There are 3 methods
1. XML Based Configuration
2. Annotation Based Configuration
3. Java Based Configuration

#### XML based Configuration

Create A bean Class
```java
public class myBean{
    public Employee emp;
    // getter and setter  ...
}
```

Create spring-mvc.xml to set our bean properties
```xml
<beans ...>
    <beans: ... >
        <properties> ... </properties>
    </bean>
</beans>
```

Set up Deployment Descriptor and Other Spring XML files  
Create A controller class  

Invoke Web Application
```java
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "spring-mvc.xml");
        MyService app = context.getBean(MyService.class);
```

#### Annotation Based Configuration

Bean via Annotation based Configuration
```java
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
//Spring bean scope 
import org.springframework.web.context.WebApplicationContext;
@Service
// Scope is set to Request (for HTTP application)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class MyAnnotatedBean {
    public Eployee emp;
    // getter and settter
}
```

Create A controller Class
> To handle requests from client
```java
// package ....
// import ....
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.journaldev.spring.beans.MyAnnotatedBean;
import com.journaldev.spring.beans.MyBean;

@Controller
@Scope("request")
// Container that initializes bean
public class HomeController {
    // bean of XML based Configuration
    private MyBean myBean;
    // bean of Annotation based Configuration
    private MyAnnotatedBean myAnnotatedBean;

    @Autowired
    public void setMyBean(MyBean myBean) {
        this.myBean = myBean;
    }

    @Autowired
    public void setMyAnnotatedBean(MyAnnotatedBean obj) {
        this.myAnnotatedBean = obj;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
            System.out.println("MyBean hashcode="+myBean.hashCode());
            System.out.println("MyAnnotatedBean hashcode="+
                                    myAnnotatedBean.hashCode());

            Date date = new Date();
            DateFormat dateFormat =
                DateFormat.getDateTimeInstance(DateFormat.LONG,
                                        DateFormat.LONG, locale);

            String formattedDate = dateFormat.format(date);

            // model : display on the web site
            model.addAttribute("serverTime", formattedDate );
            return "home";
    }
}    
```
Invoke web application 


### Configuration Class using Java Based Configuration

We need a Configuration Class that contains bean Members

Create Bean  
```java
import java.util.Date;

// Singleton by default
public class MyService {
	public void log(String msg){
		System.out.println(new Date()+"::"+msg);
	}
}
```

A Configuration Class (To configure Beans) 
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value="com.example.spring.main")
public class MyConfiguration {    
    
    // Using annotation to let Spring know it's bean
    @Bean
    public MyService getService(){
        return new MyService();
    }
}
```

### Invoke Web Application 

Using `AnnotationConfigApplicationContext` to invoke web application
```java
package com.example.spring.mail;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyMainClass {
    public static void main(String[] args) 
       
        // set up (Spring Container)
        AnnotationConfigApplicationContext ctx = 
            new AnnotationConfigApplicationContext(MyConfiguration.class);
       
        // Spring Bean
        MyService service = ctx.getBean(MyService.class);
       
        //service.log("Record_1");
        //return value of hasCode() will be the same 
        MyService newService = ctx.getBean(MyService.class);
        System.out.println("service hashcode="+service.hashCode());
        System.out.println("newService hashcode="+newService.hashCode());
        ctx.close();
    }
}
```