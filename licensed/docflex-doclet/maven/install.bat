::----------------------------------------------------------------------
:: Specify the location of Sun JDK 6.x, 5.x or 1.4.x here
::----------------------------------------------------------------------
@set JAVA_HOME=C:\jdk1.6

::----------------------------------------------------------------------
:: Specify the location of Maven 2 here
::----------------------------------------------------------------------
@set M2_HOME=C:\apache-maven-2.0.9

::----------------------------------------------------------------------
:: Other variables
::----------------------------------------------------------------------

@set DOCFLEX_DOCLET_HOME=..
@set DOCFLEX_DOCLET_VER=1.5.6

::----------------------------------------------------------------------
:: Installing DocFlex/Doclet Java library in Maven repository
::----------------------------------------------------------------------

call %M2_HOME%\bin\mvn.bat install:install-file -Dfile=%DOCFLEX_DOCLET_HOME%\lib\docflex-doclet.jar -DgroupId=docflex -DartifactId=docflex-doclet -Dversion=%DOCFLEX_DOCLET_VER% -Dpackaging=jar

pause