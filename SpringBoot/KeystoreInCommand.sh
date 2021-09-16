                                # **********************
                                # *Command Keytool的參數*
                                # **********************
                                
# Reference 
#     https://blog.csdn.net/u010142437/article/details/16370017   
#     https://geotrust.cloudmax.com.tw/guide/faq_keytool.asp                               
# -genkey     : 在使用者主目錄中建立一個預設檔案".keystore",還會產生一個預設alias叫mykey. 
#             : mykey中包含使用者的Public Key、Private key和Certificate
#             : (在沒有指定生成位置的情況下,keystore會存在使用者系統預設目錄)
#------------------------------------------------------------------------------------
#-storepasswd : Change keystore password  
keytool -keypasswd -alias duke -keypass dukekeypasswd -new newpass
#-keypasswd   : Change private key    
#             : keytool -keypasswd -alias ACCESSKEYENTRYNAME -keypass OLDPASSWORD -new NEWPASSWORD -storepass STOREPASSWORD -keystore KEYSTORENAME
keytool -genkey -alias duke -keypass dukekeypasswd
    
keytool -storepasswd -keystore e:/yushan.keystore(需修改口令的keystore) -storepass 123456(原始密碼) -new yushan(新密碼)

#-alias       : 產生別名對應一個KEYSTORE
#             : 所有的keystore入口entries(private key 和信任憑證入口)是通過唯一的別名訪問
#-keystore    : 指定金鑰庫的名稱(產生的各類資訊將不在.keystore檔案中) 
#             : home/pi/MYKEYSTORENAME.keystore (指定生成CERTIFICATE的位置和CERTIFICATE名稱)
#-keyalg      : 指定金鑰的演算法 (e.g. RSA, DSA{預設})
#-keysize     : 指定金鑰長度(預設1024)
#-validity    : 指定建立的CERTIFICATE有效期多少天(預設90天)
#-storepass   : 指定金鑰庫的密碼(獲取keystore資訊所需的密碼)
#-keypass     : 指定別名條目(ENTRY)的密碼(PRIVATE KEY)
#-dname       : 指定CERTIFICATE擁有者資訊 例如：  "CN=名字與姓氏,OU=(O)組織(U)單位名稱,O=組織名稱,L=城市或區域名稱,ST=州或省份名稱,C=單位的兩字母國家程式碼"
keytool -genkey -alias jian -keypass 1234 -keyalg RSA -keysize 1024 -validity 100 -keystore  -storepass 123456
keytool -genkey -alias jian -keypass 1234 -keyalg RSA -keysize 1024 -validity 365 -keystore  /home/pi -storepass 123456 -dname "CN=HashCode, OU=RD, O=GOOGLE, L=Essen, ST=Nordwestfallen, C=de"

#CHECK FOR THE INFORMATION*************************************************
#-list        顯示金鑰庫中的CERTIFICATE資訊   
#-v           顯示金鑰庫中的CERTIFICATE詳細資訊
#keytool -list -v -keystore 金鑰庫的名稱 -storepass 密碼
keytool -list  -v -keystore e:/keytool /yushan.keystore -storepass 123456 
#**************************************************************************


#EXPORT/IMPORT THE CERTIFICATE****************************************************************************
# -export    :將別名指定的CERTIFICATE/PUBLIC KEY匯出到指定的FILE.cer  
# -file      :File that Stores the certificate that authenticates someone's public key or trusted cerficate
#             :keytool -export -alias 需要匯出的別名 -keystore 金鑰庫的名稱 -file 指定匯出的CERTIFICATE位置及CERTIFICATE名稱 -storepass 密碼
## Export the Cert to the File 
#    When A client requests the Authenticated Certificate from you
keytool -export -alias mykey -file MJ.cer

### This sample command exports jane’s certificate to the file janecertfile.cer. 
#     That is, if jane is the alias for a key entry, the command exports the certificate at the bottom of the certificate chain in that keystore entry. 
#     This is the certificate that authenticates jane’s public key.
#     If, instead, jane is the alias for a trusted certificate entry, then that trusted certificate is exported
keytool -export -alias jane -file janecertfile.cer

### -import    : To import a certificate from a file.cer
#             : keytool -import -alias 指定匯入條目的別名 -keystore 指定keystore -file 需匯入的CERTIFICATE
### Import a certificate for two reasons:
#   1. add it to the list of trusted certificates, or
#   2. import a certificate reply received from a CA as the result of submitting a Certificate Signing Request to that CA.
### This sample command imports "the certificate(s) in the file jcertfile.cer" 
#       and stores it in the keystore entry identified by the alias joe.
keytool -import -alias joe -file jcertfile.cer

# -printcert :檢視匯出的CERTIFICATE資訊                       
keytool -printcert -file The_Certificate.crt
#*********************************************************************************************************


#DELETE THE ALIAS**********************************************************
###-delete  : 刪除金鑰庫中某條目          
keytool -delete -alias 指定需刪除的別名  -keystore 金鑰庫的名稱 -storepass 密碼
### Delete The CA whose name is RapaServer in keystore Mykeystore
keytool -delete -alias RapaServer -keystore Mykeystore
#**************************************************************************
