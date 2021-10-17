
[RxJava Blocking and Non Blocking](https://www.baeldung.com/spring-webclient-resttemplate)



## `RestTemplate` Blocking Client

[Example](https://www.tpisoftware.com/tpu/articleDetails/2383)  

Under the hood, `RestTemplate` uses the Java Servlet API, which is based on the thread-per-request model.


Once the requests are getting bigger , their waiting for the results will pile up.  
Consequently, the application will create many threads, which will exhaust the thread pool or occupy all the available memory. We can also experience performance degradation because of the frequent CPU context (thread) switching.

- Each thread will block until the web client receives the response.    
- Each thread consuming some amount of memory and CPU cycles.


## `WebClient` Non-Blocking Client
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

On the other side, `WebClient` uses an asynchronous, non-blocking solution provided by the Spring Reactive framework.

While `RestTemplate` uses the caller thread for each event (HTTP call), `WebClient` will create something like a **task** for each event.   
Behind the scenes, the Reactive framework will **queue** those **tasks** and execute them only when the appropriate **response** is available.

> The Reactive framework uses an **event-driven (tasks)** architecture.  

It provides means to compose asynchronous logic through the Reactive Streams API.   
As a result, the reactive approach can process more logic while using fewer threads and system resources, compared to the synchronous/blocking method.


