# MapStruct

[TOC]


@Mapper : This Annotation tells compiler to using MapStruct 
@Mapping : To Map A and B field

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
If C maps to A then 
```java
public class B{
  //...
  private ? fieldB
}

// A enthalt object B
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
  B CtoB(C c); 
  //...
}

```





## Direct Field

code As Nested Bean

```java
@Mapper
class interface mapper_Name{
  @Mapping(target = "B.name" ,source ="name")
  B CtoB(C c);
  @InheritInverseConfiguration
  C BtoC(B b);
}
```

## Mapping mit .Builder() 


## Conversion

### numberFormat

```java
@mapping(numberformat = "$#.00")
```

for example
```java
/* A */
pubic class A{
    private double price;
    //...
}
/* B */
public class B{
    private string price;
    //..
}
/* Mapper */
@Mapper
public interface Mapper{
    @Mapping(target = "price" , source= = "price" , numberformat = "$@.00")
    B AtoB(A a);
}
```

### dataFormat


Conversion of data to `String`
```java
/* A */
public class A{
    private String Date; // String type data
    //...
}
/* B */
import java.util.GregoriaCalender;
public class B{
    private GregoriaCalender Date;
    // ... 
}
/* Mapper */
@Mapper
public interface Mapper{
    @Mapping(source = "Date" , target = "Date" , dataFormat = "dd.MM.yyyy")
    A BtoA(B b);
}
```

### expression

mapper will call java method written in the expression 


```java
@Mapping( expression = "java( b.method( a.METHOD() )" )
```

 for example
 ```java
 /* A */
 public class A{
    public  MethodA{
        //..
     }
     //.. 
 }
/* B */
 pbulic class B{
    public 
 }
 ```
 
 ### Constant
 
 Map a constant value to target
 
 ```java
 mapping(target = "ConstantMapMe" , constant = "MeMapTarget") 
 // assign MeMapTarget to ConstantMapMe
 ```
 
 ### defaultValue
 To pass the default value in case source property is null using defaultValue attribute of @Mapping annotation.
 
```java
@Mapping( target = "target-property", 
          source="source-property",
          defaultValue = "default-value")
```

### expression

```java
@Mapping(target = "TargetProperty", 
   expression = "java(TargetMethod( .... ))")
```

or we can use `qualifiedByName` attribute which makes code more flexible
```java

```

### defaultValueExpression 

using method to set up defaultValue

```java
@Mapping(target = "target-property", 
         source="source-property" 
         defaultExpression = "default-value-method")
```

For example
```java
/* A */
public class A{
    private String name;
    //..
}
/* B */
pubic class B{
    private String name;
    //..
}
/* Mapper */
public interface mapper{
    @Mapping(target = "name" , 
             source = "name" , 
             defaultExpression = "java(UUID.randomUUID().toString())")
     B AtoB(A a);
}
```

## Mapping Collections 

### list

```java
@Mapper
public interface Mapper{
    //..
    List<objectB> ToObjectB(List<objectA> methodA)
}
/* In Main */
objectA a = //...
objectA b = //...
objectA c = //...
List<objectA> aList = Array.asList(a,b,c);
List<objectB> blist = Mapper.toObjectB(alist);
```

### Map
[reference](https://mapstruct.org/documentation/stable/api/org/mapstruct/MapMapping.html)

Configures the mapping between two map types, e.g. Map<String, String> and Map<Long, Date>.

```java
@Mapper
public interface UtilityMapper {
   @MapMapping(valueDateFormat = "dd.MM.yyyy")
   Map<String, String> getMap(Map<Long, GregorianCalendar> source);
}
```


## Enum

[referece](https://mapstruct.org/documentation/stable/api/org/mapstruct/ValueMapping.html)

```java
 public enum OrderType { 
    RETAIL, 
    B2B, 
    EXTRA, 
    STANDARD, 
    NORMAL 
 }
 public enum ExternalOrderType { 
    RETAIL, 
    B2B, 
    SPECIAL, 
    DEFAULT 
 }

// oderType maps to ExternalOrderType
public interface MAPPER{
 @ValueMapping( source = MappingConstants.NULL, target = "DEFAULT" ),
 @ValueMapping( source = "STANDARD", target = MappingConstants.NULL ),
 // All rest fields in enum will map to 'SEPCIAL'
 @ValueMapping( source = MappingConstants.ANY_REMAINING, target = "SPECIAL" )
 ExternalOrderType orderType_TO_ExternalOrderType(OrderType orderType);
}
```

```
 Mapping result:
 +---------------------+----------------------------+
 | OrderType           | ExternalOrderType          |
 +---------------------+----------------------------+
 | null                | ExternalOrderType.DEFAULT  |
 | OrderType.STANDARD  | null                       |
 | OrderType.RETAIL    | ExternalOrderType.RETAIL   |
 | OrderType.B2B       | ExternalOrderType.B2B      |
 | OrderType.NORMAL    | ExternalOrderType.SPECIAL  |
 | OrderType.EXTRA     | ExternalOrderType.SPECIAL  |
 +---------------------+----------------------------+
```

source's valid value
```
enum constant name
MappingConstants.NULL
MappingConstants.ANY_REMAINING
MappingConstants.ANY_UNMAPPED
```

target's valid value
```java
enum constant name
MappingConstants.NULL
```

## Map Stream

```java
@Mapper
public interface UtilityMapper {
   Stream<String> getStream(Stream<Integer> source);
}

// build a stream<Integer>
Stream<Integer> numbers = Arrays.asList(1, 2, 3, 4).stream();

// Stream<integer> maps to Stream<String>
Stream<String> strings = utilityMapper.getStream(numbers);
assertEquals(4, strings.count());	
```

## Customer 

