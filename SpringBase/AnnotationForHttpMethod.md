
###### tags: `Spring`
# Annotation for POST and GET

[TOC]

| Content Type                        | Parse the content                     |
| ---------------------------------   | ------------------------------------- |
| `application/x-www-form-urlencoded` | `@RequestParam` or `HttpSererRequest`                 |
| `application/json`                  | `@RequestBody`                        |

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

A request from frontend
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

## Using `HttpServeletRequest`

[More Details for HttpServletRequest](/3dDbfI6TQIyoWcyUmrZp_Q)

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
