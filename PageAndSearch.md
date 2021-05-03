# Pagination And Sorting 

To use Sorting and Paging APIs 
we must let our repository interface to extend `PagingAndSortingRepository` or `JpaRepository`.


Then we can use the method such `PageRequest.of(pageNumber, pageSize, Sort.by("field").DesorAsc)` return a `Pageable` object that can passed as parameter for method from `JpaRepository` or `PagingAndSortingRepository`

For example
To expect first page with 10 items/ per page 
```java
int pageNumber = 1; // Current Page is at first page
int pageSize = 10 ; // ten imtes
Pageable pageable  = PageRequest.of(pageNumber, pageSize);

// A page that has 10 itmes to be displayed
Page<Product> page = repository.findAll(pageable);
```



Onece we have Page typed object, there are some methods to do further operations
```java
pbulic class page{
  //..
  public class<T> getContent()
  {
    //..
  }
  public class<T> getTotalElements()
  {
    //..
  }
  public class<T> getTotalPages()
  {
    //..
  }
}

```

.getContent() method to get the Content
```java
List<Product> listProducts = page.getContent();
```

Get total rows in the database 
```java
long totalItems = page.getTotalElements();
```
get total page size
```java
int totalPages = page.getTotalPages();
```


### Implement Pagination for our Service and Controller class

To modify our Server class
```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;
  
    public List<Product> listAll() {
        return repo.findAll();
    }  
}
```

```java
// modify listAll() to
public Page<Product> listAll(int pageNum) {
    int pageSize = 5;
    // show only file items per page
    Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
     
    return repo.findAll(pageable);
}
```
> Note that pagination APIs considers page number is 0-based. In the view, we use 1-based page number for the users, but in the code we need to convert to 0-based page number, hence you see pageNum – 1 as above


## Controller class for paging

Controller class to resolve every page 
```java
@RequestMapping("/page/{pageNum}")
public String viewPage(Model model,
    @PathVariable(name = "pageNum") int pageNum) {

    Page<Product> page = service.listAll(pageNum);

    List<Product> listProducts = page.getContent();

    // To show up these attributes in the page
    model.addAttribute("currentPage", pageNum);
    model.addAttribute("totalPages", page.getTotalPages());
    model.addAttribute("totalItems", page.getTotalElements());
    model.addAttribute("listProducts", listProducts);

    return "index";
}
```

Controller for home page`/`
```java
@RequestMapping("/")
public String viewHomePage(Model model) {
    // show up the first page
    return viewPage(model, 1);
}
```


## web page with the pagination



## Sort API

Sytnax for sort the specified attirbute
```java
Sort sort = Sort.by("fileName").ascending();
```


Server Class
```java
@Autowired
private ProductRepository repo; 
//..
public Page<Product> listAll(int pageNum, String sortField, String sortDir) {
    int pageSize = 5;
    Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
            sortDir.equals("asc") ? Sort.by(sortField).ascending()
                                              : Sort.by(sortField).descending()
    );
    return repo.findAll(pageable);
}
```


Controller
```java
@RequestMapping("/page/{pageNum}")
public String viewPage(Model model,
                       @PathVariable(name = "pageNum") int pageNum,
                       @Param("sortField") String sortField,
                       @Param("sortDir") String sortDir) 
{
        Page<Product> page = service.listAll(pageNum, sortField, sortDir);

        List<Product> listProducts = page.getContent();


        model.addAttribute("currentPage", pageNum);    
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listProducts", listProducts);

        return "index";
}
```
> @PathVariable
> : Variable in URL
>
> @Param


## web page

```html
<th>
    <a th:href="/@{'/page/' + ${currentPage} + '?sortField=id&sortDir=' + ${reverseSortDir}}">Product ID</a>
</th>
<th>
    <a th:href="/@{'/page/' + ${currentPage} + '?sortField=name&sortDir=' + ${reverseSortDir}}">Name</a>
</th>
<th>
    <a th:href="/@{'/page/' + ${currentPage} + '?sortField=brand&sortDir=' + ${reverseSortDir}}">Brand</a>
</th>
<th>
    <a th:href="/@{'/page/' + ${currentPage} + '?sortField=madein&sortDir=' + ${reverseSortDir}}">Made In</a>
</th>
<th>
    <a th:href="/@{'/page/' + ${currentPage} + '?sortField=price&sortDir=' + ${reverseSortDir}}">Price</a>
</th>
```

