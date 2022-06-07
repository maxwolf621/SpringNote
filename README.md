# Spring Note

[Spring Boot Tutorial](https://morosedog.gitlab.io/categories/Spring-Boot/)

## Base
- [Framework MVC, MVP and MVVM](framework.md)
- [loC](SpringBase/IoC.md)  
- [Annotation](Annotations.md)
  - [Layer Annotation](SpringBoot/layerAnnotation.md) 
- [AOP](AOP.md)

## Spring Boot
- [Spring Boot Application](SpringBoot/SpringBootApplication.md)

### Spring boot Security

![image](https://user-images.githubusercontent.com/68631186/172059135-570bfaa7-cc5b-4e95-ba24-eb8955e6545b.png)

- [Authentication](SpringBoot/Authentication.md)
- [Filter](SpringBoot/Filter.md)   
  - [ UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter](SpringBoot/AuthenticationFilter.md)
- [Authorization](SpringBoot/Authorization.md)
- [WebSecurity](SpringBoot/WebSecurity.md)
  - [CORS setup](SpringBoot/CORS.md)   
  - [UserDetailService](UserdetailsService.md)
- [SpringBoot In Action Login](JWTAuth.md) 
#### Token
- [KeyStore](SpringBoot/Keystore.md)
- [Java JWT](SpringBoot/JWT.md)
- [Jwt Bash Comand](SpringBoot/KeystoreInCommand.sh)

## Hibernate && JPA
### Basic
- [SessionAndHibernate](SpringWithDatabase/HibernateSession.md)
  > How Session works in Hibernate Framework for communicating with database via a web request as per session...    
  > Factory creates a session, a session we can have transactions to commit to database    
- [HibernateConfiguration](SpringWithDatabase/HibernateConfiguration.md)   
  > How to configure hibernate for spring framework via java class `Configuration` instead of `xml`
  > What is SessionFactory and Session
- [Hibernate Cache](SpringWithDatabase/HibernateCache.md)   
  > reduce to retrieve query data directly from database 
- [Transaction](SpringWithDatabase/Transactional.md)   


#### Hibernate and JPA Query

- **[HQL](SpringWithDatabase/HQL.md)**
- **[Database Relationship](SpringWithDatabase/TableRelationship.md)**  
  - `@JoinTable` , `@JoinColumn` , `bidirectional`   
  - **[GenerationType](SpringWithDatabase/GenerationType.md)**
  - **[CascadeType](SpringWithDatabase/CascadeType.md)**   
- [OneToOne](SpringWithDatabase/HibernateOneToOne.md)  
- [OneToMany](SpringWithDatabase/HibernateOneToMany.md)   
- **[MayToMany](SpringWithDatabase/HibernateManyToMany.md)**   
  - **[`@Embedded` and `@Emenddable`](SpringWithDatabase/AnnotationEmbeddedAndEmbeddable.md)**    
- [Different btw OneToMany and ManyToMany ](SpringWithDatabase/ManyToMany&OneToMany.md)   

#### Cache 
- [Cache](SpringWithDatabase/Cache.md)
- [Caffeine](SpringWithDatabase/Caffeine.md)
- [redis](SpringWithDatabase/Redis.md)  


#### RxJAVA
- [Reactive Programming](reactive/RxJava.md)
- [Spring WebClient](reactive/webclient.md)
