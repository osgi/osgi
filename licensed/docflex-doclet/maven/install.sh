#!/bin/sh

#----------------------------------------------------------------------
# Specify the location of Sun JDK 6.x or 5.x here
#----------------------------------------------------------------------
JAVA_HOME=/usr/java/jdk1.6.0_02

#----------------------------------------------------------------------
# Specify the location of Maven 2 here
#----------------------------------------------------------------------
M2_HOME=/usr/local/apache-maven/apache-maven-2.0.9

#----------------------------------------------------------------------
# Other variables
#----------------------------------------------------------------------

DOCFLEX_DOCLET_HOME=..
DOCFLEX_DOCLET_VER=1.5.6

#----------------------------------------------------------------------
# Installing DocFlex/Doclet Java library in Maven repository
#----------------------------------------------------------------------

${M2_HOME}/bin/mvn install:install-file -Dfile=${DOCFLEX_DOCLET_HOME}/lib/docflex-doclet.jar -DgroupId=docflex -DartifactId=docflex-doclet -Dversion=${DOCFLEX_DOCLET_VER} -Dpackaging=jar

sleep 10
