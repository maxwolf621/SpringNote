# [ManyToMany bidirectional](https://dev.to/jhonifaber/hibernate-onetoone-onetomany-manytoone-and-manytomany-8ba)


[advanced ManyToMany](https://www.baeldung.com/jpa-many-to-many)     

[TOC]

With `@ManyToMany`, we should create a third table (Join Table) so that we can map both entities. 

Join Table will have two FK pointing to their parent tables.  

Because association btw tables is `Many-To-Many`, we can decide which table is owning-side and mapped-by-side

The table which is owning side must have `@JoinTable` annotation on it

```java
@Entity
@Table(name="course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double fee;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students;

    /* Getters and setters */
}
@Entity
@Table(name="student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "student_course",
            joinColumns = {@JoinColumn(name = "student_id")},
            inverseJoinColumns = {@JoinColumn(name = "course_id")}
    )
    private Set<Course> courses;

    /* Getters and setters */
}
```
- The Join table `student_course` that links both tables   
  > `JoinColumns` points to the owning side table (student) and `InverseJoinColumns` points to the inverse table of the owning side `course`.  

- Use `CascadeType.Merge` and `CascadeType.Persist` but not `Cascade.Remove` because if I delete a course, I don’t want to remove the students from that course.

- Using Set rather than List in my association.   
  > This is because Hibernate deletes all the rows in `student_course` link to that entity, and re-insert the ones we did not want to delete. 

## How Hibernate in ManyToMany functions

To delete just one course from a student that has 4 courses.
```sql
/** even if we just delete one row it delete all **/
delete form student_course where student_id = ?

/** reinsert the rows**/
insert into student_course(student_id, course_id) values(?, ?)
insert into student_course(student_id, course_id) values(?, ?)
insert into student_course(student_id, course_id) values(?, ?)
```

## [Tag and Posts ManyToMany Relationship](https://vladmihalcea.com/the-best-way-to-use-the-manytomany-annotation-with-jpa-and-hibernate/)

```java
@Entity(name = "Post")
@Table(name = "post")
public class Post {
 
    @Id
    @GeneratedValue
    private Long id;
 
    private String title;
 
    public Post() {}
 
    public Post(String title) {
        this.title = title;
    }
 
    @ManyToMany(cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    @JoinTable(name = "post_tag",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();
 
    //Getters and setters omitted for brevity
 
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getPosts().add(this);
    }
 
    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getPosts().remove(this);
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;

        // check content
        return id != null && id.equals(((Post) o).getId());
    }
 
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
 
@Entity(name = "Tag")
@Table(name = "tag")
public class Tag {
 
    @Id
    @GeneratedValue
    private Long id;
 
    @NaturalId
    private String name;
 
    @ManyToMany(mappedBy = "tags")
    private List<Post> posts = new ArrayList<>();
 
    public Tag() {}
 
    public Tag(String name) {
        this.name = name;
    }
 
    //....

    @Override
    public boolean equals(Object o) {
        // reference to same addr?
        if (this == o) return true;
        
        if (o == null || getClass() != o.getClass()) return false;
        
        // check the contain
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
```

- the tags association in the Post entity only defines the `PERSIST` and `MERGE` cascade types. 
- the `add/remove` utility methods are mandatory if you use bidirectional associations so that you can make sure that both sides of the association are in sync.


- **The Post entity uses the entity identifier for equality** since it lacks any unique business key. 
  > As explained in this article, you can use the entity identifier for equality as long as you make sure that it stays consistent across all entity state transitions.

- The Tag entity has a unique business key which is marked with the Hibernate-specific `@NaturalId` annotation. When that’s the case, the unique business key is the best candidate for equality checks.

- The mappedBy attribute of the posts association in the Tag entity marks that, in this bidirectional relationship, the Post entity owns the association. 
  > This is needed since only one side can own a relationship, and changes are only propagated to the database from this particular side.

For a database perspective, the Many-To-Many relationship mapping is not efficient at all, for example we do a `delete` query

```java
final Long postId = doInJPA(entityManager -> {
    Post post1 = new Post("JPA with Hibernate");
    Post post2 = new Post("Native Hibernate");
 
    Tag tag1 = new Tag("Java");
    Tag tag2 = new Tag("Hibernate");
 
    post1.addTag(tag1);
    post1.addTag(tag2);
 
    post2.addTag(tag1);
 
    entityManager.persist(post1);
    entityManager.persist(post2);
 
    return post1.id;
});

doInJPA(entityManager -> {
    Tag tag1 = new Tag("Java");
    Post post1 = entityManager.find(Post.class, postId);
    post1.removeTag(tag1);
});
```

```sql
SELECT p.id AS id1_0_0_,
       t.id AS id1_2_1_,
       p.title AS title2_0_0_,
       t.name AS name2_2_1_,
       pt.post_id AS post_id1_1_0__,
       pt.tag_id AS tag_id2_1_0__
FROM   post p
INNER JOIN
       post_tag pt
ON     p.id = pt.post_id
INNER JOIN
       tag t
ON     pt.tag_id = t.id
WHERE  p.id = 1
 
DELETE FROM post_tag
WHERE  post_id = 1
 
INSERT INTO post_tag
       ( post_id, tag_id )
VALUES ( 1, 3 )
```

So, instead of deleting just one post_tag entry, Hibernate removes all post_tag rows associated to the given `post_id` and reinserts the remaining ones back afterward. 

This is not efficient at all because it’s extra work for the database, especially for recreating indexes associated with the underlying Foreign Keys.

For this reason, it’s not a good idea to use the `java.util.List` for `@ManyToMany` JPA associations.

Instead we use `java.util.set`

```java
@ManyToMany(cascade = {
    CascadeType.PERSIST,
    CascadeType.MERGE
})
@JoinTable(name = "post_tag",
    joinColumns = @JoinColumn(name = "post_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private Set<Tag> tags = new HashSet<>();

// class Tag
@ManyToMany(mappedBy = "tags")
private Set<Post> posts = new HashSet<>();
```

So if we do `delete` query 
```sql 
SELECT p.id AS id1_0_0_,
       t.id AS id1_2_1_,
       p.title AS title2_0_0_,
       t.name AS name2_2_1_,
       pt.post_id AS post_id1_1_0__,
       pt.tag_id AS tag_id2_1_0__
FROM   post p
INNER JOIN
       post_tag pt
ON     p.id = pt.post_id
INNER JOIN
       tag t
ON     pt.tag_id = t.id
WHERE  p.id = 1
 

DELETE FROM post_tag
WHERE  post_id = 1 AND tag_id = 3
```