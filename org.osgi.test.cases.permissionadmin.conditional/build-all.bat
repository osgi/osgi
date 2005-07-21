@echo off

ren build.properties build-main.properties
ren build-signed.properties build.properties
call ant
ren build.properties build-signed.properties
ren build-main.properties build.properties

jarsigner -keystore certificate\.keystore -storepass testtest -keypass testtest tb1.jar test
jarsigner -keystore certificate\.keystore -storepass testtest -keypass testtest tb2.jar test

call ant
