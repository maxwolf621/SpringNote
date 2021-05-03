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
    color: #5EB7E0;
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
background-color: #eee;
border: 1px solid !important;
  color: #dfdfdf;
}


/* table */
.markdown-body table tr {
background-color: #1e1e1e;
border-top: none;
border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.markdown-body table tr:first-child {
border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.markdown-body table tr:nth-child(2n) {
background-color: #333;
}

.markdown-body table tr th {
color: #64B5F6;
}

.markdown-body table th,
.markdown-body table td {
border: none;
}

.markdown-body table tr th:first-child,
.markdown-body table tr td:first-child {
border-left: 1px solid rgba(255, 255, 255, 0.1);
}

.markdown-body table tr th:last-child,
.markdown-body table tr td:last-child {
border-right: 1px solid rgba(255, 255, 255, 0.1);
}

.markdown-body table tr td {
color: #ddd;
}

.markdown-body pre.flow-chart,
.markdown-body pre.sequence-diagram,
.markdown-body pre.graphviz,
.markdown-body pre.mermaid,
.markdown-body pre.abc {
background-color: #fff !important;
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

# Spring Bean and Ioc
###### tags: `Spring`

[TOC]

[REFERENCE_1](https://www.journaldev.com/2461/spring-ioc-bean-example-tutorial)

Before introducing IoC first we have to know function of Spring bean

This Note 
> How to Configure Beans ?
> Relationship btw Spring Container and Spring Bean
> What is Spring IoC     ?
> Spring Scopes          ?

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
> used to create global session beans for PROTLET applications

[HTTP Session]

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

*Inject Spring beans (modularities) to this controller class through WebApplicationContext container.*

### Configuration of Bean and Web Application

There are 3 methods
1. XML Based Configuration
2. Annotation Based Configuration
3. Java Based Configuration

#### XML based Configuration

1. Create A bean Class
    ```java
    public class myBean{
        public Employee emp;
        // getter and setter  ...
    }
    ```
    Create spring-mvc.xml to set our bean properties
    ```xml=
    <beans ...>
        <beans: ... >
            <properties> ... </properties>
        </bean>
    </beans>
    ```
2. Set up Deployment Descriptor and Other Spring XML files
3. Create A controller class
4. Invoke Web Application
    ```java=
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                    "spring-mvc.xml");
            MyService app = context.getBean(MyService.class);
    ```


#### Annotation Based Configuration

1. Bean via Annotation based Configuration
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
2. Create A controller Class
3. Invoke web application 


#### Create A controller class

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




### Configuration Class using Java Based Configuration

We need a Configuration Class that contains bean Members

Create Bean  
```java=
import java.util.Date;

// Singleton by default
public class MyService {
	public void log(String msg){
		System.out.println(new Date()+"::"+msg);
	}
}
```

A Configuration Class (To configure Beans) 
```java=
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
Using `AnnotationConfigAppliocationContext` to invoke web application
```java=
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
