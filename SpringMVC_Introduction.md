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

```htmlmixed=
<html>
<head>
    <title>Home</title>
</head>
<h1>
    This is index `/`
</h1>
<p>
    The time on the server is ${serverTime}
</p>
</html>
```
> ${serverTime} <-- will be used object of Model class in controller class



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


/*---FORM---*/
  .form-control {
    background: #333;
    color: #fff;
  }

  .form-control::placeholder,
  .form-control::-webkit-input-placeholder,
  .form-control:-moz-placeholder,
  .form-control::-moz-placeholder,
  .form-control:-ms-input-placeholder {
    color: #eee;
    
  }

/*--------------- navbar ---------------*/

.header {
background-color: #1B2631;
border-color: #1B2631;
}

/* whole bar 背景色*/
.navbar {
background-color: #1B2631;
border-color: #1B2631;
}

/* */
.navbar a {
color: #9BCBFC !important;
}

/* editor, both, show 外觀顏色*/
.navbar .btn-group label {
background-color: #1B2631;
color: white;
border-color: gold;
}




/* navbar for bearbeiten, beides, Anzeigen 當游標指向這些物件時的顏色 */
.navbar .btn-group label.btn-default:focus,
.navbar .btn-group label.btn-default:hover {
background-color: #273746;
color: #eee;
border-color: yellow;
}

/* navbar for bearbeiten, beides, Anzeigen while activing */
.navbar .btn-group label.active {
background-color: #555;
color: #eee;
border-color: ;
}

.navbar .btn-group label.active:focus,
.navbar .btn-group label.active:hover {
background-color: #555;
color: #eee;
border-color: #555;
}

.navbar-default .btn-link:focus,
.navbar-default .btn-link:hover {
    color: #eee;
}

.navbar-default .navbar-nav>.open>a,
.navbar-default .navbar-nav>.open>a:focus,
.navbar-default .navbar-nav>.open>a:hover {
background-color: #555;
}

.dropdown-header {
color: #aaa;
}

.dropdown-menu {
background-color: #222;
border: 1px solid #555;
border-top: none;
}
.dropdown-menu>li>a {
color: #eee;
}

.dropdown-menu>li>a:focus,
.dropdown-menu>li>a:hover {
background-color: #555555;
color: #eee;
}

.dropdown-menu .divider {
background-color: #555;
}

.header .open .dropdown-menu {
background-color: #202020;
}

.navbar .announcement-popover {
background: #4F4F4F;
}

.navbar .announcement-popover .announcement-popover-header {
background: #2e2e2e;
border-bottom: 1px solid #2e2e2e;
}

.navbar .announcement-popover .announcement-popover-body {
background: #4F4F4F;
color: #eee;
}

.navbar .announcement-popover .announcement-popover-footer {
background: #4F4F4F;
}

.navbar .announcement-area .caption.inverse {
color: #eee;
}

.label-warning {
background-color: #ffc107;
color: #212529;
}


/*--------------- settings ---------------*/
.section .form-horizontal .form-group .btn-default {
font-size: 16px;
border-color: #6d6d6d;
background-color: #333;
color: #FFF;
}

.section .form-horizontal .form-group .btn-default:hover,
.section .form-horizontal .form-group .btn-default:focus {
background-color: #737373;
color: #FFF;
}

.section .form-horizontal .form-control:focus {
border-color: #bbb;
}

/* Shared */
#notificationLabel,
.ui-infobar .btn.ui-edit {
color: #eee;
border-color: #6a6a6a;
}

.ui-infobar__user-info li {
color: #bbb;
}

footer {
background: #101010;
color: #bbb;
border-top: 1px solid #454545;
}

footer a {
color: #bbb;
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

/* [](htpp://) */
a,.open-files-container li.selected a {
    color: #D2B4DE;
}


/*--------------- editor ---------------*/
.cm-m-markdown {
color: #ddd;
}

.cm-s-one-dark .cm-header,
.cm-m-xml.cm-attribute {
color: #ffa653;
}

.cm-s-one-dark .cm-string,
.cm-s-one-dark .cm-variable-2 {
color: #7bf;
}

.cm-m-markdown.cm-variable-3 {
color: #ff7e7e;
}

.cm-s-one-dark .cm-link {
color: #b0ee83;
}

.cm-s-one-dark .CodeMirror-linenumber {
color: #666;
}

.cm-strong {
color: #f4511e;
}

.cm-s-one-dark .cm-comment {
color: #a9a9a9;
}

.cm-matchhighlight {
color: #ffea00;
}

.cm-positive {
color: #11bf64;
}

.cm-negative {
color: #ff3e3e;
} 

.dropdown-menu.CodeMirror-other-cursor {
border: 2px solid #4d4d4d;
background-color: #202020;
}

.dropdown-menu.CodeMirror-other-cursor li a {
color: #ececec;
}


/*--------------- Navi bar of ? ---------------*/
.modal-header {
background-color: #2a2a2a;
}

.panel-default {
border-color: #6d6d6d;
}

.panel-default>.panel-heading {
background-color: #2a2a2a;
color: #eee;
border-color: #6d6d6d;
}

.panel-body {
background: #2e2e2e;
}

.panel-body a {
color: #7bf;
}

.table>tbody>tr>td,
.table>tbody>tr>th,
.table>tfoot>tr>td,
.table>tfoot>tr>th,
.table>thead>tr>td,
.table>thead>tr>th {
border-color: #6d6d6d;
}


/*--------------- history / recent ---------------*/
  .list.row-layout li .item {
    border-color: #696c7d;
  }

  .list.row-layout li:nth-last-of-type(1) .item {
    border-bottom: none;
  }

  .list li .item {
    background: #1c1c1c;
  }

  .list li:hover .item,
  .list li:focus .item {
    background: #404040;
  }

  .list li .item h4 {
    color: #fff;
  }

  .list li p {
    color: #ccc;
  }

  .list li p i {
    font-style: normal;
  }

  .list li .item .content .tags span {
    background: #555;
  }

  .list li .item.wide .content .title a,
  .list li .item.wide .content .title a:focus,
  .list li .item.wide .content .title a:hover {
    color: #ddd;
  }

  .ui-item {
    color: #fff;
    opacity: 0.7;
  }

  .ui-item:hover,
  .ui-item:focus {
    opacity: 1;
    color: #fff;
  }

  .list li .item.wide hr {
    border-color: #6d6d6d;
  }

  .overview-widget-group .btn,
  .multi-select-dropdown-menu .ui-dropdown-label,
  .multi-select-dropdown-menu .dropdown-options,
  .form-control {
    border-color: #6d6d6d;
  }

  .multi-select-dropdown-menu .dropdown-options .ui-option:hover {
    background-color: #4d4d4d;
    color: #eee;
  }

  #overview-control-form #overview-keyword-input-container .select2-container {
    background-color: #3e4045 !important;
  }

  #overview-control-form #overview-keyword-input-container .select2-container .select2-choices {
    background-color: #3e4045;
  }

  .search {
    background-color: #3e4045;
    color: #eee;
  }

  .btn.btn-gray {
    background: #1b1b1b;
  }

  .btn.btn-gray:hover {
    background: #4d4d4d;
    color: #eee;
  }

  .search::placeholder,
  .search::-webkit-input-placeholder,
  .search:-moz-placeholder,
  .search::-moz-placeholder,
  .search:-ms-input-placeholder {
    color: #eee;
  }

  .btn.btn-gray {
    border-color: #6d6d6d;
    background: #333;
    color: #eee;
  }

  .select2-default {
    color: #eee !important;
  }

  .select2-results .select2-highlighted {
    background: #4d4d4d;
    color: #eee;
  }

  .select2-container-multi .select2-choices {
    background: #3e4045;
  }

  .select2-container-multi .select2-choices .select2-search-choice {
    background: #131313;
    color: #eee;
    border-color: #555;
    box-shadow: none;
  }

  .btn-default,
  .btn-default:focus {
    color: #eee;
    background-color: #2e2e2e;
    border-color: #6a6a6a;
  }

  .btn-default.active.focus,
  .btn-default.active:focus,
  .btn-default.active:hover,
  .btn-default:active.focus,
  .btn-default:active:focus,
  .btn-default:active:hover,
  .open>.dropdown-toggle.btn-default.focus,
  .open>.dropdown-toggle.btn-default:focus,
  .open>.dropdown-toggle.btn-default:hover {
    background: #737373;
  }

  .btn-default:hover {
    color: #fff;
    background-color: #7d7d7d;
    border-color: #6a6a6a;
  }

  .overview-widget-group .btn.active {
    background-color: #6a6a6a;
    color: #eee;
  }

  .overview-widget-group .btn:hover {
    background-color: #7d7d7d;
    color: #eee;
    border-color: #636363;
  }

  .overview-widget-group .slider.round {
    border-color: #ccc;
  }

  .overview-widget-group .slider.round:before {
    border-color: #ccc;
  }

  .overview-widget-group input:checked+.slider {
    background-color: #ccc;
  }

  .ui-category-description-icon a {
    color: #eee;
  }

  .item .ui-history-pin.active {
    color: #f00;
  }

  .ui-history-close {
    color: #eee;
    opacity: 0.5;
  }

  .pagination>li>a,
  .pagination>li>span {
    color: #eee;
    background-color: #2e2e2e;
    border-color: #6a6a6a;
  }

  .pagination>li>a:hover {
    color: #fff;
    background-color: #7d7d7d;
    border-color: #6a6a6a;
  }

  .pagination>.disabled>a,
  .pagination>.disabled>a:focus,
  .pagination>.disabled>a:hover,
  .pagination>.disabled>span,
  .pagination>.disabled>span:focus,
  .pagination>.disabled>span:hover {
    color: #eee;
    background-color: #2e2e2e;
    border-color: #6a6a6a;
  }

  .pagination.dark>li>a,
  .pagination.dark>li>span {
    color: #aaa;
  }

/*--------------- comment ---------------*/
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

.ui-comment-container .ui-comments-container .ui-comment .comment-content {
color: #eee;
}

.ui-comment-container .ui-comments-container .ui-comment .comment-menu {
color: #eee;
}

.ui-comment-container .ui-comments-container .ui-comment .comment-menu .comment-dropdown-menu {
background: #222;
color: #eee;
border-color: #555;
}

.ui-comment-container .ui-comments-container .ui-comment .comment-menu .comment-dropdown-menu>div:hover {
background-color: #555555;
color: #eee;
}

.ui-comment-container .ui-comments-container .ui-comment .comment-menu:hover,
.ui-comment-container .ui-comments-container .ui-comment .comment-menu:active,
.ui-comment-container .ui-comments-container .ui-comment .comment-menu.active {
background-color: #737373;
color: #eee;
}

.ui-comment-container .ui-comment-input-container {
background-color: #3c3c3c;
}

.ui-comment-container textarea {
background-color: #3e4045;
color: #eee;
border: 1px solid #6d6d6d;
}

.ui-comment-container textarea::placeholder,
.ui-comment-container textarea::-webkit-input-placeholder,
.ui-comment-container textarea:-moz-placeholder,
.ui-comment-container textarea::-moz-placeholder,
.ui-comment-container textarea:-ms-input-placeholder {
color: #eee;
}

@keyframes highlight {
0% {
  background-color: #3c3c3c;
}

30% {
  background-color: #3c3c3c;
}

100% {
  background-color: transparent;
}
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
background-color: #1e1e1e;
border: 1px solid #555 !important;
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

/*--------------- code mirror ---------------*/
.modal-content {
background: #1f2226;
}

.modal-header {
border-bottom: 1px solid #46484f;
}

.modal-footer {
border-top: 1px solid #46484f;
}

a.list-group-item {
background: #1f2226;
color: #ddd;
border: 1px solid #46484f;
}

a.list-group-item .list-group-item-heading {
color: #ddd;
}

a.list-group-item:focus,
a.list-group-item:hover {
background: #434651;
color: #ddd;
}

button.close {
color: #ddd;
opacity: .5;
}

.close:focus, .close:hover {
color: #fff;
opacity: .8;
}

.CodeMirror {
background: #1f2226;
}

.CodeMirror-gutters {
background: #1f2226;
border-right: 1px solid rgba(204, 217, 255, 0.1);
}

.cm-s-default .cm-comment {
color: #888;
}

.cm-s-default .cm-quote {
color: #ddd;
}

.cm-s-default .cm-header {
color: #ffa653;
}

.cm-s-default .cm-link {
color: #b0ee83;
}

.cm-s-default .cm-string,
.cm-s-default .cm-variable-2 {
color: #7bf;
}

.cm-s-default .cm-def {
color: #c678dd;
}

.cm-s-default .cm-number,
.cm-s-default .cm-attribute,
.cm-s-default .cm-qualifier,
.cm-s-default .cm-plus,
.cm-s-default .cm-atom {
color: #eda35e;
}

.cm-s-default .cm-property,
.cm-s-default .cm-variable,
.cm-s-default .cm-variable-3,
.cm-s-default .cm-operator,
.cm-s-default .cm-bracket {
color: #f76e79;
}

.cm-s-default .cm-keyword,
.cm-s-default .cm-builtin,
.cm-s-default .cm-tag {
color: #98c379;
}

.modal-title {
color: #ccc;
}

.modal-body {
color: #ccc !important;
}

div[contenteditable]:empty:not(:focus):before {
color: #aaa;
}

.CodeMirror pre {
color: #ddd;
}

.CodeMirror pre span[style^="background-color: rgb(221, 251, 230)"] {
background-color: #288c27 !important;
}

.CodeMirror pre span[style^="background-color: rgb(249, 215, 220)"] {
background-color: #a52721 !important;
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
  

  /*------- code highlight: Visual Stutdio Code theme for highlight.js -------*/
  .hljs {
    background: #1E1E1E;
    color: #DCDCDC;
  }

  .hljs-keyword,
  .hljs-literal,
  .hljs-symbol,
  .hljs-name {
    color: #569CD6;
  }

  .hljs-link {
    color: #569CD6;
    text-decoration: underline;
  }

  .hljs-built_in,
  .hljs-type {
    color: #4EC9B0;
  }

  .hljs-number,
  .hljs-class {
    color: #B8D7A3;
  }

  .hljs-string,
  .hljs-meta-string {
    color: #D69D85;
  }

  .hljs-regexp,
  .hljs-template-tag {
    color: #d16969;
  }

  .hljs-title {
    color: #dcdcaa;
  }

  .hljs-subst,
  .hljs-function,
  .hljs-formula {
    color: #DCDCDC;
  }

  .hljs-comment,
  .hljs-quote {
    color: #57A64A;
  }

  .hljs-doctag {
    color: #608B4E;
  }

  .hljs-meta,
  .hljs-meta-keyword,
  .hljs-tag {
    color: #9B9B9B;
  }

  .hljs-variable,
  .hljs-template-variable {
    color: #BD63C5;
  }

  .hljs-params,
  .hljs-attr,
  .hljs-attribute,
  .hljs-builtin-name {
    color: #9CDCFE;
  }

  .hljs-section {
    color: gold;
  }

  .hljs-emphasis {
    font-style: italic;
  }

  .hljs-strong {
    font-weight: bold;
  }

  /*
  .hljs-code {
    font-family:'Monospace';
  }
  */

  .hljs-bullet,
  .hljs-selector-tag,
  .hljs-selector-id,
  .hljs-selector-class,
  .hljs-selector-attr,
  .hljs-selector-pseudo {
    color: #D7BA7D;
  }

  .hljs-addition {
    background-color: #155a36;
    color: #dfdfdf;
    display: inline-block;
    width: 100%;
  }

  .hljs-deletion {
    background-color: #872e2e;
    color: #dfdfdf;
    display: inline-block;
    width: 100%;
  }
</style>

