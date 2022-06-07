###### tags: `Spring` `JAVA`
# JWT

[JWTlibrary](https://segmentfault.com/a/1190000024448199)  
[Spring Boot and JWT](https://segmentfault.com/a/1190000024448199)  
[[codeproject] JWT](https://www.codeproject.com/Articles/1253786/Java-JWT-Token-Tutorial-using-JJWT-Library)  
[Reference (china)](https://blog.csdn.net/qq_37636695/article/details/79265711)  
[methods example of KeyStore](https://www.codeproject.com/Articles/1253786/Java-JWT-Token-Tutorial-using-JJWT-Library)  

## Spring Authentication (filter) Flow Diagram with JWT
![](https://i.imgur.com/XkNQh28.png)
**The request is intercepted by the `JWTAuthenticationFilter` for checking validation of the token**     
If the token is valid, the request is forwarded to the corresponding Controller.   

## Purpose 

Say if there are the micro-services....One of the micro-services would be an authentication service.  

When a Request comes into micro-service ecosystem, the request will first be authenticated.   
User credential will be sent to the *authentication* and *authorization* service first, which can look up the user in database, and verify the credentials, then send back an *encrypted* JWT token that contains the user information.  

**The service that is supposed to process the request will *decrypt* the JWT token and verify the user's authorization before the request can be handled.**

The authorization check will see what *roles* the user has and whether the user roles are sufficient for the request to be handled.   

## Status of Session instead jwt
[GOOD Reference](https://betterprogramming.pub/json-web-tokens-vs-session-cookies-for-authentication-55a5ddafb435)

![image](https://user-images.githubusercontent.com/68631186/172255861-398d28d7-291a-4330-b7cc-58bf88ee3102.png)
- When client sent the request, the Server must make a authorization and store client's information in the Memory if authentication is successful.  
- Cookies are basically data stored on your computer(local host), which can be accessed by websites to perform various functionalities from remembering your online shopping cart to authentication.

![image](https://user-images.githubusercontent.com/68631186/172258777-0d0ba531-90e8-417a-9968-24324504641e.png)
- **The more Authorization of clients the Server gets, the higher Cost the Server gets**   


### Problem of Session 
1. Scalability : Sessions are stored in the external storage ,導致需要考慮儲存擴展性(*Scalability*)的問題
2. CORS : 資源共享問題(To access different domain)
    > For example AJAX fetches information from other domain server，it might occur the error.
3. CSRF Attack

## Jwt Token

To avoid the above problems we use Token-Based Authentication  

Token-based authentication using JWT is the recommended method in modern web apps.  

**It scales better than that of a session because tokens are stored on the client-side** while the session makes use of the server memory, and this might be an issue when a large number of users use the system at once.

**However, a drawback with JWT is that it is much bigger compared to the session ID stored in a cookie since JWT contains more user information.**  
Caution must be taken to not include sensitive information in the JWT to prevent XSS security attacks.

![](https://i.imgur.com/8UPOkuj.png)
- Key Points
    > authentication of Request (who sends the request)  
    > authorization of Request (the permission of the client)  
    > encryption of Token  
    > decryption of Token
 
In a token-based application, the server creates a signed token and sends the token back to the client.   

The JWT is stored on the client’s side (usually in local storage) and sent as a header for every subsequent request.

The server would then decode the JWT, and, if the token is valid, processes the request and sends a response.

When the user logs out, the token is destroyed on the client’s side without having any interaction with the server.

- JWT should use `HTTPS` protocol instead of `HTTP` for the safe
- Client's Token can be hacked from a hacker  
**If the hacker wants to access the server, hacker needs to send the request from the same computer(ip address)**

### JWT instead of Status Session

There are two main reason for using JTW token instead of Session
1. the Authorization  
Once the user is logged in, each subsequent request will include the JWT, allowing the user to access routes, services, and resources that are permitted with that token.  
**Single Sign On is a feature that widely uses JWT nowadays, because of its small overhead and its ability to be easily used across different domains.**

2. Information Exchange  
JSON Web Tokens are a good way of securely transmitting information between parties.  
Because JWTs can be signed.  
For example, **using public/private key pairs—you can be sure the senders are who they say they are.**  
**Additionally, as the signature is calculated using the header and the payload, you can also verify that the content hasn't been tampered with.**

### Content of JWT token

Construction of JWT can be presented 3 sections
- Header	 
- Payload	
- Signature	

Each section is base64 URL-encoded.   
This ensures that it can be used safely in a URL

#### Header
Two information
1. Type of token
2. Algorithm of encrypted type
```json
{
  "alg":"HS256",
  "type":"JWT"
 }
```

#### PayLoad (User's information)
Claims are statements about **an entity (typically, the user) and additional data.**  

1. Registered claims  
These are a set of predefined claims which are not mandatory but recommended, to provide a set of useful, interoperable claims. Some of them are: `iss` (issuer), `exp` (expiration time), `sub` (subject), `aud` (audience) ... etc..

Notice that the claim names are only three characters long as JWT is meant to be compact

2. Public claims  
These can be defined at will by those using JWTs. But to avoid collisions they should be defined in the IANA JSON Web Token Registry or be defined as a URI that contains a collision resistant namespace.

3. Private claims  
These are the **custom claims** created to share information between parties that agree on using them and are neither registered or public claims.


#### Signature

A Signature contains `based64url` Encrypted Header/Play and password

`HEADER.PAYLOAD.signatureKey`
```json
HMACSHA256(base64UrlEncode+ "." + base64UrlEncode(payload) + "." + signatureKey
```

A encrypted Token looks like the following
![](https://i.imgur.com/rRljr45.png)


#### Spring Server如何處理含JWT的Client Request

1. Check payload validation
2. Fetch Key 如果是`keyBytes`則通過`keyBytes`及指定的算法種類創造Key Instance
3. Key's Type of Algorithm in the Json's header  
If it needs to compress，則Algorithm for Compressing也需紀錄在header中
4. 將json格式的header轉成bytes，做Base64 Encoded
5. 將Json格式的claims轉成bytes，如果過長需則壓縮後再做Base64 Encoded
6. Combine header和claims  
If Signature key為`null`,則直接在末端補上`.` Else using `sign(String jwtWithoutSignature)` method and encode the returned value
7. return JWT(header+claims+key)

#### Advantage of JWT
- JWT 不加密的情况下，不能將Information寫入JWT
- After generating Token，可以用private key再encrypted一次
- 認證以及交換資訊特性減少Server查詢Data

#### Disadvantage of JTW

- Server does not store **status of session**  
因此無法在使用過程中Abandon某個token，或更改 token 的權限  
只要JWT一簽發，在到期之前都具有效性，除非Server deploys **extra Logic**

- JWT 本身包含了認證訊息   
一旦洩漏，任何人都可以取得該token的所有權限. 故JWT的expiration設置的時效性不宜過長,以及較重要的權限，用戶request時則需在進行一次authorization

### Base64URL encode

`+`,`/` and `=` are special symbols in <u>base64URL</u>, if these symbols are contained in the URL, they will be replaced 

For instance `api.example.com/?token=xxx` then `=` will be deleted, `+` will be replaced with `-` and `/` will be replaced with `_`

### JWT in client request

When client receives returned JWT token that from Server, client will store it in the local host.   
After that client carries JWT while sending request Server   
**The Common way is we put token in the header's Authorization in HTTP request**, as the following
```json
{
    "Authorization": "Bearer <token>"
}
```
- By such way Spring Server side uses `filter` implementation to get token 



## Java JWT
- [Reference](https://zhuanlan.zhihu.com/p/265839399)
- [Methods Examples](https://blog.csdn.net/weixin_41540822/article/details/88781964)
- [Methods Examples](https://blog.csdn.net/qq_37636695/article/details/79265711)

### Generate JWT Token 

via `jwt.builder()` create signature, payload(claims)
```java
public static String generateToken(String username){

    // Claims 
    Map claims= new HashMap<>();
    claims.put(CLAIM_KEY_USERNAME,username);
    claims.put(CLAIM_KEY_CREATE_TIME,new Date(System.currentTimeMillis()));

    // `Jwts.builder()` returns `DefaultJwtBuilder()` 
    return Jwts.builder
            // payloads
            .setClaims(claims)
            .setExpiration(generateExpirationDate())
            // header
            .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
            //.compressWith(CompressionCodecs.DEFLATE)
            .compact(); // generate Token
}
```
- When all required attribute in `builder()` are set up, we use `.compact()` to generate jwt Token
- `signWith(parameters)` would have the followings parameters
  ```java
  .signWith(Key key)  
  .signWith(Key key, SignatureAlgorithm algo)
  .signWith(SignatureAlgorithm alg, byte[] secretKeyBytes)
  .signWith(SignatureAlgorithm alg, String base64EncodedSecretKey)  
  ```

#### The class DefaultJwtBuilder()'s sourceCode
```java
public class DefaultJwtBuilder implements JwtBuilder {

    private static final ObjectMapper OBJECT_MAPPER = 
                    new ObjectMapper();
    
    /* Jwts.builder() Contains these fields  */
    private Header header; 
    
    // Choose One of them
    private Claims claims;
    private String payload; 
    
    // Signature Key Algorithm
    private SignatureAlgorithm algorithm; 
    // Signature 
    private Key key; 

    //Signature key's character
    private byte[] keyBytes;  

    // Algorithm for Compression
    private CompressionCodec compressionCodec; 
    
    // methods ...
}
```
- [DefaultJwtBuilder's Source Code](https://github.com/jwtk/jjwt/blob/master/impl/src/main/java/io/jsonwebtoken/impl/DefaultJwtBuilder.java)  
- [DefaultJwtBuilder Explanation](https://www.jianshu.com/p/1ebfc1d78928)   


#### Some Important methods in Class `DefaultJwtBuilder`
```java
.setHeaderParam()
.setHeaderParams()
.setAlgorithm()
.setPayload() 
.setClaims() 
.claim() 
.compressWith() 

// The flowing DefaultJwtBuilder methods are optional
.setIssuer()
.setSubject()
.setAudience()
.setExpiration()
.setNotBefore()
.setIssuedAt()
.setId()
```
- `claim()` if attribute of claim is null, it will invoked a instance of Claims automatically by DefaultClaims(), else it will check the validation of the instance then update the claim  
- `compressWith()` if payload is too large then we can compress it via `CompressionCodecs.GZIP` or `CompressionCodecs.DEFLATE`

### Parse JWT
[Reference](https://www.jianshu.com/p/6bfeb86885a3)

`Jwts.parser()` and `Jwts.builder()` are the Relationship mapping 
- For example `setSigningKey()` in `Jwts.parser().` corresponds to `signWith()` in `Jtws.builder()`

```java
private static Claims getClaimsFromToken(String token) {
    
    Claims claims;
    
    try {
        /** 
          * `.Parse()` returns `DefaultJwtParse()`
          */
        claims = Jwts.parser()
                // the token's key
                /**
                  * setSigningKey(Key key)
                  * setSigningKey(String base64encode)
                  * setSingingKey(bytes[] key)
                  * paseClaims(String claims_JWT)
                  */
                .setSigningKey(secretKey)  
                // parse the jwt Token 
                .parseClaimsJws(token)     
                .getBody();
    } catch (Exception e) {
        claims = null;
    }
    return claims;
}
```
- If parse JWT validation failed then it throws exception `IncorrectClaimException`

#### `DefaultJwtParse`

- [SourceCode of `DefaultJwtParse`](https://github.com/jwtk/jjwt/blob/master/impl/src/main/java/io/jsonwebtoken/impl/DefaultJwtParser.java)  

```java
// don't need millis since JWT date fields are only second granularity:
private static final String ISO_8601_FORMAT = 
                    "yyyy-MM-dd'T'HH:mm:ss'Z'";

private static final int MILLISECONDS_PER_SECOND = 1000;

private ObjectMapper objectMapper = new ObjectMapper();

private byte[] keyBytes;

private Key key; 

private SigningKeyResolver signingKeyResolver;

private CompressionCodecResolver compressionCodecResolver =
     new DefaultCompressionCodecResolver(); 

Claims expectedClaims = new DefaultClaims();

// check if token is expire
private Clock clock = DefaultClock.INSTANCE; 
private long allowedClockSkewMillis = 0; 
```

`.Parse()` basically compares the information where returned by`.builder`

To require validity of Claims we have a set of Methods to deal with
```java
.requireIssuedAt()
.requireIssuer()
.requireAudience()
.requireSubject()
.requireId()
.requireExpiration()
.requireNotBefore()
.require() // self-define the validation of Claims
```

### Java JWT methods correspond to Registered Claims

| jwt.builder() Methods |  Claims |
| -------------         | ------- |
|setIssuer              | sets the iss (Issuer) Claim
|setSubject             | sets the sub (Subject) Claim
|setAudience            |sets the aud (Audience) Claim **(receiver)**
|setExpiration          |sets the exp (Expiration Time) Claim
|setNotBefore           |sets the nbf (Not Before) Claim **(Token cant not use until ..)**
|setIssuedAt            |sets the iat (Issued At Time) Claim 
|setId                  |sets the jti (JWT ID) Claim **(unique ID)**

```java
String jws = Jwts.builder()
    .setIssuer("me") // client 
    .setSubject("Bob") // username
    .setAudience("you") // server
    .setExpiration(expiration) //a java.util.Date
    .setNotBefore(notBefore) 	//a java.util.Date 
    .setIssuedAt(new Date()) 	//for example, now
    .setId(UUID.randomUUID()) 	//just an example id
```

JSON
```json
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

### Private Claims

Create self-define payloads via `Jwts.claim()`
```java
Claims claims = Jwts.claims();
claims.put("userId", "1234567");
claims.put("role", "admin")
claims.setExpiration(exp);
```

JSON
```json
{
    "userId" : "1234567"
    "role"   : "admin"
    "exp"    : 1528360637
}
```

Via HashMap to create cliams and put it to `Jwts.setClaims(Map<?,?> claims)`
```java
// Time of Generated JWT
long nowMillis = System.currentTimeMillis();

Date now = new Date(nowMillis);

Map<String,Object> claims = new HashMap<String,Object>();

// self define claims (private Claims)
claims.put("uid", "DSSFAWDWADAS...");
claims.put("user_name", "admin");
claims.put("nick_name","DASDA121");

//** generate signature key 
//   (secret key) in local host 
//   (e.g from file directory in your system ...)
//** Once the secret key is hacked by someone, 
//     it can be used to access the server side by hacker
SecretKey key = generalKey();

JwtBuilder builder = 
        Jwts.builder() 
            .setClaims(claims) 
             //JWT ID：是JWT的身份證
             //不重複一次性token
            .setId(id)        
            .setIssuedAt(now)
            //作為用戶的唯一標誌(身份證)
            //e.g.a user id，role id etc...
            .setSubject(subject)
            // A JWT's Key Algorithm , Key Type
            .signWith(signatureAlgorithm, key); 

public SecretKey generalKey(){
  //Local host stringKey is
  //7786df7fc3a34e26a61c034d5ec8245d
  String stringKey = Constant.JWT_SECRET;

  //local host decode [B@152f6e2
  byte[] encodedKey = Base64.decodeBase64(stringKey);
  
  System.out.println(encodedKey);    //[B@152f6e2

  //7786df7fc3a34e26a61c034d5ec8245d
  System.out.println(
      Base64.encodeBase64URLSafeString(encodedKey));
  
  // Generate the Secrete Key
  SecretKey key = new SecretKeySpec(
      encodedKey, 0, encodedKey.length, "AES");

  // Encrypted encodeKey 
  // from encodeKey[0] 
  // to encodeKey[encodeKey.length] via AES
  return key;
}
```


### Code Example of Generate/Parse JWT

[Source Code](https://maizitoday.github.io/post/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E5%88%97-json-web-token/)

```java
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

        // generate jwt
        String jwt = Jwts.builder()
                        .setSubject(new Employee().toString())
                        .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                        // A key made of HS512
                        .signWith(SignatureAlgorithm.HS512, "PrivateKey") 
                        .compact();
        System.out.println(token);

        // Get User of this jwt
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

```java
@Component
public class JwtToken implements Serializable {

    private static final long EXPIRATION_TIME = 1 * 60 * 1000;
    /**
     * SECRET KEY SIGNATURE
     */
    private static final String SECRET = "learn to dance in the rain";

    /**
     * Build JWT
     */
    public String generateToken(HashMap<String, String> userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put( "userName", userDetails.get("userName") );

        return Jwts.builder()
                .setClaims( claims )
                .setExpiration( 
                    new Date( 
                        Instant.now(.toEpochMilli() + EXPIRATION_TIME))
                // ENCODE secret key with HS512
                .signWith( SignatureAlgorithm.HS512, SECRET )
                .compact();
    }

    /**
     * Parse JWT
     */
    public void validateToken(String token) throws AuthException {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token);
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
