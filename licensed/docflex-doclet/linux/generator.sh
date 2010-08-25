#!/bin/sh

#----------------------------------------------------------------------
# Please specify the location of Sun JDK 1.6 or 1.5 here
#----------------------------------------------------------------------
JDK=/usr/java/jdk1.6.0_02

#----------------------------------------------------------------------
# The location of DocFlex/Doclet home directory
#----------------------------------------------------------------------
DFH=..

#----------------------------------------------------------------------
# Javadoc options: DocFlex Doclet class
#----------------------------------------------------------------------
OPTIONS="-docletpath ${DFH}/lib/docflex-doclet.jar -doclet com.docflex.javadoc.Doclet"

#----------------------------------------------------------------------
# Specify the DocFlex main configuration file prepared for Linux
#----------------------------------------------------------------------
OPTIONS="${OPTIONS} -docflexconfig ${DFH}/linux/docflex.config"

#----------------------------------------------------------------------
# -J-Xmx option sets the maximum heap size allocated by JVM.
# Check this option when running on big projects!
#
# DocFlex generator is pretty hungry for memory as it stores lots
# of temporary data in hash-tables in order to boost performance.
# However, according to our tests, 512 Mb heap seemed to be quite
# enough to generate an HTML documentation for the entire JDK 6
# Java API sources containing more than 4000 classes.
#----------------------------------------------------------------------
OPTIONS="-J-Xmx512m ${OPTIONS}"

#----------------------------------------------------------------------
# GENERATING DOCUMENTATION
#----------------------------------------------------------------------
# The command following this comment will generate sample documentation
# by a single Java package.
#
# Note, DocFlex Doclet supports -link/-linkoffline options.
#
# For example, if you are connected to Internet, the following command
# will generate the same sample documentation however with the direct
# hyperlinks to the online Java API docs located at Sun Java Technology
# web-site:
#
# ${JDK}/bin/javadoc ${OPTIONS} \
#  -link http://java.sun.com/javase/6/docs/api/ \
#  -d out -sourcepath demo java5
#----------------------------------------------------------------------

${JDK}/bin/javadoc ${OPTIONS} -d ${DFH}/out -sourcepath ${DFH}/demo java5
