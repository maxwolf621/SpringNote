###### tags: `Hibernate`  
# Database table relationships  
[Reference](https://stackoverflow.com/questions/3113885/difference-between-one-to-many-many-to-one-and-many-to-many)  

- one-to-many 
  > is the most common relationship, and ==it associates a row (PK) from a parent table to multiple rows in a child table.==  
- one-to-one 
  >requires the child table Primary Key to be associated via a Foreign Key with the parent table Primary Key column.  
- many-to-many 
  > requires a link table containing two Foreign Key columns that reference the two different parent tables.     
  > [Many To Many Association](/tJJtCj7_Rs6XlLsqLOtG2A)    
  
## Difference btw One-To-Many and Many-To-Many  

The Difference btw them is `reusability` 

One-to-Many: One Person Has Many Skills, a Skill is not **reused** between Person(s)  
- Unidirectional: A Person can directly reference Skills via its Set  
- Bidirectional: Each "child" Skill has a single pointer back up to the Person (which is not shown in your code)  

Many-to-Many: One Person Has Many Skills, a Skill is **reused** between Person(s)
- Unidirectional: A Person can directly reference Skills via its Set
- Bidirectional: A Skill has a Set of Person(s) which relate to it.


In a One-To-Many relationship, one object is the parent and one is the "child".   
**The parent controls the existence of the child.**     

In a Many-To-Many, the existence of either type is dependent on something outside the both of them (in the larger application context).  

```diff
+ Many-To-Many Bidirectional relationship does not need to be symmetric!  
　That is, a bunch of People could point to a skill, 
  but the skill needs not relate back to just those people. 
  Typically it would, but such symmetry is not a requirement.  
```
## Attribute of the Relationship

Orphan Removal
- JPA 2 supports an additional and more aggressive remove cascading mode which can be specified using the orphanRemoval element of the `@OneToOne` and `@OneToMany` annotations
  > If `orphanRemoval=true` is specified the disconnected Address instance is automatically removed. 
  > **The attribute is useful for cleaning up dependent objects** (e.g. Address) that should not exist without a reference from an owner object (e.g. Employee).

#### For example A Post can have many comments
The relationship is based on the Foreign Key column (e.g. `post_id`) in the child table.  
![](https://i.imgur.com/Db6bn7z.png)  


## [`@OneToMany`](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/)  

Type of `@OneToMany`
1. a unidirectional `@OneToMany `association
2. a bidirectional `@OneToMany` association

- One Side we can call it `Father Entity` or `MappedBy Side`
- Many Side we can call it `Child Entity` or `Owning Side`

## Father(MappedBy) and Child(Owning)?

Concept  
If Father doesn't exist then Child will not exist which means child must be dependent on father   
- The Foreign Key in Child Entity (which references to Father entity's Primary key)

### Unidirectional `@OneToMany`

```java
/**
 * MappedBy Side
 */
@Entity(name = "Post")
@Table(name = "post")
public class Post {
    
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    
    /** A post can have many Comments
     *   {@code orphanRemoval} :　Each comment should reference to post
     *   {@code cascadeType.all}　: If post does not exists then comments should be deleted
     */
    @OneToMany(cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();
 
    public List<PostComment> getComments(){
        return comments;
    }
    // other setter and getter ...
}

/** 
 * Owning Side 
 */
@Entity(name = "PostComment")
@Table(name = "post_comment")
public class PostComment {
    @Id
    @GeneratedValue
    private Long id;
    private String review;
    
    //...
}
```

To persist 1 Post and 3 PostComments
```java
Post post = new Post("first Post");
post.getComments().add(new PostComment("My first review"));
post.getComments().add(new PostComment("My Second review"));
post.getComments().add(new PostComment("My Third review"));
```

Hibernate will execute SQL statements like this
```sql
/* Create A New Post */
insert into post (title, id)
values ('First post', 1)

/* Create 3 Comments  */
insert into post_comment (review, id)
values ('My first review', 2)
insert into post_comment (review, id)
values ('My second review', 3)
insert into post_comment (review, id)
values ('My third review', 4)

/** 
 * Create A Relation Mapping Table
 * which references to ownning and mappedby entity's PK
 */
insert into post_post_comment (Post_id, comments_id)
values (1, 2)
insert into post_post_comment (Post_id, comments_id)
values (1, 3)
insert into post_post_comment (Post_id, comments_id)
values (1, 4)
```

![image](https://i.imgur.com/935wP0Z.png)  
- We got a extra table to link other two tables with extra two of Foreign Keys  
  >　Hibernate needs to create post and comments tables and then mapping these two tables together

## `@JoinColumn`

#### (IMPORTANT!!!!) **`@OneToOne`, `@ManyToMany` and `@OneToMany` with `@JoinColumn` have different meaning**


- `@JoinColumn` and `@Column` are almost the same
  >　the difference is `@JoinColumn` describes the **attribute columns btw tables (entities)** and **`@Column` describes attribute in a table**


```java
@OneToOne
@JoinColumn(name = "addr_id")
public AddressEO getAddress() {
         return address;
}
```
- If we don't specify `name="addr_id"` then the default name value is `name=entity_ReferenceTablePrimaryKey`


#### `@JoinColumn(name = table_x_fk , referenceColumnName = ref_pk)`

For example   
Assume given two tables(address and customer) in one-to-one situation
```sql
CREATE TABLE address (
  id int(20) NOT NULL auto_increment,
  ref_id int int(20) NOT NULL,
  province varchar(50) ,
  city varchar(50) ,
  postcode varchar(50) ,
  detail varchar(50) ,
  PRIMARY KEY (id)
)
```

Attribute `address_id` in table `customer` references to attribute `ref_id` in table `address` 
```java
@OneToOne
@JoinColumn(name = "address_id", referencedColumnName="ref_id")
public AddressEO getAddress() {
         return address;
}
```
## Unidirectional `@OneToMany` with `@JoinColumn`

**When using a unidirectional one-to-many association, only the parent side maps the association.**  

So in the post and comments relationship, it means only the `Post` entity will define a `@OneToMany` association to the child `PostComment` entity       
```java
/**
 * Add {@code @JoinColumn = mappedby PK} to our 
 * mappedby side and owning side stays the same
 * --------------------------------------------
 * literally it means 
 * each comment needs update their row called post_id (FK) to this post 
 */
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "post_id")
private List<PostComment> comments = new ArrayList<>();
```

Hibernate will execute like this way
```sql
insert into post (title, id)
values ('First post', 1)

/** 
 * Note that 
 *   post_comment doesn't set up its foreign key 
 */
insert into post_comment (review, id)
values ('My first review', 2)
insert into post_comment (review, id)
values ('My second review', 3)
insert into post_comment (review, id)
values ('My third review', 4)
 
/**
 * Because of 
 * @JoinColum(name = "post_id")
 * Hibernate will join a new column post_id as ForeignKey
 * In post_comment with specific post_id value
 */
update post_comment set post_id = 1 where id = 2
update post_comment set post_id = 1 where id = 3
update post_comment set post_id = 1 where id = 4
```
- Hibernate inserts the child records first without the Foreign Key since the child entity does not store this information (`post_id`).  
  > During the collection handling phase, the Foreign Key column is updated accordingly.

If we need to delete a record in the child entity
```java
post.getComments().remove(0);
```

Hibernate will work like this
```sql
update post_comment 

-- post_comment's id=2's foreign key references to null */
set post_id = null 
where post_id = 1 and id = 2

-- delete row of post_comment's id=2's
delete from post_comment where id=2
```

## [Unidirectional `@OneToMany` with `@JoinTable`](https://stackoverflow.com/questions/5478328/in-which-case-do-you-use-the-jpa-jointable-annotation)

```java
@JoinTable(
        name = "MY_JT",
        joinColumns = @JoinColumn(
                name = "PROJ_ID",
                referencedColumnName = "PID"
        ),
        inverseJoinColumns = @JoinColumn(
                name = "TASK_ID",
                referencedColumnName = "TID"
        )
)
@OneToMany
private List<Task> tasks;
```
![](https://i.imgur.com/0B7yVLD.png)

## Bidirectional `@OneToMany()`

**Every bidirectional association must have one owning side only (the child side), the other one being referred to as the inverse (or the mappedBy) side.**

- Using `mappedBy` element defines a bidirectional relationship.  
    > **This attribute allows you to refer the associated entities from both sides**
 

![](https://i.imgur.com/k6ZZIgv.png)
- `@OneToMany` with the `mappedBy` attribute set, you have a bidirectional association.  
   > In our case, both the Post entity has a collection of `PostComment` child entities, and the child `PostComment` entity has a reference back to the parent Post entity


```java
@Entity(name = "Post")
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
 
    /**
     * this given column is maintained by the anothr entity
     * whose attribute/column anmed `post` 
     */
    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PostComment> comments = new ArrayList<>();
 
    // To synchronize both sides of the bidirectional association.
    public void addComment(PostComment comment) {
        comments.add(comment);
        comment.setPost(this);
    }
    public void removeComment(PostComment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
}
 
@Entity(name = "PostComment")
@Table(name = "post_comment")
public class PostComment {
    @Id
    @GeneratedValue
    private Long id;
    private String review;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostComment )) return false;
        return id != null && id.equals(((PostComment) o).getId());
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    //...
}
```
- With `@JoinColumn` we indicate hibernate that child entity (post_comment) **must have two keys (PK and FK)**

By property of `mappedBy` , the hibernate will do like 
```sql
/* Create Post */
insert into post (title, id)
values ('First post', 1)

/**
  *　post will maintain post_comment
  */
insert into post_comment (post_id, review, id)
values (1, 'My first review', 2)
insert into post_comment (post_id, review, id)
values (1, 'My second review', 3)
insert into post_comment (post_id, review, id)
values (1, 'My third review', 4)
```

### [`mappedBy` and `@JoinColumn`](https://www.baeldung.com/jpa-join-column)

[Reference](https://stackoverflow.com/questions/11938253/jpa-joincolumn-vs-mappedby)

The annotation `@JoinColumn(name = x , referencedColumnName = y)` indicates that this entity is the owner of the relationship **(that is: the corresponding table has a column with a foreign key `x` to the referenced table `y`)**

The attribute `mappedBy` indicates that the entity in this side is the inverse of the relationship, and **the owner(控制權) resides in the other entity.**

#### Syntax of `@JoinColumn`   
```java
@JoinColum( name = this_table_Fk_name , referencedColumnName = "column_from_other_entity" )
private ReferenceToTableName ref(){
    //...
}
```

For example  
A Company can have lot of branches
```java
/* MappedBy side */
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    @OneToMany(targetEntity=Branch.class,
               cascade = CascadeType.ALL, 
               fetch = FetchType.LAZY, 
               orphanRemoval = true)
    /**
      * join a column named companyId as FK and references it to 
      * a column named id in other entity
      */
    @JoinColumn(name = "companyId", referencedColumnName = "id")
    private List<Branch> branches = new ArrayList<>();
    
    //...
}

/* Owning Side*/
@Entity
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    
    private string name;
    // ...
}
```
- The Entity inside `@JoinColum` , attribute `name` refers to foreign key column name which is `companyId` and attribute `rferencedColumnName` indicates the primary key `id` of the entity company to which the foreign key `companyId` refers  

### Two one way relationship 

By specifying the `@JoinColumn` on both models you don't have a two way relationship.   
You have two one way relationships, and a very confusing mapping of it at that.    
You're telling both models that they "own" the identical column.  
Really only one of them actually should!   

[Synchronize](https://vladmihalcea.com/jpa-hibernate-synchronize-bidirectional-entity-associations/)  
[EagerFetching](https://vladmihalcea.com/eager-fetching-is-a-code-smell/)  
[MappedBy](https://stackoverflow.com/questions/9108224/can-someone-explain-mappedby-in-jpa-and-hibernate#:~:text=MappedBy%20signals%20hibernate%20that%20the,constraint%20to%20the%20other%20table.)  
