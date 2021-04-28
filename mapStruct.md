# MapStruct

[TOC]


## Basic Mapping 


if A and B both methods have the same name
```java
@Mapper 
class interface Mapper_Class_Name{
    A toA(B b)
 }
```

Say if A's fieldA and B's fieldB both have different name but need to map together.
For this case, we use `target` and `source` via @Mapping annotation
```java
@Mapper 
class interface Mapper_Class_Name{
    // B mps to A
    @Mapping(target="fieldA", source = fieldB)
    A toA(B b)
 }

/* A.java */
public class A{
    private ? fieldA;
    //....
}
/* B.java */
public class B{
    private ? fieldB;
    // ...
 }
```


declaration
```java
public static void main(String[] args)
{
  private Mapper_Class_Name mapperInstance = Mappers.getMapper(StudentMapper.class);
}
```

## Custom Mapping 

```java
@Mapper
class interface Mapper_Class_Name{
  default  A toA(B b){
    A a = new A();
    a.set1(b.get1() );
    a.set2(b.get2() );
    // a.setter = b.getter
    retrun a
 }
 ```
 
 ## multiple objects mapping
 
 To map >2 classes for example 
 
 
 if C maps to A and B
```java 
/* A */
public class A{
  private FieldA;
  //...
}
/* B */
public class B{
  private FieldB;
  //...
}

/* C */
public classC{
  private FieldCtoB;
  private FieldCtoA;
  //...
}

/* Map */
@Mapper
class interface Mapper_Name{
  @Mapping(target = "A.FieldA", source = "FieldCtoA")
  @Mapping(target = "B.FieldB", source = "FieldCtoB")
  public C ToC(A a,  B b)
}
```

## Mapping Nested Bean

Es gibt A,B und C klasses

When Class A contains object of class B as the field
How map it(A) to (C)
```java
public class B{
  //...
  private ? fieldB
}

// A enthalt B
pubic class A{
  //...
  private B b;
  //...
}
public class C{
  //....
  private ? fieldC
}

/* C to C */
@Mapper
class interface mapper_Name{
  @Mapping(target = "B.name" ,source ="name")
  B toC(C c)
}

```


## Direct Field

```java
@Mapper
class interface mapper_Name{
  @Mapping(target = "B.name" ,source ="name")
  B toC(C c)
  @InheritInverseConfiguration
  C toB(B b);
}

```

## Mapping mit .Builder() 




 
 
