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
background-color: #F8F8FFF;
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

# Annotation for POST and GET


###### tags: `Spring`

[TOC]

| Content Type                      | Parse the content                     |
| --------------------------------- | ------------------------------------- |
| `application/x-www-form-urlencoded` | `@RequestParam` or `HttpSererRequest`                 |
| `application/json`               | `@RequestBody`                        |

## `@RequestParam` to get URL's parameter

`http://localhost:5000/api/unit?code=111`
To get **111** we use `@RequestParam`
```java
@RestController
public class controller{
  @RequestMapping(value="/example", method=RequestMethod.GET)  
  public String sayHello(@RequestParam Integer id){
        return "id:"+id;
  }  
}
```
## `@PathVariable` to get URL's parameter 

To get variable(parameter) in the URL 

```java
RequestMapping("user/get/mac/{macAddress}")
public String getByMacAddress(@PathVariable String macAddress){
　　//do something;
}
```

For example: 

```java
@RestController
public class HelloController {
    @RequestMapping(value="/hello/{id}/{name}",method= RequestMethod.GET)
    public String sayHello(@PathVariable("id") Integer id,
                    @PathVariable("name") String name){
        return "id:"+id+" name:"+name;
    }
}
```


## @RequestBody 

Content-Type should be `application/json` only

Consider a model class
```java
public class Person {

    private long id;
    private String name;
    private int age;
    private String hobby;

    @Override
    public String toString(){
        return "name:"+name+";age="+age+";hobby:"+hobby;
    }

    // getters and setters
}
```


A request from front end
```json
{ 

  "name" :"suki_rong",
  "age"  : 18, 
  "hobby":"programing"
}
```

To get json Content Type using `@RequestBody`
```java=
// A request from URL demo1
@PostMapping(path="/demo1")
{
  public void demo1(@RequestBody Person person) {
    System.out.println(person.toString());
}

// or
@PostMapping(path = "/demo1")
public void demo1(@RequestBody Map<String, String> person) {
    System.out.println(person.get("name"));
}

```

## Using `HttpServeletRequest` instead of `@RequestBody`

[More Details for HttpServletRequest](/3dDbfI6TQIyoWcyUmrZp_Q)

I think this is much clearer than using `@RequestBody`

```java
HttpServletRequest request;
request.getHeader(String name);  // return String type 
request.getheaders(String name); // return Enumeration
```

```java
@GetMapping("/demo3")
public void demo3(HttpServletRequest request) {
    // get request's value
    System.out.println(request.getHeader("myHeader"));
    for (Cookie cookie : request.getCookies()) {
        if ("myCookie".equals(cookie.getName())) {
            System.out.println(cookie.getValue());
        }
    }
```

[JSON](https://yellowcong.blog.csdn.net/article/details/79711429)