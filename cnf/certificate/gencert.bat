@echo off

keytool -genkey -alias test -keyalg RSA -keysize 1024 -sigalg MD5WithRSA -validity 3000 -keystore .keystore -keypass testtest -storepass testtest -dname "CN=John Smith,O=ACME Inc,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US"

keytool -export -alias test -file cert.crt -storepass testtest -keystore .keystore
