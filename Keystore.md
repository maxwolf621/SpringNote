###### tags: `Spring` `JAVA`
#### [Diagram for KeyStroe btw Clinet and Server](http://support.sas.com/rnd/javadoc/93/Foundation/com/sas/net/ssl/doc-files/jsse_index.html)
#### [Note for JWT](/o9RYmd2DR96e4XId64Ao5w)

#### [Class KeyStore Methods](https://docstore.mik.ua/orelly/java-ent/security/ch11_02.htm)

# KeyStore
[TOC]

![](https://i.imgur.com/h2jzGzv.png)

## What is KeyStore

[Reference](https://reurl.cc/kVYaN9)

### A KeysSore can be a **repository** where stores
- private keys
- certificates 
- symmetric keys 
    > This is typically a file, but the storage can also be handled in different ways (e.g. cryptographic token or using the OS's own mechanism.)


### KeyStore is also a class which is part of the standard *API*.

It is essentially a way to **load**, **save** and generally interact with one of the physical Key Stores (repositories) as described above. 

A KeyStore can also be purely in memory, if you just need the API *abstraction* for your application.

- How to load and handle such a KeyStore instance depends on the format of the keystore file (or other storage system)that backs it.
    > Multiple formats are available. 
    >> Some of the most common are JKS and PKCS#12


### KeyStore is also as the counterpart of TrustStore

This is where it can get confusing, since both *keystore* and *truststore* are keystores, they're just used for different purposes.  

Let say them literally
- The *keystore* is used to initialise the *key manager*
- The *truststore* is used to initialise the *trust manage*r. 

:::info
### From the JSSE reference guide:
![](https://i.imgur.com/4pttWPY.png)

- A TrustManager(驗證) determines whether the remote authentication credentials (and thus the connection) should be trusted.
- A KeyManager(授權) determines which authentication credentials to send to the remote host.

> Essentially, a keystore used as a truststore will contain a number of (CA) certificates that you're willing to trust:
>> Those are the trust anchors you are going to use to verify remote certificates you don't already know and trust. 
:::

 

In contrast, **a keystore used as a keystore will contain your own <u>certificate</u> and its <u>private key</u>**: this is what you're going to use to authenticate yourself to a remote party (when required).

>There is a _<u>default truststore</u>_ bundled with the **JRE** `(/lib/security/cacerts)`.  
> There isn't a default keystore, since it's usually a more explicit step for the user.



## Definitions in Key Store
- Alias
 > **the alias field should be a unique string to identify the key entry**.  
 > This applies to all types such a trusted and intermediate.

## Connection btw Client and Server 

![](https://i.imgur.com/3pF4fE5.png)
![](https://i.imgur.com/FilgCjC.png)




## Methods

### To initialize a key 
Using
`.getInstance(String Algorithm_Type)`
`.load(InputStream, char[] password )`
```java=
try { 
     keyStore = KeyStore.getInstance("JKS");
     InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
     keyStore.load(resourceAsStream, "secret".toCharArray());
 } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
     throw new SpringRedditException("Exception occurred while loading keystore");
 }
```

#### getInstance(String Algorithm_Type)

To create a KeyStore via Algorithm_Type (by default is JKS)

### load(InputStream, char[] Password)

Initialize the KeyStore from the data provided over the given parameter InputStream.  

**The integrity of the keystore is typically protected by using a message digest.**

Before the digest is created, the password is added to the digest data; **this means that the digest cannot be re-created from a tampered keystore without knowledge of the password.**  


> `InputStream`
>> message digest
>> when the keystore is stored, a message digest that represents the data in the keystore is also stored.  
>
> `char[] password`
>> the password could be used for anything else (including encrypting the entire KeyStore) if you were to write your own implementation. 
>> The Sun implementation of the KeyStore class requires another password to access each private key in the Key Store, so this isn't a potential 

:::info
**The password for this method can be null, in which case the keystore is loaded and not verified.**  
> This use of the password is a property of the Sun implementation of the KeyStore class;  
:::

## Get Private key

#### `getKey(String Alias, char[] Password)`
> Return the **private key** for the entry associated with the given alias. 

```java=
private PrivateKey getPrivateKey() {
        try {
        // alias : springblong
        // password : secret 
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore");
        }
    }
```

## Generate Token via Jwt library

[Definition of principal](/CrTB3w_mRm-2SFVF648fpw)

```java=
public String generateToken(Authentication authentication) 
{
    // get Principal
    org.springframework.security.core.userdetails.User \
     principal = (User) authentication.getPrincipal();
    
    return Jwts.builder()
            // subject : human operator
            .setSubject(principal.getUsername())
            // signature
            .signWith(getPrivateKey())
            // 提交
            .compact();
}
```

## Get Public Key

![](https://i.imgur.com/l4GMn1u.png)
```java=
private PublicKey getPublickey() {
    try {
        return keyStore.getCertificate("springblog").getPublicKey();
    } catch (KeyStoreException e) {
        throw new SpringRedditException("Exception occured while retrieving public key from keystore");
    }
}
```

#### public final Certificate getCertificate(String alias) 
Return the certificate associated with the given alias. 

If the alias represents a key entry, the certificate returned is the user's certificate (that is, the first certificate in the entry's certificate chain); 
certificate entries have only a single certificate. 

#### public final PublicKey getPublicKey()
> Returns the public key corresponding to this certificate.
```java=
public PublicKey GetPublicKey(Certificate cert){
    return cert.getPublicKey()
}
```


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