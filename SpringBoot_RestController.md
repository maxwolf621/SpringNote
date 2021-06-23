###### tags: `Spring Boot`
# A BackEnd Spring Project Steps
[TOC]

## Controller

```java=
@RestController
@RequestMapping("/api")
public class MemberController {
    @Autowired
    private MemberRepository memberRepository;


    /**
     * when the user in the uri : `../api/members`
     */   
    @GetMapping("/members")
    public Collection<Member> members() {
        // it will display all members on the webpage via Repository
        return memberRepository.findAll();
    }
    
    //...
}

```

- We can have different controls for different Repositories/ Servers in the Controller

- @RestController = @Controller + @ResponseBody, 
    > ResponseBody會將返回結果直接寫在Http Response Body中, 因為我們資料傳輸時通常只傳回Json, 所以大部分都會使用這Annotation頁面的導頁與指定會交由前端(React,Angular...etc...)來做

- **`@GetMapping`,` @PostMapping`,` @PutMapping`, `@DeleteMapping`與Http的GET, POST, PUT, DELETE等Methods相對應, 只要遵守RESTful規範直接使用即可**
