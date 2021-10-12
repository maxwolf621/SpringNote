# Spring AOP

Concept of AOP
It's implementation of PROXY PATTERN

[Ref](https://welson327.gitbooks.io/java-spring/content/spring_aop/basic_concept_proxy.html)  

```vim
interface
'---> implementationA
'---> proxy

proxy dependency implementationA
```

```java
// Subject
public interface Subject {
    public void doSomething() {
        //...
    }
}

// RealSubject
public class RealSubject implements Subject  {
    
    // will be called by instance from proxy
    public void doSomething() {
        //...
    }
}

// Proxy
public class HelloProxy implements Subject {     
    private Subject realSubject; 

    // DI
    public HelloProxy(Subject realSubject) { 
        this.realSubject = realSubject; 
    } 


    public void doSomething() { 
        realSubject.doSomething();
    } 

}
```

### Dynamic Proxy 

- must implement `InvocationHandler`

```java
class LogProxy implements InvocationHandler {

    Object subject;

    // create proxy
    public Object getLogProxy(Object subject) {
        this.subject = subject;

        return Proxy.newProxyInstance(
                subject.getClass().getClassLoader(), 
                subject.getClass().getInterfaces(), this);
    }


    /**
      * dynamically add operations
      */
    @Override 
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        System.out.println("dynamic proxy method starts ..." + method);

        Object result = method.invoke(subject, args);

        System.out.println("dynamic proxy method ends." + method); 
        return result;
    }
}
```

To assign object to proxy ...
```java
interface Subject {
    public void executeByProxy();
}
class RealSubject implements Subject {
    public void executeByProxy() {
        // ...
    }
}

Subject speaker = (Subject) new LogProxy().getLogProxy(new RealSubject());
speaker.executeByProxy();
```



### AOP

AOP helps us for this situations divide 業務邏輯 and 商業邏輯 apart
```java
public void doAction(User user) {

    // 業務邏輯
    if(!isLogin(user)) {   // <--- cross cutting
        throw new Exception();
    }
    if(!isPermitted(user) { // <---cross cutting
        throw new Exception();
    }


    // 商業邏輯
    doAction();
}
```


```

LOGIN STARTS ------- | --------- | --------- | -------- LOGIN ENDS
                     'AOP        'AOP        'AOP 
```