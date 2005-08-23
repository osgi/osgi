@echo off

keytool -delete -alias permsig
keytool -noprompt -import -trustcacerts -alias permsig -file cert.crt -keypass testtest
