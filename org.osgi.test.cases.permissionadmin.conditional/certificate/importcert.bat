@echo off

keytool -delete -alias trustcert
keytool -noprompt -import -trustcacerts -alias trustcert -file cert.crt -keypass testtest
