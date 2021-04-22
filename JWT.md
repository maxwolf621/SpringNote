###### tags: `Spring` `JAVA`
#### [jtw libarary](https://segmentfault.com/a/1190000024448199)

#### [Spring Boot and JWT](https://segmentfault.com/a/1190000024448199)
#### [Good reference with Code examle](https://www.codeproject.com/Articles/1253786/Java-JWT-Token-Tutorial-using-JJWT-Library)
#### [Reference (china)](https://blog.csdn.net/qq_37636695/article/details/79265711)
# JWT
[TOC]

## To learn
- How to create a Java Key Store
- How to extract public key out of the KeyStore file
- How to create the JWT token
- How to encrypt the JWT token using the private key in the keystore
- How to decrypt the encrypted JWT token using the exported public key

## Purpose 
![](https://i.imgur.com/RFry4xl.png)

- Key Points
	> authentication of Request
	> authoroization of Request
	> encryption of Token
	> decryption of Token
	> request's roles

Say if there are the mirco-services  
One of the micro-services would be an authentication service.  
When a request comes into micro-service ecosystem, the request will first be *authenticated*.  
User credential will be sent to the *authentication* and *authorization* service first, which can look up the user in database, and verify the credentials, then send back an *encrypted* JWT token that contains the user information.  
The service that is supposed to process the request will *decrypt* the JWT token and verify the user's authorization before the request can be handled.  
The authorization check will see what *roles* the user has and whether the user roles are sufficient for the request to be handled.

## Why dont use Session 
[Real GOOD Reference](https://betterprogramming.pub/json-web-tokens-vs-session-cookies-for-authentication-55a5ddafb435)
![](https://i.imgur.com/jHBcOs0.png)

Sessions
    > When client sends the request，Server has to make a authorization and store in the ServerMemory，
    >> The more Authorization of clients Server gets, the higher Server's Cost gets
    >> ![](https://i.imgur.com/V59FBHJ.png)

#### Cookies
Cookies are basically data stored on your local computer, which can be accessed by websites to perform various functionalities from remembering your online shopping cart to authentication.

- Scalability : Session是在外部裝置裡的,導致需要考慮儲存擴展性(Scalability)的問題
- CORS : 資源共享問題(access different domain)
    > example AJAX fetches information from other domain server，it might occur the error
- CSRF Attack


To avoid the above problems we use Token-Based Authentication  
Token-based authentication using JWT is the recommended method in modern web apps.  
It **scales** better than that of a session because tokens are stored on the client-side while the session makes use of the server memory, and this might be an issue when a large number of users use the system at once.  
**However, a drawback with JWT is that it is much bigger compared to the session ID stored in a cookie since JWT contains more user information**. 
> Care must be taken to not include sensitive information in the JWT to prevent XSS security attacks.

## JWT token

![](https://i.imgur.com/8UPOkuj.png)
 
In a token-based application, the server creates a signed token and sends the token back to the client.  
The JWT is stored on the client’s side (usually in local storage) and sent as a header for every subsequent request.

The server would then decode the JWT, and, if the token is valid, processes the request and sends a response. When the user logs out, the token is destroyed on the client’s side without having any interaction with the server.


so lets say it more clearer to use JTW token instead of Session

> For the Authorization
> : Once the user is logged in, each subsequent request will include the JWT, allowing the user to access routes, services, and resources that are permitted with that token.  
>> **Single Sign On is a feature that widely uses JWT nowadays, because of its small overhead and its ability to be easily used across different domains.**

> Information Exchange
> : JSON Web Tokens are a good way of securely transmitting information between parties. 
> Because JWTs can be signed—for example, **using public/private key pairs—you can be sure the senders are who they say they are.** 
> **Additionally, as the signature is calculated using the header and the payload, you can also verify that the content hasn't been tampered with.**




## Content of JWT token

Construction of JWT can be presented 3 sections
- Header	 
- Payload	
- Signature	
    > Each section is base64 URL-encoded. This ensures that it can be used safely in a URL

#### header

header will give us two information
- type of token
- algorithm oof encrypted type
```json=
{
  "alg":"HS256",
  "type":"JWT"
 }
```
#### PayLoad

Claims are statements about an entity (typically, the user) and additional data.  

> Registered claims
> : ==These are a set of predefined claims which are not mandatory but recommended==, to provide a set of useful, interoperable claims. Some of them are: iss (issuer), exp (expiration time), sub (subject), aud (audience) ... etc..

Notice that the claim names are only three characters long as JWT is meant to be compact.

> Public claims
> : These can be defined at will by those using JWTs.  
But to avoid collisions they should be defined in the IANA JSON Web Token Registry or be defined as a URI that contains a collision resistant namespace.

> Private claims
> : These are the custom claims created to share information between parties that agree on using them and are neither registered or public claims.

[Source Code](https://blog.csdn.net/weixin_41540822/article/details/88781964)


#### Java methods and Json
setIssuer: sets the iss (Issuer) Claim
setSubject: sets the sub (Subject) Claim
setAudience: sets the aud (Audience) Claim **(receiver)**
setExpiration: sets the exp (Expiration Time) Claim
setNotBefore: sets the nbf (Not Before) Claim **(Token cant not use until ..)**
setIssuedAt: sets the iat (Issued At 某時間) Claim 
setId: sets the jti (JWT ID) Claim **(unique ID)**


in java
```java=
String jws = Jwts.builder()
    .setIssuer("me")
    .setSubject("Bob")
    .setAudience("you")
    .setExpiration(expiration) 	//a java.util.Date
    .setNotBefore(notBefore) 	//a java.util.Date 
    .setIssuedAt(new Date()) 	// for example, now
    .setId(UUID.randomUUID()) 	//just an example id
```

in json
```json=
{
    "sub":"Bob",
    "aud":"you",
    "iss":"me",
    "iat":1528360628,
    "nbf":1528360631,
    "jti":"253e6s5e",
    "exp":1528360637}
}
```

[Reference]()
use self-define `claim()` or `setClaims()` methods
```java=
Claims claims = Jwts.claims();
claims.put("userId", "1234567");
claims. claims.setExpiration(exp);
```

in json
```json=
{
    "userId" : "1234567"
    "exp"    : 1528360637
}
```

if we use private claims `setClaims()` method
it should look like this
```java=
 long nowMillis = System.currentTimeMillis();// Time of Generated JWT
        Date now = new Date(nowMillis);
        Map<String,Object> claims = new HashMap<String,Object>();
        // self define cliams (private Claims)
        claims.put("uid", "DSSFAWDWADAS...");
        claims.put("user_name", "admin");
        claims.put("nick_name","DASDA121");
        SecretKey key = generalKey();//生成签名的时候使用的秘钥secret,这个方法本地封装了的，一般可以从本地配置文件中读取，切记这个秘钥不能外露哦。它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
        
        JwtBuilder builder = Jwts.builder() 
                .setClaims(claims)          //Always put after .builder()
                .setId(id)                  //JWT ID：是JWT的身份證，这个可以设置为一个不重复的值，主要用来作为一次性token,迴避重放攻擊。
                .setIssuedAt(now)         
                .setSubject(subject)        //sub(Subject)：代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid etc... ，作為用戶的唯一標誌(身份證)
                .signWith(signatureAlgorithm, key);// Key Algorithm , key Type
```

```java=
    public SecretKey generalKey(){
        String stringKey = Constant.JWT_SECRET;//Local host stringKey is 7786df7fc3a34e26a61c034d5ec8245d
        byte[] encodedKey = Base64.decodeBase64(stringKey);//lcoal host decode [B@152f6e2
        
        System.out.println(encodedKey);//[B@152f6e2
        System.out.println(Base64.encodeBase64URLSafeString(encodedKey));//7786df7fc3a34e26a61c034d5ec8245d
        
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        // Encrypted encodeKey from encodeKey[0] to encodeKey[encodeKey.length] via AES
        return key;
    }

```



:::danger
Do not store any important information in the `payload` and `header` unless they are encrypted
:::


#### Signature

A Signature contains `based64url` Encrypted Header/Play and password

```
HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

### A encrypted Token
![](https://i.imgur.com/rRljr45.png)


### Code Example

[Source Code](https://maizitoday.github.io/post/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E5%88%97-json-web-token/)

```java=
@Data
@ToString
public class Employee {
    private String id;
    private String username;
    private String password;

    public Employee() {
        this.setId("testId");
        this.setUsername("testUsername");
        this.setPassword("testPassword");
    }
}

public static void main(String[] args) {
        // generate Token
        String token = Jwts.builder()
                .setSubject(new Employee().toString())
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS512, "PrivateSecret") 
                .compact();
        System.out.println(token);

        System.out.println("开始测试jwt Subject 解密");
        String user = Jwts.parser()
                    .setSigningKey("PrivateSecret")
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        System.out.println(user);

  
        System.out.println("显示所有的信息");
        Object object = Jwts.parser()
                    .setSigningKey("PrivateSecret")
                    .parseClaimsJws(token)
                    .getBody();
        System.out.println(object);  
    }
@Component
public class JwtToken implements Serializable {

    private static final long EXPIRATION_TIME = 1 * 60 * 1000;
    /**
     * JWT SECRET KEY
     */
    private static final String SECRET = "learn to dance in the rain";

    /**
     * 簽發JWT
     */
    public String generateToken(HashMap<String, String> userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put( "userName", userDetails.get("userName") );

        return Jwts.builder()
                .setClaims( claims )
                .setExpiration( new Date( Instant.now().toEpochMilli() + EXPIRATION_TIME  ) )
                .signWith( SignatureAlgorithm.HS512, SECRET )
                .compact();
    }

    /**
     * 驗證JWT
     */
    public void validateToken(String token) throws AuthException {
        try {
            Jwts.parser()
                    .setSigningKey( SECRET )
                    .parseClaimsJws( token );
        } catch (SignatureException e) {
            throw new AuthException("Invalid JWT signature.");
        }
        catch (MalformedJwtException e) {
            throw new AuthException("Invalid JWT token.");
        }
        catch (ExpiredJwtException e) {
            throw new AuthException("Expired JWT token");
        }
        catch (UnsupportedJwtException e) {
            throw new AuthException("Unsupported JWT token");
        }
        catch (IllegalArgumentException e) {
            throw new AuthException("JWT token compact of handler are invalid");
        }
    }
}    
```

:::info
### Base64URL encode
`+`,`/` and `=` are special symbols in <u>base64URL</u>, if the above symbols are contains in the URL say `api.example.com/?token=xxx` then `=` will be deleted, `+` will be replaced with `-` and `/` will be replaced with `_`

### usage of JWT
When client receive returned JWT token that from Server, client will store it in the local host.

Client will carry JWT information from the local host to send request/communication to/with Server 

The Common way is we put token in the header's Authorization in HTTP request, as the following
```json
Authorization: Bearer <token>
```

### process to generate jwt Token

1. Check payload validation
2. 取得Key。如果是keyBytes則通過keyBytes及指定的算法種類創造Key Instance。
3. 將所指定Algorithm紀錄在header。
    > If compresses，則Algorithm for Compressing也需紀錄在header中
4. 把json格式的header轉成bytes，再Base64 Encoded
5. 把Json格式的claims轉成bytes，如果過長需壓縮，再Base64 Encoded
6. Combine header和claims
    > if Signature key is null,則直接在末端補上" . ")；
    > else using `sign(String jwtWithoutSignature)` method and encode the returned value
7. return JWT(header+claims+key)


### Property 
- 不加密或加密的
    > JWT 不加密的情况下，不能將Information寫入JWT
- 生成原始 Token 以后，可以用private key再encrypted一次。
- 認證以及交換資訊
    > it reduces that Server to query the data
:::

## disadvantage of JTW

- Server does not store status of session
    > 因此無法在使用過程中abondon某個token，或更改 token 的權限
    > 只要JWT一簽發signs，在到期之前都具有效性，除非Server deploys **extra Logic**

- JWT 本身包含了認證訊息
    > 一旦洩漏，任何人都可以取得該token的所有權限
    >> To avoid this, JWT's expiration設置的時效性不宜過長,以及較重要的權限，用戶request時則需在進行一次Authorization

:::danger
1. JWT should use HTTPS protocol instead of HTTP for the safe
2. Token can be hacked but if hacker want to access the server, 
:::

## class Jwt
[Reference](https://zhuanlan.zhihu.com/p/265839399)

[methods](https://blog.csdn.net/weixin_41540822/article/details/88781964)
[methods examples](https://blog.csdn.net/qq_37636695/article/details/79265711)
### Generate jwt Token via `.builder()`
```java=
public static String generateToken(Stringusername){

    Map claims= new HashMap<>();
    claims.put(CLAIM_KEY_USERNAME,username);
    claims.put(CLAIM_KEY_CREATE_TIME,new Date(System.currentTimeMillis()));

    return Jwts.builder()
            .setClaims(claims)
            .setExpiration(generateExpirationDate())
            .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
            //.compressWith(CompressionCodecs.DEFLATE)
            .compact();

}
```

Jwts.builder() returns DefaultJwtBuilder()
```java=
public class DefaultJwtBuilder implements JwtBuilder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    // Jwts Contains
    private Header header; 
    
    //Choose One of Claim and Playload 
    private Claims claims;
    private String payload; 
    
    private SignatureAlgorithm algorithm; // Signigure Key Algorithm
    private Key key; // Signiture 
    private byte[] keyBytes; //Sinigure key 字節
    private CompressionCodec compressionCodec; // Algorithm for Compression
    
    // methods
    }
```
[Details of class DefaultJwtBuilder](https://github.com/jwtk/jjwt/blob/master/impl/src/main/java/io/jsonwebtoken/impl/DefaultJwtBuilder.java)
[sample code explanation](https://www.jianshu.com/p/1ebfc1d78928)

Important methods in Class DefaultJwtBuilder are
```java=
// if header is null return default header
setHeaderParam()
setHeaderParams()
setAlgorithm
setPayload() 
setClaims() // set A instance 
claim() 
compressWith() 
```
>`claim()` if attribute of claim is null, it will invoked a instance of Claims automatically by DefaultClaims(), else it will check the validation of the instance then update the claim

> compressWith() if payload is too large then we can compress it via CompressionCodecs.GZIP or CompressionCodecs.DEFLATE

> signWith() 两个参数分别是签名算法和自定义的签名Key（盐）。签名key可以byte[] 、String及Key的形式传入。
>> 前两种形式均存入builder的keyBytes属性，后一种形式存入builder的key属性。如果是第二种（及String类型）的key，则将其进行base64解码获得byte[] 。


The flowing methods are optional
```java=
setIssuer()
setSubject()
setAudience()
setExpiration()
setNotBefore()
setIssuedAt()
setId()
```

When all required attribute in builder() set up, we use `.compact()` to generate jwt Token

### Parse jwt Token
[Reference](https://www.jianshu.com/p/6bfeb86885a3)

> `.Parse()` and `.Builder` are the Relationship mapping
>> for example `setSigningKey()` in `.Pasrse` corresponds to `signWith()` in `.builder()`
`
```java=
private static Claims getClaimsFromToken(String token) {
    Claims claims;
    try {
        claims = Jwts.parser()
                .setSigningKey(secretKey)    // the token's key
                .parseClaimsJws(token) // the Token needs to be parse
                .getBody();
    } catch (Exception e) {
        claims = null;
    }
    return claims;
}
```

`.Parse()` returns `DefaultJwtParse()`
```java=
//don't need millis since JWT date fields are only second granularity:
private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
private static final int MILLISECONDS_PER_SECOND = 1000;

private ObjectMapper objectMapper = new ObjectMapper();

private byte[] keyBytes;
private Key key; 
private SigningKeyResolver signingKeyResolver;
private CompressionCodecResolver compressionCodecResolver = new DefaultCompressionCodecResolver(); 
Claims expectedClaims = new DefaultClaims();
private Clock clock = DefaultClock.INSTANCE; 
private long allowedClockSkewMillis = 0;  // check if token is expire
```

`.Parse()` basically compares the information where returned by`.builder`
They are header, signature, Validity period and Claims

To require validity of Claims we have a set Methods to deal with
```java=
requireIssuedAt()
requireIssuer()
requireAudience()
requireSubject()
requireId()
requireExpiration()
requireNotBefore()
require() // self-define the validation of Claims
```
> if validation fails then throw `IncorrectClaimException`
## Authentication (filter) Flow Diagram
![](https://i.imgur.com/XkNQh28.png)
- Note that **The request is intercepted by the `JWTAuthenticationFilter` for checking validation of the token**
    > If the token is valid, the request is forwarded to the corresponding Controller.

## To invoke JWT filter we need these concepts
[Note for Key Store](/ESLslf26QYapX02_ueZaRw)
[Note for Authentication](/CrTB3w_mRm-2SFVF648fpw)
[methods example of KeyStore](https://www.codeproject.com/Articles/1253786/Java-JWT-Token-Tutorial-using-JJWT-Library)

### Check token validation 
```java 
public boolean validateToken(String jwt) {
    parser().setSigningKey(getPublickey()).parseClaimsJws(jwt);
return true;
}
```
## `JwtAuthenticationFilter`.java

**It extends OncePerRequesFilter to implement filter**

![](https://i.imgur.com/br6ByIm.png)

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


/*---FORM---*/
  .form-control {
    background: #333;
    color: #fff;
  }

  .form-control::placeholder,
  .form-control::-webkit-input-placeholder,
  .form-control:-moz-placeholder,
  .form-control::-moz-placeholder,
  .form-control:-ms-input-placeholder {
    color: #eee;
    
  }



/* navbar for bearbeiten, beides, Anzeigen 當游標指向這些物件時的顏色 */
.navbar .btn-group label.btn-default:focus,
.navbar .btn-group label.btn-default:hover {
background-color: #273746;
color: #eee;
border-color: yellow;
}

/* navbar for bearbeiten, beides, Anzeigen while activing */
.navbar .btn-group label.active {
background-color: #555;
color: #eee;
border-color: ;
}

.navbar .btn-group label.active:focus,
.navbar .btn-group label.active:hover {
background-color: #555;
color: #eee;
border-color: #555;
}

.navbar-default .btn-link:focus,
.navbar-default .btn-link:hover {
    color: #eee;
}

.navbar-default .navbar-nav>.open>a,
.navbar-default .navbar-nav>.open>a:focus,
.navbar-default .navbar-nav>.open>a:hover {
background-color: #555;
}

.dropdown-header {
color: #aaa;
}

.dropdown-menu {
background-color: #222;
border: 1px solid #555;
border-top: none;
}
.dropdown-menu>li>a {
color: #eee;
}

.dropdown-menu>li>a:focus,
.dropdown-menu>li>a:hover {
background-color: #555555;
color: #eee;
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

.markdown-body h1 .octicon-link,
.markdown-body h2 .octicon-link,
.markdown-body h3 .octicon-link,
.markdown-body h4 .octicon-link,
.markdown-body h5 .octicon-link,
.markdown-body h6 .octicon-link {
    color: yellow;
}

.markdown-body img {
    background-color: transparent;
}

.ui-toc-dropdown .nav>.active:focus>a, .ui-toc-dropdown .nav>.active:hover>a, .ui-toc-dropdown .nav>.active>a {
    color: gold;
    border-left: 2px solid white;
}

.expand-toggle:hover, 
.expand-toggle:focus, 
.back-to-top:hover, 
.back-to-top:focus, 
.go-to-bottom:hover, 
.go-to-bottom:focus {
    color: gold;
}

/* [](htpp://) */
a,.open-files-container li.selected a {
    color: #D2B4DE;
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
