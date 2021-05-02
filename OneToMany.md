<style>
html,
body, 
.ui-content,
/*Section*/
.ui-toc-dropdown{
    background-color: #1B2631;
    color: #9BCBFC;
}

body > .ui-infobar {
    display: none;
}
.ui-view-area > .ui-infobar {
    display: block ;
    color: #5D6D7E ;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body blockquote{	
    /*#7FFFD4*/
    /*#59FFFF*/
    color: #7FFFD4;
}


/* > */
.markdown-body blockquote {
color: #9BCBFC ;
border-left-color: #B22222 ;
font-size: 16px;
}

.markdown-body h6{
    color: gold;
}
.markdown-body h1,
.markdown-body h2 {
    border-bottom-color: #5D6D7E;
    border-bottom-style: ;
    border-bottom-width: 3px;
}

.markdown-body img {
    background-color: transparent;
}

/* [](htpp://) */
a,.open-files-container li.selected a {
    color: #D6EAF8;
}

/* == == */
.markdown-body mark,
mark 
{
    background-color: #708090 !important ;
    color: gold;
    margin: .1em;
    padding: .1em .2em;
    font-family: Helvetica;
}

/* scroll bar */
.ui-edit-area .ui-resizable-handle.ui-resizable-e {
background-color: #303030;
border: 1px solid #303030;
box-shadow: none;
}

/* info bar */
.ui-infobar {
color: #999;
}

/* `` */
.markdown-body code,
.markdown-body tt {
    color: #eee;
    background-color: #424a55;
}

/* ``` ``` */
.markdown-body pre {
background-color: #1e1e1e;
border: 1px solid #555 !important;
color: #dfdfdf;
}

/* scroll bar */
.ui-edit-area .ui-resizable-handle.ui-resizable-e {
background-color: #303030;
border: 1px solid #303030;
box-shadow: none;
}

/* info bar */
.ui-infobar {
color: #999;
}

/*----Prism.js -----*/
code[class*="language-"],
pre[class*="language-"] {
color: #DCDCDC;
}

:not(pre)>code[class*="language-"],
pre[class*="language-"] {
background: #1E1E1E;
}

.token.comment,
.token.block-comment,
.token.prolog,
.token.cdata {
color: #57A64A;
}

.token.doctype,
.token.punctuation {
color: #9B9B9B;
}

.token.tag,
.token.entity {
color: #569CD6;
}

.token.attr-name,
.token.namespace,
.token.deleted,
.token.property,
.token.builtin {
color: #9CDCFE;
}

.token.function,
.token.function-name {
color: #dcdcaa;
}

.token.boolean,
.token.keyword,
.token.important {
color: #569CD6;
}

.token.number {
color: #B8D7A3;
}

.token.class-name,
.token.constant {
color: #4EC9B0;
}

.token.symbol {
color: #f8c555;
}

.token.rule {
color: #c586c0;
}

.token.selector {
color: #D7BA7D;
}

.token.atrule {
color: #cc99cd;
}

.token.string,
.token.attr-value {
color: #D69D85;
}

.token.char {
color: #7ec699;
}

.token.variable {
color: #BD63C5;
}

.token.regex {
color: #d16969;
}

.token.operator {
color: #DCDCDC;
background: transparent;
}

.token.url {
color: #67cdcc;
}

.token.important,
.token.bold {
font-weight: bold;
}

.token.italic {
font-style: italic;
}

.token.entity {
cursor: help;
}

.token.inserted {
color: green;
}
  
</style>

###### tags: `Hibernate`
# Database table relationships
[TOC]
 
- one-to-many 
     > is the most common relationship, and ==it associates a row from a parent table to multiple rows in a child table.==  
- one-to-one 
     >requires the child table Primary Key to be associated via a Foreign Key with the parent table Primary Key column.  
- many-to-many 
   > requires a link table containing two Foreign Key columns that reference the two different parent tables.  


## `@OneToMany`

### [Link of Reference](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/)

For example
>![](https://i.imgur.com/Db6bn7z.png)
>> A Post can have many comments


#### Type of `@OneToMany`
1. a unidirectional `@OneToMany `association
2. a bidirectional `@OneToMany` association

- One Side we can call it `Father Entity` or `MappedBy Side`
- Many Side we can call it `Child Entity` or `Owning Side`

#### Why Call it Father(MappedBy) and Child(Owning)?
> Concept
> : If Father doesn't exist then Child will not exist  

Because `Foreign Key` in Child Entity (which references to Father entity's Primary key)
> Child's Entity is dependent on Father Entity's Primary


- So as in database
    > To **identify** Child Entity (Weak Entity).  
    > The Foreign key in Child Entity must be mapped by the primary Key in Father Entity. 
    > `Mapped by` means `Maintained By`

To see why bidirectional is better than others, we start from Unidirectional association
### Unidirectional `@OneToMany`

```java=
/* MappedBy Side */
@Entity(name = "Post")
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    
    // A post can have many Comments
    @OneToMany(cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();
 
    public List<PostComment> getComments(){
        return comments;
    }
    // ....
}

/* Owning Side */
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
```java=
Post post = new Post("first Post");
post.getComments().add(new PostComment("My first review"));
post.getComments().add(new PostComment("My Second review"));
post.getComments().add(new PostComment("My Third review"));
```

Hibernate will execute SQL statements like this
```sql=
/* Create A New Post */
insert into post (title, id)
values ('First post', 1)

/* 3 Comments  */
insert into post_comment (review, id)
values ('My first review', 2)
insert into post_comment (review, id)
values ('My second review', 3)
insert into post_comment (review, id)
values ('My third review', 4)

/* Create A Relation Mapping Table */
insert into post_post_comment (Post_id, comments_id)
values (1, 2)
insert into post_post_comment (Post_id, comments_id)
values (1, 3)
insert into post_post_comment (Post_id, comments_id)
values (1, 4)
```
![](https://i.imgur.com/935wP0Z.png)
- So we got a extra table to link other two tables with extra two of Foreign Keys



## Unidirectional `@OneToMany` with `@JoinColumn`

with `@JoinColumn` annotation it helps Hibernate to know that **there is a post_id `Foreign Key` column in the `post_comment` table**

- We just need to add `@JoinColumn` in MappedBy Side

```java=
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
// tell hibernate there is a foreign key 
//     called `post_id` in post_comment table
@JoinColumn(name = "post_id")
private List<PostComment> comments = new ArrayList<>();
```

now Hibernate will execute like this way
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
* Because of @JoinColum(name = "post_id")
*   Hibernate will set up post_comment's ForeignKey
*/
update post_comment set post_id = 1 where id = 2
update post_comment set post_id = 1 where id = 3
update post_comment set post_id = 1 where id = 4
```

- Hibernate inserts the child records first without the Foreign Key since the child entity does not store this information (post_id).  
    > During the collection handling phase, the Foreign Key column is updated accordingly.

If we need to delete a record in the child entity
```java=
post.getComments().remove(0);
```

Hibernate will work like this
```sql=
update post_comment 
/* post_comment's id=2's freign key references to null */
set post_id = null 
where post_id = 1 and id = 2

/* delete row of post_comment's id=2's */
delete from post_comment where id=2
```


## Unidirectional `@OneToMany` with `@JoinTable`

### [Link to Example](https://stackoverflow.com/questions/5478328/in-which-case-do-you-use-the-jpa-jointable-annotation)

```java=
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


## Bidirectional `@OneToMany(MappedBy)`

- The `mappedBy` element defines a bidirectional relationship.  
    > This attribute allows you to refer the associated entities from both sides

**Every bidirectional association must have one owning side only (the child side), the other one being referred to as the inverse (or the mappedBy) side.**
- Owning Side ( Child ==*(Many)*== entity)
- mappedBy Side ( Fahter ==*(is referred by child entity)*== entity) 

```java=
@Entity(name = "Post")
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
 
    @OneToMany(
        // post_comment's FK is maintained by post
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PostComment> comments = new ArrayList<>();
 
    // To synchronize both sides of the bidirectional association.
    //     we need the followings
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
- With attribute `mappedBy="post"` **tells hibernate that post's PK is used/referenced by child entity's FK**
    > which means post maintains post_comment's FK

By property of `mappedBy` , the hibernate will do like 
```sql=
/* Create Post */
insert into post (title, id)
values ('First post', 1)
/* post will maintain post_comment*/
insert into post_comment (post_id, review, id)
values (1, 'My first review', 2)
insert into post_comment (post_id, review, id)
values (1, 'My second review', 3)
insert into post_comment (post_id, review, id)
values (1, 'My third review', 4)
```


## Usage of `@JoinColumn`

Syntax 
`@JoinColum( name = Relation_AttributeName, referencedColumnName = Where Relation_AttributeName refers to)`

For example 
A Company can have lot of branches
```java=
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
    @JoinColumn(name = "companyId, referencedColumnName = "id")
    private List<Branch> branches = new ArrayList<>();
    //...
}

/* Owning Side*/
@Entity
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private string name;
    // ...
}
```
- The Entity inside `@JoinColum` , attribute `name` refers to foreign key column name which is `companyId` and attribute `rferencedColumnName` indicates the primary key `id` of the entity company to which the foreign key `companyId` refers  

> [Synchronize](https://vladmihalcea.com/jpa-hibernate-synchronize-bidirectional-entity-associations/)
> [EagerFetching](https://vladmihalcea.com/eager-fetching-is-a-code-smell/)



