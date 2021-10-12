###### tags: `Spring` `JAVA`
[Diagram for KeyStroe btw Clinet and Server](http://support.sas.com/rnd/javadoc/93/Foundation/com/sas/net/ssl/doc-files/jsse_index.html)  
[Note for JWT](/o9RYmd2DR96e4XId64Ao5w)  
[Class KeyStore Methods](https://docstore.mik.ua/orelly/java-ent/security/ch11_02.htm)  
[Android Keystore](https://medium.com/joe-tsai/%E4%BD%BF%E7%94%A8keystore-%E5%84%B2%E5%AD%98%E6%95%8F%E6%84%9F%E6%80%A7%E8%B3%87%E6%96%99-92ad9b236e58)
# KeyStore
[TOC]
![](https://i.imgur.com/h2jzGzv.png)  
[Reference](https://reurl.cc/kVYaN9)  

## A KeysSore can be a **repository** where stores
- Key entry : private keys 
- Certificate entry : certificates (containing a public key)

### Certificate Chains
![image](https://user-images.githubusercontent.com/68631186/123245968-e6ea2200-d517-11eb-8eb0-35f238a5f145.png)
[What is Certificate Chains](https://knowledge.digicert.com/solution/SO16297.html)
- The first certificate in the chain contains the public key corresponding to the private key.

When keys are first generated, the chain starts off containing a single element, a self-signed certificate.  
- A self-signed certificate is one for which the issuer (signer) is the same as the subject (the entity whose public key is being authenticated by the certificate).  
- Whenever the `-genkey` is called to generate a new public/private key pair, it also wraps the public key into a self-signed certificate.

Later, after a Certificate Signing Request (CSR) has been generated (`-certreq` subcommand) and sent to a Certification Authority (CA), the response from the CA is imported (`-import`), and the self-signed certificate is replaced by a chain of certificates.  
At the bottom of the chain is the certificate (reply) issued by the CA authenticating the subject’s public key. 
The next certificate in the chain is one that authenticates the CA’s public key.  


## KeyStore As Standard API  
KeyStore is also a class which is part of the standard *API*.  
It is essentially a way to **load**, **save** and generally interact with one of the physical Key Stores (repositories) as described above.   
A KeyStore can also be purely in memory, if you just need the API *abstraction* for your application.  

- How to load and handle such a KeyStore instance depends on the format of the keystore file (or other storage system)that backs it.
    > 1. Multiple formats are available. 
    > 2. Some of the most common are `JKS` and `PKCS#12`

KeyStore is also as the counterpart of TrustStore  
This is where it can get confusing, since both *keystore* and *truststore* are keystores, they're just used for different purposes.  
Say it literally  
- The *keystore* is used to initialise the *key manager*  
- The *truststore* is used to initialise the *trust manager*.   

![image](https://user-images.githubusercontent.com/68631186/123222993-15113700-d503-11eb-85fb-7ac232928c5c.png)        
![](https://i.imgur.com/3pF4fE5.png)  
![](https://i.imgur.com/FilgCjC.png)  


:::info  
![](https://i.imgur.com/4pttWPY.png)  
- A TrustManager (Who I can trust)  
    > determines whether the **remote** authentication credentials (and thus the connection) should be trusted.  
- A KeyManager (Send which key to the trust one)  
    > determines which authentication credentials to send to the remote host.  
Essentially, a keystore used as a truststore will contain a number of (CA) certificates that you're willing to trust : Those are the trust anchors you are going to use to verify remote certificates you don't already know and trust. 
:::  

![image](https://user-images.githubusercontent.com/68631186/123208003-e8542400-d4f0-11eb-80d3-a540866af419.png)  
- **A keystore used as a keystore will contain your own <u>certificate</u> and its <u>private key</u>**: this is what you're going to use to authenticate yourself to a remote party (when required).                                                                                    

## Alias
- **All keystore entries (key and trusted certificate entries) are accessed via unique aliases.**  
- Ignore Case. for instance `InT` is equal as `iNt` 

# Keystore In Java 

## Initialize a keystore
```java                                                     
try {           
     /**
      * To create a KeyStore instance named keystore via Algorithm_Type
      *         KeyStore.getInstance(String Algorithm_Type)
      */
     keyStore = KeyStore.getInstance("JKS");                                          
     
     /**
      * The instance keystore is a keystore existing in the filesystem /springblog.jks    
      *         KeyStore.load(InputStream, char[] password )  
      */
     InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
     keyStore.load(resourceAsStream, "secret".toCharArray());
 } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
     throw new SpringRedditException("Exception occurred while loading keystore");
 }
```
`load(InputStream, char[] Password)`  
Initialize the KeyStore from the data provided over the given parameter `InputStream`.  
**The integrity of the keystore is typically protected by using a message digest.**
Before the digest is created, the password is added to the digest data; **this means that the digest cannot be re-created from a tampered keystore without knowledge of the password.**  

> `InputStream`
>> when the keystore is stored, a message digest that represents the data in the keystore is also stored.  

> `char[] password`
>> the password could be used for anything else (including _encrypting_ the entire KeyStore) if you were to write your own implementation. 
>> The Sun implementation of the KeyStore class requires another password to access each private key in the Key Store, so this isn't a potential 

:::info
**The password for this method can be null, in which case the keystore is loaded and not verified.**  
> This use of the password is a property of the Sun implementation of the KeyStore class
:::

## Get Private key  
It return the **private key** for the entry associated with the given **alias**.  
```java
private PrivateKey getPrivateKey() {
        try {
        // getKey(String Alias, char[] Password)
        // alias : springblong (keystore name)
        // password : secret 
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore");
        }
    }
```

## Generate Token via Keystore
[Definition of principal](/CrTB3w_mRm-2SFVF648fpw)  

To Generate Token of A User
- A user principal
- A keystore privateKey
- Using jwt package to generate the token (build with principal, keystore private key)
```java
public String generateToken(Authentication authentication) 
{
    // Principal
    org.springframework.security.core.userdetails.User principal = (User) authentication.getPrincipal();
    
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
```java
private PublicKey getPublickey() {
    try {
        return keyStore.getCertificate("springblog").getPublicKey();
    } catch (KeyStoreException e) {
        throw new SpringRedditException("Exception occured while retrieving public key from keystore");
    }
}
```  

`public final Certificate getCertificate(String alias)`  
It returns the certificate associated with the given alias.  

`public final PublicKey getPublicKey()`
It returns the public key corresponding to this certificate.
```java
public PublicKey GetPublicKey(Certificate cert){
    return cert.getPublicKey()
}
```
