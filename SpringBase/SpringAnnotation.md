# Spring Annotation
###### tags: `Spring`

[REF](https://www.journaldev.com/16966/spring-annotations)

[TOC]

Using **Annotations** to define beans, their dependencies and other XML based file  


## Annotations (often used)

* `@Configuration`
    - To indicate that **A class declares one or more `@Bean` methods from bean class**.
    > ==These Configuration classes are processed by the **Spring Container** to generate bean **definitions** and **server requests at run-time**==
    ![](https://i.imgur.com/CpZdLGY.png)


#### @Bean
- Indicates that a method produces a bean to be managed by the Spring container.  
- This is one of the most used and important spring annotation.   
- It also can be used with parameters like **name, initMethod and destroyMethod.**  

For example 
- To Indicate bean method from a bean class (class Computer) in configuration Class
```java=
@Configuration
public class AppConfig {
    @Bean(name = "comp", initMethod = "turnOn", destroyMethod = "turnOff")
    Computer computer(){
        return new Computer();
    }
}
```
Bean Class Commputer
```java=
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

There are also other way for parameters `initMethod` and `destroyMethod`.  
They are `@PreDestroy` and `@PostConstruct`
```java=
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
:::info
Second Approach is recommended
:::


#### Layers in Spring Framework

![](https://i.imgur.com/3cmZ6Ro.png)



#### @Component
- Indicates that an annotated class is a **component**.   
- ==Such classes are considered as candidates for **auto-detection** when using annotation-based configuration and classpath scanning.==  
- Spring only picks up and *registers* beans with `@Component` and doesn't look for `@Service` and `@Repository` in general.  
- ==Components are registered in **ApplicationContext** because they themselves are annotated with `@Component`==
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

#### @Autowired
- Spring `@Autowired` annotation is used for automatic injection of beans.   
for example
```java=
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

#### @Qualifier
- is used in **conjunction with `@Autowired` to avoid confusion when we have two of more bean configured for same type.**  

#### @PropertySource
- Provides a simple declarative mechanism for adding a property source to Spring’s Environment.  
- There is a similar annotation for adding an array of property source files   
    > i.e `@PropertySources`.  



## Spring MVC
@Controller  
@RequestMapping  
@PathVariable  
@RequestParam  
@ModelAttribute  
@RequestBody and @ResponseBody  
@RequestHeader and @ResponseHeader  

## Security Annotations
@EnableWebSecurity is used with @Configuration class to have the Spring Security configuration defined
## Spring Boot Annotations 
@SpringBootApplication  
@EnableAutoConfiguration  




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

