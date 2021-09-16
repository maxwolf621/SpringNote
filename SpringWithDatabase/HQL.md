###### tags: `Hibernate`
# [Hibernate Query Language](https://www.codejava.net/frameworks/hibernate/hibernate-query-language-hql-example)

## Properties of HQL

1. SQL Similarity
2. **Full Object-Oriented**
   > HQL can operated with inheritance, polymorphism ...
3. Case-Insensitive (for keyword)   
   > e.g. `Select`, `SELECT`, `select` they are the same

## Create A Query(" .... HQL QUERIES ...")

```java
String hql = "From x where x.value = 'Y' ";

/*
 * <p> Create a Query Instance 
 *     via the session For A Sql Database
 * </p>
 */
Query query = session.createQuery(hql);
```

## `List list()` and `int executeUpdate()` methods

Using `list` or `executeUpdate` methods depending on the type of the query

`list` : doesn't modify database
`executeUpdate()` : do modifying the database

For `SELECT` in HQL, then we use `list` method
```java
/**
 * <p> Select query on a mapped object 
 *     returns a list of those objects.
 * </p>
 */
List<Model_Class> listResult = query.list();

/** FOR EXAMPLE **/

String hql = "from Category";
Query query = session.createQuery(hql);

/**
 * <pre> SELECT FROM CATEGORY 
 * </pre>
 */
List<Category> listCategories = query.list();
 
/**
 * <p> Print Each Resource </p>
 */
for (Category aCategory : listCategories) {
    System.out.println(aCategory.getName());
}
```

for `INSERT`, `UPDATE`, `DELETE`, then we use `executeUpdate()`
```java
/**
 * @return 
 * If result(integer) is bigger than (>) 1 
 * then execute successful
 */ 
int result = query.executeUpdate();
```

### `Select` examples

```java
/**
 * <pre> sql Syntax :Select from xx where xxx 
 * </pre>
 */
String hql  ="from tableX where value = 'X' ";
String hql2 ="from tableX where TableY.value = 'X' ";
Query query = session.createQuery(hql);
Query query = session.createQuery(hql2);

/**
 * {@code list} means <pre> SELECT <pre> 
 */
List<Product> listProducts = query.list();

/** FOR EXAMPLE **/
String hql = "from Product where category.name = 'Computer'";
Query query = session.createQuery(hql);

List<Product> listProducts = query.list();

for (Product aProduct : listProducts) {
    System.out.println(aProduct.getName());
}
```

### `setParameter` method

```java
String hql = "from table where value like :keyword";

/**
 * <p> keyword </p>
 */
String keyword = "New";

/**
 * <p> create a query 
 *     that asks for {@code keyword} </p>
 */
Query query = session.createQuery(hql);

/* 
 * {@code setParameter(String Keyword, "%" + String value + "%")}
 */
query.setParameter("keyword", "%" + keyword + "%");

/**
 * <pre> SELECT from table where value like :keyword 
 * </pre>
 */
List<ModelClass> listProducts = query.list();
```

### insert

```java
S
tring hql = "insert into Category (id, name)" + " select id, name from OldCategory";
Query query = session.createQuery(hql);
 

int rowsAffected = query.executeUpdate();
if (rowsAffected > 0) {
    System.out.println(rowsAffected + "(s) were inserted");
}
```

### do multiple queries at same time

For example :: Update & Delete in once
```java
String hqlUpdate =  "update table set valueA_ = :valueA where valueB_ = :valueB";
String hqlDel = "delete from tableC where Attribute = :value";
Query query = session.createQuery(hql);

int valueA = ... ;
int valueB = ... ;

query.setParameter(valueA_, valueA);
query.setParameter(valueB_, valueB);
 
int rowsAffected = query.executeUpdate();
```

```java
/** FOR EXAMPLE **/
String hql = "update Product set price = :price where id = :id";
Query query = session.createQuery(hql);

query.setParameter("price", 488.0f);
query.setParameter("id", 43l);
 
int rowsAffected = query.executeUpdate();
if (rowsAffected > 0) {
    System.out.println("Updated " + rowsAffected + " rows.");
}

String hql = "delete from OldCategory where id = :catId";

Query query = session.createQuery(hql);
query.setParameter("catId", new Long(1));
 
int rowsAffected = query.executeUpdate();
if (rowsAffected > 0) {
    System.out.println("Deleted " + rowsAffected + " rows.");
}
```

## Join Query 

HQL supports the following join types (similar to SQL):  
- `innerjoin` (can be abbreviated as join).
- `leftouterjoin` (can be abbreviated as `leftjoin`).
- `rightouterjoin` (can be abbreviated as `rightjoin`).
- `fulljoin`


```java
/**
 * <p> a query that retrieves results  
 *     which is a join between 
 *     two tables Product and Category
 * </p>
 */
String hql = "from Product p inner join p.category";
Query query = session.createQuery(hql);

/**
  * {@code Object[]}
  */
List<Object[]> listResult = query.list();
 
for (Object[] aRow : listResult) {
    Product product = (Product) aRow[0];
    Category category = (Category) aRow[1];
    System.out.println(product.getName() + " - " + category.getName());
}
```

Using the join keyword in HQL is called explicit join.  

- Note that a JOIN query returns a list of Object arrays, so we need to deal with the result set differently:  
  > `List<Object[]> listResult = query.list();`

- HQL provides with keyword which can be used in case you want to supply extra join conditions. For example:  
  > `from Product p inner join p.category with p.price > 500`
  > That joins the Product and Category together with a condition specifies that product’s price must be higher than 500.  

- we can write implicit join query which uses dot-notation. For example:  
  > `from Product where category.name = 'Computer'`  
  >　That result in innerjoin in the resulting SQL statement.  
 
## Hibernate Sort Query Example

Sorting in HQL is very similar to SQL using `ORDER BY` clause follows by a sort direction `ASC `(ascending) or `DESC` (descending).
```java 
String hql = "from Product order by price ASC";
 
Query query = session.createQuery(hql);
List<Product> listProducts = query.list();
 
for (Product aProduct : listProducts) {
    System.out.println(aProduct.getName() + "\t - " + aProduct.getPrice());
}
```

## Hibernate Group By Query Example
Using `GROUPBY` clause in HQL is similar to SQL.  

```java
/**
 * <p> To query summarizes price of 
 *     all products grouped by each category
 * </p>
 * <pre> select sum(p.price), p.category.name from Product p group by category </pre>
 */
String hql = "select sum(p.price), p.category.name from Product p group by category";
 
Query query = session.createQuery(hql);
List<Object[]> listResult = query.list();
 
for (Object[] aRow : listResult) {
    Double sum = (Double) aRow[0];
    String category = (String) aRow[1];
    System.out.println(category + " - " + sum);
```


## Pagination Query Example (count of retrieving data)
To return a subset of a result set, the Query interface has two methods for limiting the result set:


```java 
/**
 * <p> List First 10 Products 
 * </p>
 */
String hql = "from Product";
 
/**
 * @Description
 * {@code setFirstResult(int_FirstResult)}: 
 *   sets the first row to retrieve.
 * {@code setMaxResults(int_MaxResults)}:
 *   sets the maximum number of rows to retrieve.
 */
Query query = session.createQuery(hql);
query.setFirstResult(0);
query.setMaxResults(10);
 
List<Product> listProducts = query.list();
 
for (Product aProduct : listProducts) {
    System.out.println(aProduct.getName() + "\t - " + aProduct.getPrice());
}
```

## Date Range Query Example
A nice feature of Hibernate is that it is able to defer parameter type to generate the resulting SQL statement accordingly.  

```java 
/**
 * <pre> from .. where .. :xx and .. :yy 
 * <p> List only orders whose purchase date is in a specified range.
 * </p>
 * To use Date Range Query In HQL
 * We need to use {@code SimpleDateFormat("yyyy-mm-dd")}
 */
String hql = "from Order where purchaseDate >= :beginDate and purchaseDate <= :endDate";
 
Query query = session.createQuery(hql);
 
 /**
  * <p> create a instance of {@code SimpleDateFormat} 
  * </p>
  */
SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

Date beginDate = dateFormatter.parse("2014-11-01"); 
query.setParameter("beginDate", beginDate);
 
Date endDate = dateFormatter.parse("2014-11-22");
query.setParameter("endDate", endDate);
 
List<Order> listOrders = query.list();
 
for (Order anOrder : listOrders) {
    System.out.println(anOrder.getProduct().getName() + " - "
            +  anOrder.getAmount() + " - "
            + anOrder.getPurchaseDate());
}
```

## Using Expressions in Hibernate Query
For expressions used in the WHERE clause, HQL supports all basic arithmetic expressions similar to SQL include the following:

- mathematical operators: `+`, `-`, `*`, `/`
- binary comparison operators: `=`, `>=`, `<=`, `<>`, `!=`, `like`
- logical operators: `and`, `or`, `not`

For instance, the following query returns only products with price is ranging from 500 to 1000 dollars:
`from Product where price >= 500 and price <= 1000`

## Using Aggregate Functions in Hibernate Query

HQL supports the following aggregate functions:
```sql
avg(…) 
sum(…) 
min(…)
max(…)
count(*)
count(…)
count(distinct…)
count(all…)
```

For example, the following :
```java
/** 
 * <p> query total counts of the products
 * <pre> select count(name) from Product </pre>
 */
String hql = "select count(name) from Product";
Query query = session.createQuery(hql);
List listResult = query.list();

Number number = (Number) listResult.get(0);
System.out.println(number.intValue());
```
