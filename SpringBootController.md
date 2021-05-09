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


    // when we on `../api/members`
    //    it will display all members on the webpage
    @GetMapping("/members")
    public Collection<Member> members() {
        return memberRepository.findAll();
    }
    
    //...
}

```

We can have different controls for different Repositories 
for example a controller for register REpository ... etc ...

- @RestController = @Controller + @ResponseBody, 
    > ResponseBody會將返回結果直接寫在Http Response Body中
因為我們資料傳輸時通常只傳回Json, 所以大部分都會使用這Annotation頁面的導頁與指定會交由前端(React)來做

**`@GetMapping`,` @PostMapping`,` @PutMapping`, `@DeleteMapping`與Http的GET, POST, PUT, DELETE等方法相對應, 只要遵守RESTful規範直接使用即可**
