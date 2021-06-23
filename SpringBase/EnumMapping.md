###### tags: `Hibernate`
# Enum Type Mapping 
[TOC]

> To map an enum type from Java to a column in database with Hibernate ORM framework

For example we have a Entity called Person with the Gender column
Now we want to hold the value with specified range (Male or Female)

It may look like this
```java=
public enmu Gender{
    Male ; Female
}
public class Person {
    // other fields
    private Gender gender;
    //..
}
```

Now we can use Annotation or XML based Configuration to map the Column
## Annotation Mapping

if DateType of the Column Gender is integer  
(e.g. 0 : male, 1 : female)
```java=
@Enumerated(EnumType.ORDINAL)
public Gender getGender(){
    return gender;
}
```

if it's a String type
```java=
@Enumerated(EnumType.STRING)
public Gender getGender
{
    return gender;
}
```

## Xml Mapping

```xml=
<property name="gender" column="GENDER">
    <type name="org.hibernate.type.EnumType">
        <param name="enumClass">com.example.hibernate.Gender</param>
    </type>
</property>
```
```xml=
<property name="gender" column="GENDER">
    <type name="org.hibernate.type.EnumType">
        <param name="enumClass">com.example.hibernate.Gender</param>
        <param name="useNamed">true</param>
    </type>
</property>
```
