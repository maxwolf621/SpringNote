# Difference of Service, Component And Repo
- [[stackoverflow]What's the difference between @Component, @Repository & @Service annotations in Spring?](https://stackoverflow.com/questions/6827752/whats-the-difference-between-component-repository-service-annotations-in?rq=1)

![image](https://user-images.githubusercontent.com/68631186/172062706-fa307de0-d9e6-4294-9560-e7808abb88f2.png)    

`@Component` class is generic stereotype for any Spring-managed component.   

Def of `@Repository`, `@Service`, or `@Controller` are also annotated with `@Component`, they are special types of `@Component` annotation
```
@Component
public @interface Service {
   //....
}
@Component
public @interface Repository {
   //..
}
 
@Component
public @interface Controller {
   //...
}
```

| Annotation|function                                                |
| --------- | -------------------------------------------------------|   
|@Controller|Presentation layer for requests mapping from presentation page done |
|@Service   |stereotype for service layer(Business Layer                         |
|@Repository|stereotype for persistence layer (Data Access Layer)                |
- `@Controller` class :  Presentation layer won't go to any other file it goes directly to @Controller class and checks for requested path in `@RequestMapping` annotation which written before method calls if necessary.
- `@Service` class : All business logic is here. It will (call method in Repository class) request `@Repository` as per user request
- `@Repository` class : all the Database related operations are done by the repository(**Get data from the database**).


By annotating classes with `@Repository`, `@Service`, or `@Controller` it helps the programmer associate with specific layer aspects 

For example :: Choose using `@Repository` instead `@Component` for the persistence layer, this repository class already supported as a marker for automatic exception translation in the persistence layer(catch platform specific exceptions and re-throw them as one of Spring’s unified unchecked exception).    
