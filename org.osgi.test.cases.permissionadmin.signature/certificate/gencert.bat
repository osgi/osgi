@echo off

keytool -genkey -alias test -keyalg RSA -keysize 1024 -sigalg MD5WithRSA -validity 3000 -keystore .keystore -keypass testtest -storepass testtest -dname "CN=permission.signature.test, L=Cologne, ST=Nordrhain Westfallen, O=ACME Inc, OU=ACME Cert Authority, C=US"

keytool -export -alias test -file cert.crt -storepass testtest -keystore .keystore
