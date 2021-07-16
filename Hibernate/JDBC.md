###### tags: `Hibernate`
# JDBC
[TOC]

[REFERENCE](https://www.codejava.net/frameworks/spring/spring-jdbc-template-simple-example)

Access relational Database using JDBC with Spring Framework  

# Using JdbcTemplate methods
To use JdbcTemplate, we must inject a Data Source reference either via constructor or setter when instantiating a new JdbcTemplate object

## Inject a Data Source (Code based Configuration)
To set up database via `SimpleDriverDataSource`
```java
SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
dataSource.setDriver(new com.xxx.xxx.Driver());
dataSource.setUrl(".....");
dataSource.setUsername("....");
dataSource.setPassword("....");
```

## Define JdbcTemplate
```java
/**
 * Configure JdbcTemplate via
 * {@code JdbcTemplate(SimpleDriveDataSource dataSoruce)}
 */
JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

/**
 * Configure JdbcTemplate via
 *    {@setDataSource(SimpleDriverDataSource dataSource)}
 */
JdbcTemplate jdbcTemplate = new JdbcTemplate();
jdbcTemplate.setDataSource(dataSource);
```

## Operations of JdbcTemplate
To execute updates and queries
```java
String query= "INSERT INTO ..."
jdbcTemplate.update(query);

// Using RowMapper return a List of object
List<T> results = jdbcTemplate.query("SELECT * FROM ...", new RowMapper<T>() {...});
```

# CRUD operation using JDBC
## Database
Create a table named `contact`

```mysql
CREATE DATABASE contactdb;
 
CREATE TABLE `contact` (
  `contact_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `address` varchar(45) NOT NULL,
  `telephone` varchar(45) NOT NULL,
  PRIMARY KEY (`contact_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```
## Configuring Dependencies in **MAVEN** (pom.xml)
Add two dependencies **JDBC, MySql** dependencies in `pom.xml`

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version> ... </version>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version> .... </version>
</dependency>
```

## Configure Model Class

```java
// packages

@Data
@AllargsConstructor
@NoargsConstructor
public class Contact {
    private String name;
    private String email;
    private String address;
    private String phone;
 
    }
}
```

## Run the program

```java=
 import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
 
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
 
public class SpringJDBCTemplateExample {
    public static void main(String[] args) throws SQLException {
    
        /**
         * <p> Connect DataSource via
         *     {@code SimpleDriverDataSource()}
         * </p>
         */
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new com.mysql.jdbc.Driver());
        dataSource.setUrl("jdbc:mysql://localhost/contactdb");
        dataSource.setUsername("root");
        dataSource.setPassword("root_password");
         
        /**
         * Access data source using 
         *  {@code JdbcTemplate}
         */
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
 
        // JDBC operation : insert 
        String sqlInsert = 
            "INSERT INTO contact (name, email, address, telephone)" + 
            " VALUES (?, ?, ?, ?)";
        /**
         * update via sqlInse
         */
        jdbcTemplate.update(sqlInsert, "Tom", "tomea@mail.com", 
                                "USA", "12345");
         
        // Update
        String sqlUpdate = "UPDATE contact set email=? where name=?";
        jdbcTemplate.update(sqlUpdate, "tomee@mail.com", "Tom");
        
        // Select (all the Contacts)
        String sqlSelect = "SELECT * FROM contact";
        List<Contact> listContact = jdbcTemplate.query(sqlSelect, 
            new RowMapper<Contact>() 
            {
                public Contact mapRow(ResultSet result, int rowNum) 
                                        throws SQLException 
                {
                    Contact contact = new Contact();
                    contact.setName(result.getString("name"));
                    contact.setEmail(result.getString("email"));
                    contact.setAddress(result.getString("address"));
                    contact.setPhone(result.getString("telephone"));

                    return contact;
                }

            });
         
        for (Contact aContact : listContact) {
            System.out.println(aContact);
        }
 
}
```

#### To catch the exception `DataAccessException` explicitly

For example
```java=
try {
    String sqlDelete = "DELETE FROM contact where name=?";
    jdbcTemplate.update(sqlDelete, "Tom");
 
} catch (DataAccessException ex) {
    ex.printStackTrace();
}
```



# Spring MVC with JdbcTemplate

[Reference](https://www.codejava.net/frameworks/spring/spring-mvc-with-jdbctemplate-example)

1. Create MySQL database
2. Create Maven Project (poem.xml ... etc)
3. Create Model Classes
4. Create DAO Classes
5. Spring MVC Configuration
6. Configure Spring MVC Dispatcher Servlet (web.xml ...)
7. Create Controller Class
8. Create .jsp web pages


# NamedParameterJdbcTemplate


[Reference](https://www.codejava.net/spring-tutorials)

Using placeholder parameters seems to be convenient, but it causes readability 

Therefore, Spring JDBC provides the `NamedParameterJdbcTemplate` class to solve the issues which may be caused by using placeholder parameters with `JdbcTemplate` class

The `NamedParameterJdbcTemplate` class is a wrapper of `JdbcTemplate` class so it has the same methods names. 
The differences are in the parameters that allow you to use named parameters for SQL statements.

For exmple using named parameter instead of ? 
```java=
NamedParameterJdbcTemplate template = 
    new NamedParameterJdbcTemplate(dataSource);
 
// String sql = "INSERT INTO contact (name, email, address)
//      VALUES (?, ?, ?)";
String sql = "INSERT INTO Contact (name, email, address) \
                    VALUES (:name, :email, :address)";
 
Map<String, String> params = new HashMap<>();
 
params.put("name", "Tom");
params.put("email", "tomea@gmail.com");
params.put("address", "USA");
 
template.update(sql, params);
```

## Class `MapSqlParameterSource`

Instead of using 
```java=
NamedParameterJdbcTemplate template = 
    new NamedParameterJdbcTemplate(dataSource);
// ...
Map<String,String> params = new HashMap<>();
params.put("name", "Tim Hardy");
params.put("email", "1234@gmail.com");
params.put("address", "New York,USA");
//...
```

we can use `MapSqlParameterSource` to reduce the code
```java=
NamedParameterJdbcTemplate template = 
    new NamedParameterJdbcTemplate(dataSource);

String query = "INSERT INTO Contact (name, email, address) \
              VALUES (:name, :email, :address)";
             
MapSqlParameterSource params = new MapSqlParameterSource();
params.addValue("name", "Tim Hardy")
      .addValue("email", "1234@gmail.com")
      .addValue("address", "New York,USA");

// jdbcTemplate.update(sql, "Tim Hardy", "1234@gmail.com", "New York,USA");
template.update(query, params);
```

`new MapSqlParameterSource("AttributeinDatabase" , "value")`
```java=
NamedParameterJdbcTemplate template = 
    new NamedParameterJdbcTemplate(dataSource);
 
String sql = "SELECT * FROM Contact WHERE name=:name";
SqlParameterSource param = new MapSqlParameterSource("name", "Tom Harrison");
 
Contact result = template.queryForObject(sql, param, 
        BeanPropertyRowMapper.newInstance(Contact.class));
```


#### Model Class of NamedParameterJdbcTemplate

```java=
public void ex(calss<?> obj){
    NameParameterJdbcTemplate template = 
        new NamedParameterJdbcTemplate(dataSource);
    String sql = /* operations */ ;
    BeenPropertySqlParameterSource pSource =
        new BeenPropertySqlParameterSource(obj);
    template.update(sql, paramSource);
}
```
For example
```java=
public void save(Contact contact) {
    NamedParameterJdbcTemplate template = 
            new NamedParameterJdbcTemplate(dataSource);
 
    String sql = "INSERT INTO Contact (name, email, address) \
                    VALUES (:name, :email, :address)";
 
    BeanPropertySqlParameterSource paramSource = 
            new BeanPropertySqlParameterSource(contact);
 
    template.update(sql, paramSource);
}
```


## `ResultSetExtractor<?>` returns the object


To return a object
```javascript=
NamedParameterJdbcTemplate template = 
    new NamedParameterJdbcTemplate(dataSource);
 
String sql = "SELECT * FROM Contact WHERE name=:name";
 
SqlParameterSource param = 
    new MapSqlParameterSource("name", "Tom");

// search a person whoses name is Tom
//     returns a Contact object
Contact result = template.query(sql, param, 
        new ResultSetExtractor<Contact>() 
        {
            @Override
            public Contact extractData(ResultSet rs) 
                throws SQLException, DataAccessException {
                if (rs.next()) {
                    Contact contact = new Contact();
                    contact.setId(rs.getInt("contact_id"));
                    contact.setName(rs.getString("name"));
                    contact.setEmail(rs.getString("email"));
                    contact.setAddress(rs.getString("address"));

                    return contact;
                }
                return null;
            }
        });
```

## `BeanPropertyRowMapper` instead of `ResultSetExtractor`
 
```java 
NamedParameterJdbcTemplate template = 
    new NamedParameterJdbcTemplate(dataSource);
 
String sql = "SELECT * FROM Contact WHERE name=:name";
 
SqlParameterSource param = 
    new MapSqlParameterSource("name", "Tom");
 
// queryForObject(qeury, sqlParameterSource, 
//     BeenPropertyRowMapper.newIstance(Type.class) )
Contact result = template.queryForObject(sql, param, BeanPropertyRowMapper.newInstance(Contact.class));
```


Examples for listing objects
```java=
NamedParameterJdbcTemplate template = 
    new NamedParameterJdbcTemplate(dataSource);
 
String sql = "SELECT * FROM Contact WHERE email LIKE :email";
 
SqlParameterSource param = new MapSqlParameterSource("email", "%gmail.com%");
 
List<Contact> result = template.query(sql, param, new RowMapper() 
    {
        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException     {

            Contact contact = new Contact();
            contact.setId(rs.getInt("contact_id"));
            contact.setName(rs.getString("name"));
            contact.setEmail(rs.getString("email"));
            contact.setAddress(rs.getString("address"));

            return contact;
        }
    });


// Using BeanPropertyRowMapper
NamedParameterJdbcTemplate template = 
    new NamedParameterJdbcTemplate(dataSource);
 
String sql = "SELECT * FROM Contact WHERE email LIKE :email";
 
SqlParameterSource param = new MapSqlParameterSource("email", "%gmail.com%");
 
List<Contact> result = template.query(sql, param,
    BeanPropertyRowMapper.newInstance(Contact.class));

```


# SimpleJdbc

Instead of Jdbctemplate SimpleJdbc makes code simpler 

- SimpleJdbcCall (do Query)
    > Procedure and Function 

- SimpleJdbcInsert


## SimpleJdbcCall

Usage 
```java
// set our dataSource ...
SimpleJdbcCall actor = 
    new SimpleJdbcCall(dataSource).withProcedureName("The_Produce_Your_Create");

// to find value1 and value2
SqlParameterSource params = new MapSqlParameterSource();
params.addValue("in_param_1", "value1")
      .addValue("in_param_2", "value2");

Map<String, Object> out = actor.execute(params);
String value1 = (String) out.get("out_param_1");
Integer value2 = (Integer) out.get("out_param_2");
```


### Call a stored procedure with IN and OUT parameters


A Procedure
> find the specific id 
```sql=
CREATE PROCEDURE `get_contact`(IN contact_id INTEGER,
    OUT _name varchar(45),
    OUT _email varchar(45),
    OUT _address varchar(45),
    OUT _phone varchar(45))
BEGIN
    SELECT name, email, address, telephone
    INTO _name, _email, _address, _phone
    FROM Contact WHERE id = contact_id;
END
```

To invoke Procedure by SimpleJdbcCall
```java=
// The Id we want to find
int contactId = 10;

SimpleJdbcCall actor = 
    // Procedure named get_contact
    new SimpleJdbcCall(dataSource).withProcedureName("get_contact");
 
SqlParameterSource inParams = 
    new MapSqlParameterSource().addValue("contact_id", contactId);

// To invoke Procedure by SimpleJdbcCall by giving a specific id
Map<String, Object> outParams = actor.execute(inParams);
String name = (String) outParams.get("_name");
String email = (String) outParams.get("_email");
String address = (String) outParams.get("_address");
String phone = (String) outParams.get("_phone");
System.out.println(name + ", " + email + ", " + address + ", " + phone);
```


### SimpleJdbcCall calls a procedure that return a result set

A Procedure
> Show all attributes from table Contact ordered by name
```sql=
CREATE PROCEDURE `list_contact`()
BEGIN
    SELECT * FROM Contact ORDER BY name ASC;
END
```

To invoke Procedure using SimpleJdbcCall
```java=
SimpleJdbcCall procedureActor = new SimpleJdbcCall(dataSource)
        .withProcedureName("list_contact")
        .returningResultSet("contacts", new RowMapper<Contact>() 
        {
            @Override
            public Contact mapRow(ResultSet rs, int rowNum)
                                     throws SQLException             
            {
                Contact contact = new Contact();
                contact.setId(rs.getInt("id"));
                contact.setName(rs.getString("name"));
                contact.setEmail(rs.getString("email"));
                contact.setAddress(rs.getString("address"));
                contact.setTelephone(rs.getString("telephone"));
                return contact;
            }
        });
Map<String, Object> out = procedureActor.execute();
List<Contact> listContacts = (List<Contact>) out.get("contacts");

```

### SimpleJdbcCall(dataSource).withFunctionName(...) to execute a function

```sql=
CREATE FUNCTION `calculate_book_rating`(book_title varchar(128)) RETURNS double
    READS SQL DATA
BEGIN
    DECLARE out_value DOUBLE;
    SELECT AVG(r.rating) AS AvgRating FROM Review r JOIN Book b
        ON r.book_id = b.book_id AND b.title = book_title
        INTO out_value;
RETURN out_value;
END
```

```java=
SimpleJdbcCall procedureActor = 
new SimpleJdbcCall(dataSource).withFunctionName("calculate_book_rating");
 
String bookTitle = "Effective Java (3rd Edition)";
 
Double rating = procedureActor.executeFunction(Double.class, bookTitle)
```

## SimpleJdbcInsert

To insert data using `SimpleJdbcInsert`
```java=
SimpleJdbcInsert insertActor = new SimpleJdbcInsert(dataSource);
insertActor.withTableName("contact");

Map<String, Object> params = new HashMap<>();
params.put("name", "Tom");
params.put("email", "tom@gmail.com");
params.put("address", "Seattle, USA");
int result = insertActor.execute(params);
if (result > 0) {
    System.out.println("Insert Successfully!");
}

// the Above is Same as the following
JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
String sqlInsert = "INSERT INTO contact (name, email, address) VALUES (?, ?, ?)";
jdbcTemplate.update(sqlInsert, "Tom", "tom@gmail.com", "Seattle, USA");
```

### Return value of the auto-generated primary key

```java=
SimpleJdbcInsert insertActor = new SimpleJdbcInsert(dataSource);
insertActor.withTableName("contact").usingGeneratedKeyColumns("contact_id");
//..
Number newId = insertActor.executeAndReturnKey(params);

if (newId != null) {
    System.out.println("Insert Successfully. New Id = " + newId.intValue());
}
```
For example 
```java=
SimpleJdbcInsert insertActor = new SimpleJdbcInsert(dataSource);
insertActor.withTableName("contact").usingGeneratedKeyColumns("contact_id");

MapSqlParameterSource params = new MapSqlParameterSource();
params.addValue("name", "Tom")
      .addValue("email", "tom@gmail.com")
      .addValue("address", "Seattle, USA");
 
Number newId = insertActor.executeAndReturnKey(params);
 
if (newId != null) {
    System.out.println("Insert Successfully. New Id = " + newId.intValue());
}`
```
> `executeAndReturnKey()` 
> : returns a Number object holding value of the auto-generated key of the primary column.

### `BeanPropertySqlParameterSource` to insert object of Model Class


model class
```java=
public class Contact{
    private String name;
    //...
}
```

Insert A new Contact
```java=
Contact New_Contact_John = new Contact(/*...*/) ;

// Define SimpleJdbcInsert and which table to be inserted 
SimpleJdbcInsert insertActor = new SimpleJdbcInsert(dataSource);
insertActor.withTableName("contact");
// Define action of `insert a object`
BeanPropertySqlParameterSource paramSource = 
    new BeanPropertySqlParameterSource(contact);
// Do Inserting A object
int result = insertActor.execute(paramSource);
 
if (result > 0) 
{
    System.out.println("Insert Successfully!");
}
```

# Batch Update with Jdbctemplate 

Consider a Entity User
```java=
public class User {
    private Integer id;
    private String email;
    private String password;
    private String name;
     
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    //.....
}
```


## Using Jdbctemplate.batchUpdate

Syntax 
```java=
Jdbctemplate.batchUpdate([]queries, {lambda});
// or
Jdbctemplate.batchUpdate(quesry1, questy2, queary3, ...);
```

Do Insert / Update / Delete by Batch
```java=
// Insert , Update, Delete queries
String sql1 = 
    "INSERT INTO Users (email, pass, name) VALUES ('email0', 'pass0', 'name0')";
String sql2 = 
    "UPDATE Users SET password='default' WHERE user_id < 10";
String sql3 = 
    "DELETE FROM Users WHERE email = ''";
     
int[] updateCounts = template.batchUpdate(sql1, sql2, sql3);

// or ...
String[] sqlArray = {
        "INSERT INTO Users (email, pass, name) VALUES ('email0', 'pass0', 'name0')",
        "INSERT INTO Users (email, pass, name) VALUES ('email1', 'pass1', 'name1')",
        "INSERT INTO Users (email, pass, name) VALUES ('email2', 'pass2', 'name2')",
}; 
 
int[] updateCounts = template.batchUpdate(sqlArray);
```

## Batch Updates with `PreparedStatement` 


```java=
public void batchInsert(List<User> listUsers) {
    String sql = "INSERT INTO Users (email, pass, name) VALUES (?, ?, ?)";
         
    int[] updateCounts = template.batchUpdate(sql,
            new BatchPreparedStatementSetter() 
            {
                 
                @Override
                public void setValues(PreparedStatement ps, int i) 
                                throws SQLException
                {
                    User user = listUsers.get(i);
                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getPassword());
                    ps.setString(3, user.getFullname());
                }
                 
                @Override
                public int getBatchSize() {
                    return 100;
                }
            });
         
}
```

## Multiple Batches Update 


```java=
String sql = "INSERT INTO Users (email, password, full_name) VALUES (?, ?, ?)";
 
List<User> batchArgs = new ArrayList<>();
 
for (int i = 1; i <= 1000; i++) {
    batchArgs.add(new User("email-" + i, "password-" + i, "fullname-" + i));
}
 
int batchSize = 50;
 
int[][] updateCounts = template.batchUpdate(sql, batchArgs, batchSize,
        new ParameterizedPreparedStatementSetter<User>() {
            @Override
            public void setValues(PreparedStatement ps, User user) throws SQLException {
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getFullname());
            }
        });
```