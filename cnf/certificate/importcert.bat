keytool -keystore ../keystore -delete -alias trustcert
keytool -keystore ../keystore -noprompt -import -trustcacerts -alias trustcert -file cert.crt -keypass testtest
