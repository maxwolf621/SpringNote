# Spring Note

## Spring Base
- [Annotation](Annotations.md)
- [loC](SpringBase/IoC.md)  
- [AOP](AOP.md)

## Spring MVC
- [Introduction](SpringMVC_Introduction.md)
## Spring Boot
- [Spring Boot Application and Rest](SpringBootApplication.md)
#### Spring boot Security

- [CORS](SpringBoot/CORS.md)    
- [filter](SpringBoot/Filter.md)  
  > [ UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter](SpringBoot/AuthenticationFilter.md)
- [Key Store](SpringBoot/Keystore.md)
- [Authentication](SpringBoot/Authentication.md)
- [Authorization](SpringBoot/Authorization.md)
- [UserDetailsAndWebConfigurer](SpringBoot/UserDetailsAndWebConfigurer.md)
## Hibernate && JPA
#### Basic
- [SessionAndHibernate](SpringWithDatabase/HibernateSession.md)
  > How Session works in Hibernate Framework for communicating with database via a web request as per session...    
  > Factory creates a session, a session we can have transactions to commit to database    
- [Filter](Filter.md) 
- [HibernateConfiguration](SpringWithDatabase/HibernateConfiguration.md)   
  > How to configure hibernate for spring framework via java class `Configuration` instead of `xml`
  > What is SessionFactory and Session
- [Hibernate Cache](SpringWithDatabase/HibernateCache.md)   
  > reduce to retrieve query data directly from database 
- [Transaction](SpringWithDatabase/Transactional.md)   
#### Hibernate and JPA Query

- **[HQL](SpringWithDatabase/HQL.md)**
- **[Database Relationship](SpringWithDatabase/TableRelationship.md)**  
  > - `@JoinTable` , `@JoinColumn` , `bidirectional`
  > **[GenerationType](SpringWithDatabase/GenerationType.md)**
  > **[CascadeType](SpringWithDatabase/CascadeType.md)**   
- [OneToOne](SpringWithDatabase/HibernateOneToOne.md)  
- [OneToMany](SpringWithDatabase/HibernateOneToMany.md)   
- **[MayToMany](SpringWithDatabase/HibernateManyToMany.md)**   
  > **[`@Embedded` and `@Emenddable`](SpringWithDatabase/AnnotationEmbeddedAndEmbeddable.md)**    
- [Different btw OneToMany and ManyToMany ](SpringWithDatabase/ManyToMany&OneToMany.md)   


#### Cache 
- [Cache](SpringWithDatabase/Cache.md)
- [Caffeine](SpringWithDatabase/Caffeine.md)
- [redis](SpringWithDatabase/Redis.md)  
