###### tags: `Spring` `JAVA`
# JWT
[TOC]

[JWTlibarary](https://segmentfault.com/a/1190000024448199)  
[Spring Boot and JWT](https://segmentfault.com/a/1190000024448199)  
[Good reference with Code examle](https://www.codeproject.com/Articles/1253786/Java-JWT-Token-Tutorial-using-JJWT-Library)  
[Reference (china)](https://blog.csdn.net/qq_37636695/article/details/79265711)  




## Concept
- How to create a Java Key Store
- How to extract public key out of the KeyStore file
- How to create the JWT token
- How to encrypt the JWT token using the private key in the keystore
- How to decrypt the encrypted JWT token using the exported public key

## Purpose 

Say if there are the mirco-services...  
One of the micro-services would be an authentication service.  

When a Request comes into micro-service ecosystem, the request will first be authenticated.  
> User credential will be sent to the *authentication* and *authorization* service first, which can look up the user in database(via Userdetails Srvice), and verify the credentials, then send back an *encrypted* JWT token that contains the user information.  

**The service that is supposed to process the request will *decrypt* the JWT token and verify the user's authorization before the request can be handled.**

- The authorization check will see what *roles* the user has and whether the user roles are sufficient for the request to be handled.   


## Why don't use Session 
[GOOD Reference](https://betterprogramming.pub/json-web-tokens-vs-session-cookies-for-authentication-55a5ddafb435)

![](https://i.imgur.com/jHBcOs0.png)

Sessions
> When client sends the request, the Server has to make a authorization and store in the Memory if authentication is successful.  
>> The more Authorization of clients Server gets, the higher Server's Cost gets
>> ![](https://i.imgur.com/V59FBHJ.png)

#### Cookies
Cookies are basically data stored on your computer(local host), which can be accessed by websites to perform various functionalities from remembering your online shopping cart to authentication.

##### Problem of Session will face 
1. Scalability : Session是在外部裝置裡的,導致需要考慮儲存擴展性(*Scalability*)的問題
2. CORS : 資源共享問題(To access different domain)
    > For example AJAX fetches information from other domain server，it might occur the error.
3. CSRF Attack


#### To avoid the above problems we use Token-Based Authentication  
> Token-based authentication using JWT is the recommended method in modern web apps.  

**It scales better than that of a session because tokens are stored on the client-side** while the session makes use of the server memory, and this might be an issue when a large number of users use the system at once.  
**However, a drawback with JWT is that it is much bigger compared to the session ID stored in a cookie since JWT contains more user information**. 
> Care/Caution must be taken to not include sensitive information in the JWT to prevent XSS security attacks.

## JWT token

![](https://i.imgur.com/8UPOkuj.png)
- Key Points
    > authentication of Request (who sends the request)  
    > authorization of Request (the permission of the client)  
    > encryption of Token  
    > decryption of Token  
In a token-based application, the server creates a signed token and sends the token back to the client.  
- The JWT is stored on the client’s side (usually in local storage) and sent as a header for every subsequent request.
- ==The server would then decode the JWT, and, if the token is valid, processes the request and sends a response.==
- ==When the user logs out, the token is destroyed on the client’s side without having any interaction with the server.==


There are two main reason for using JTW token instead of Session

> For the Authorization
> : Once the user is logged in, each subsequent request will include the JWT, allowing the user to access routes, services, and resources that are permitted with that token.  
>> **Single Sign On is a feature that widely uses JWT nowadays, because of its small overhead and its ability to be easily used across different domains.**
>
> Information Exchange
> : JSON Web Tokens are a good way of securely transmitting information between parties. 
> Because JWTs can be signed—for example, **using public/private key pairs—you can be sure the senders are who they say they are.(via Class Keystore)** 
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
- algorithm of encrypted type
```json=
{
  "alg":"HS256",
  "type":"JWT"
 }
```
#### PayLoad (User's information)

Claims are statements about **an entity (typically, the user) and additional data.**  

1. Registered claims
    > ==These are a set of predefined claims which are not mandatory but recommended==, to provide a set of useful, interoperable claims. Some of them are: `iss` (issuer), `exp` (expiration time), `sub` (subject), `aud` (audience) ... etc..

Notice that the claim names are only three characters long as JWT is meant to be compact.

2. Public claims
    > These can be defined at will by those using JWTs.  
    >> But to avoid collisions they should be defined in the IANA JSON Web Token Registry or be defined as a URI that contains a collision resistant namespace.

3. Private claims
    > These are the **custom claims** created to share information between parties that agree on using them and are neither registered or public claims.

[Source Code](https://blog.csdn.net/weixin_41540822/article/details/88781964)


#### Jwts.builder().methods correspond to Registered Claims in jwt

| Java Methods |  Claims |
| -------------| ------- |
|setIssuer     | sets the iss (Issuer) Claim
|setSubject    | sets the sub (Subject) Claim
|setAudience  |sets the aud (Audience) Claim **(receiver)**
|setExpiration|sets the exp (Expiration Time) Claim
|setNotBefore |sets the nbf (Not Before) Claim **(Token cant not use until ..)**
|setIssuedAt  |sets the iat (Issued At Time) Claim 
|setId        |sets the jti (JWT ID) Claim **(unique ID)**


In java
```java=
String jws = Jwts.builder()
    .setIssuer("me") // client 
    .setSubject("Bob") // username
    .setAudience("you") // server
    .setExpiration(expiration) //a java.util.Date
    .setNotBefore(notBefore) 	//a java.util.Date 
    .setIssuedAt(new Date()) 	//for example, now
    .setId(UUID.randomUUID()) 	//just an example id
```

in json
```json=
{
    "sub":"Bob",
    "aud":"you",
    "iss":"me",
    "exp":1528360637,
    "nbf":1528360631,
    "iat":1528360628,
    "jti":"253e6s5e"
}
```

self-define (private Cliams) payloads via `claim()` or `setClaims()` methods
```java=
Claims claims = Jwts.claims();
claims.put("userId", "1234567");
claims.put("role", "admin")
claims.setExpiration(exp);
```

in json
```json=
{
    "userId" : "1234567"
    "role"   : "admin"
    "exp"    : 1528360637
}
```

if we use private claims via `setClaims()` method
it should look like this
```java=
long nowMillis = System.currentTimeMillis();// Time of Generated JWT
Date now = new Date(nowMillis);
Map<String,Object> claims = new HashMap<String,Object>();
// self define cliams (private Claims)
claims.put("uid", "DSSFAWDWADAS...");
claims.put("user_name", "admin");
claims.put("nick_name","DASDA121");

//** generate signature key (secret key) in local host 
//    (e.g from file ...), this key can not be stolen.
//** once the secret key is hacked by someone, 
//     it can be used to access the server side by hacker
SecretKey key = generalKey();

JwtBuilder builder = Jwts.builder() 
        .setClaims(claims) //Always put after .builder()
        .setId(id)        //JWT ID：是JWT的身份證(不重複的值,一次性token)
        .setIssuedAt(now)         
        .setSubject(subject) //sub(Subject)：代表这个JWT的主体，for example a userid，roleid etc...
                             //作為用戶的唯一標誌(身份證)
        .signWith(signatureAlgorithm, key); // A JWT's Key Algorithm , Key Type
```

`gerneralKey` Method 
```java=
    public SecretKey generalKey(){
        String stringKey = Constant.JWT_SECRET;//Local host stringKey is 7786df7fc3a34e26a61c034d5ec8245d
        byte[] encodedKey = Base64.decodeBase64(stringKey);//lcoal host decode [B@152f6e2
        
        System.out.println(encodedKey);    //[B@152f6e2
        System.out.println(Base64.encodeBase64URLSafeString(encodedKey));//7786df7fc3a34e26a61c034d5ec8245d
        
        // generate the Secrete Key
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

```json=
HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload) + "." + singatureKey
```

#### A encrypted Token looks like the following
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
                        // A key made of HS512
                        .signWith(SignatureAlgorithm.HS512, "PrivateKey") 
                        .compact();
        System.out.println(token);

        // Validate jwts
        String user = Jwts.parser()
                        // Compare the key
                        .setSigningKey("PrivateKey")
                        .parseClaimsJws(token)
                        .getBody()
                        // Get User
                        .getSubject();
        System.out.println(user);

 
        Object object = Jwts.parser()
                    .setSigningKey("PrivateKey")
                    .parseClaimsJws(token)
                    .getBody();
        System.out.println(object);  
    }
```
```java=
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

### Base64URL encode
`+`,`/` and `=` are special symbols in <u>base64URL</u>, if these symbols are contained in the URL, they will be replaced 
> For instance `api.example.com/?token=xxx` then `=` will be deleted, `+` will be replaced with `-` and `/` will be replaced with `_`

### JWT in client
When client receives returned JWT token that from Server, client will store it in the local host.

Client will carry JWT information from the local host to send request/communication to/with Server 

**The Common way is we put token in the header's Authorization in HTTP request**, as the following
```json=
{
    "Authorization": "Bearer <token>"
}
```
> By such way we can use filter to get token 

### Process to generate jwt Token
1. Check payload validation
2. Fetch Key。如果是keyBytes則通過keyBytes及指定的算法種類創造Key Instance。
3. Record Key's Type of Algorithm in the Json's header。
    > If it needs to compress，則Algorithm for Compressing也需紀錄在header中
4. 把json格式的header轉成bytes，then Base64 Encoded
5. 把Json格式的claims轉成bytes，如果過長需則壓縮，再Base64 Encoded
6. Combine header和claims
    > If Signature key is null,則直接在末端補上`.`
    > Else using `sign(String jwtWithoutSignature)` method and encode the returned value
7. return JWT(header+claims+key)


### Property 
- 不加密或加密的
    > JWT 不加密的情况下，不能將Information寫入JWT
- After generating Token，可以用private key再encrypted一次。
- 認證以及交換資訊
    > it reduces that Server to query the data

## Disadvantage of JTW

- Server does not store **status** of session
    > 因此無法在使用過程中abondon某個token，或更改 token 的權限
    > 只要JWT一簽發signs，在到期之前都具有效性，除非Server deploys **extra Logic**

- JWT 本身包含了認證訊息
    > 一旦洩漏，任何人都可以取得該token的所有權限
    >> To avoid this, JWT's expiration設置的時效性不宜過長,以及較重要的權限，用戶request時則需在進行一次Authorization

:::danger
1. JWT should use `HTTPS` protocol instead of `HTTP` for the safe
2. Client's Token can be hacked from a hacker
    > **if hacker want to access the server, hacker needs to send the request from the same computer(ip address)**
:::

## class JWT
[Reference](https://zhuanlan.zhihu.com/p/265839399)
[methods](https://blog.csdn.net/weixin_41540822/article/details/88781964)
[methods examples](https://blog.csdn.net/qq_37636695/article/details/79265711)

### Generate JWT Token via `.builder()`
```java=
public static String generateToken(Stringusername){

    // cliams Hashmap
    Map claims= new HashMap<>();
    claims.put(CLAIM_KEY_USERNAME,username);
    claims.put(CLAIM_KEY_CREATE_TIME,new Date(System.currentTimeMillis()));

    return Jwts.builder
            // setCliams(Map cliams)
            .setClaims(claims)
            .setExpiration(generateExpirationDate())
            .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
            //.compressWith(CompressionCodecs.DEFLATE)
            .compact(); // generate Token
}
```

Jwts.builder() returns DefaultJwtBuilder() 
The calss DefaultJwtBuilder()'s sourceCode
```java=
public class DefaultJwtBuilder implements JwtBuilder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    /* Jwts.builder() Contains these fields  */
    private Header header; 
    
    //Choose One of them
    private Claims claims;
    private String payload; 
    
    private SignatureAlgorithm algorithm; // Signigure Key Algorithm
    private Key key; // Signiture 
    private byte[] keyBytes; //Sinigure key's character 
    private CompressionCodec compressionCodec; // Algorithm for Compression
    
    // methods
    }
```
[Complement DefaultJwtBuilder's Source Code](https://github.com/jwtk/jjwt/blob/master/impl/src/main/java/io/jsonwebtoken/impl/DefaultJwtBuilder.java)  
[sample code explanation](https://www.jianshu.com/p/1ebfc1d78928)   

Important methods in Class `DefaultJwtBuilder` are
```java=
// if header is null return default header
.setHeaderParam()
.setHeaderParams()
.setAlgorithm()
.setPayload() 
.setClaims() // set A instance 
.claim() 
.compressWith() 
```
>`claim()` if attribute of claim is null, it will invoked a instance of Claims automatically by DefaultClaims(), else it will check the validation of the instance then update the claim  

> `compressWith()` if payload is too large then we can compress it via `CompressionCodecs.GZIP` or `CompressionCodecs.DEFLATE`

`signWith(parametrs)` would have the followings
```java
.signWith(Key key)  
.signWith(Key key, SignatureAlgorithm algo)
.signWith(SignatureAlgorithm alg, byte[] secretKeyBytes)
.signWith(SignatureAlgorithm alg, String base64EncodedSecretKey)  
```

The flowing methods are optional
```java=
.setIssuer()
.setSubject()
.setAudience()
.setExpiration()
.setNotBefore()
.setIssuedAt()
.setId()
```

==When all required attribute in builder() are set up, we use `.compact()` to generate jwt Token==

### Parse jwt Token
[Reference](https://www.jianshu.com/p/6bfeb86885a3)

> `.Parse()` and `.Builder` are the Relationship mapping (對應關係)
>> for example `setSigningKey()` in `.Pasrse` corresponds to `signWith()` in `.builder()`
`
```java=
private static Claims getClaimsFromToken(String token) {
    Claims claims;
    try {
        claims = Jwts.parser()
                .setSigningKey(secretKey)    // the token's key
                .parseClaimsJws(token)      // parse the jwt Token
                .getBody();
    } catch (Exception e) {
        claims = null;
    }
    return claims;
}
```
> `setSigningKey(Key key)`, `setSigningKey(String base64encode)`, `setSingingKey(bytes[] key)`  
> `paseClaims(String cliams_Jwtoken)`


[SourceCode of DefaultJwtParse](https://github.com/jwtk/jjwt/blob/master/impl/src/main/java/io/jsonwebtoken/impl/DefaultJwtParser.java)  
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
.requireIssuedAt()
.requireIssuer()
.requireAudience()
.requireSubject()
.requireId()
.requireExpiration()
.requireNotBefore()
.require() // self-define the validation of Claims
```
- if validation fails then it will throw `IncorrectClaimException`

## Authentication (filter) Flow Diagram
![](https://i.imgur.com/XkNQh28.png)
- Note that **The request is intercepted by the `JWTAuthenticationFilter` for checking validation of the token**
    > If the token is valid, the request is forwarded to the corresponding Controller.

## To invoke JWT filter we need these concepts
[Note for Key Store](/ESLslf26QYapX02_ueZaRw)  
[Note for Authentication](/CrTB3w_mRm-2SFVF648fpw)  
[methods example of KeyStore](https://www.codeproject.com/Articles/1253786/Java-JWT-Token-Tutorial-using-JJWT-Library)  
[longin filter](https://hackmd.io/@maxWolf/ryLEJKQPu)  

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
