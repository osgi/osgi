@echo off

ren build.properties build-main.properties
ren build-signed.properties build.properties
call ant
ren build.properties build-signed.properties
ren build-main.properties build.properties
ren tb2.jar tb2_signed.jar
ren tb3.jar tb3_signed.jar

jarsigner -keystore certificate\.keystore -storepass testtest -keypass testtest tb2_signed.jar test
jarsigner -keystore certificate\.keystore -storepass testtest -keypass testtest tb3_signed.jar test

call ant
