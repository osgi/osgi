#!/bin/sh

#----------------------------------------------------------------------
# Please specify the location of Sun JDK 1.4.x here
#----------------------------------------------------------------------
JDK=/usr/java/j2sdk1.4.2_14

#----------------------------------------------------------------------
# The location of DocFlex/Doclet home directory
#----------------------------------------------------------------------
DFH=..

#----------------------------------------------------------------------
# Javadoc options: DocFlex Doclet class
#----------------------------------------------------------------------
OPTIONS="-docletpath ${DFH}/lib/docflex-doclet-1.4.jar -doclet com.docflex.javadoc.Doclet"

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
# -breakiterator option is processed by Javadoc itself (not a doclet).
# Setting it may improve summary descriptions as well as suppress
# lots of warnings that Javadoc 1.4 would display without it.
#----------------------------------------------------------------------
OPTIONS="-breakiterator ${OPTIONS}"

#----------------------------------------------------------------------
# GENERATING DOCUMENTATION
#----------------------------------------------------------------------
# The command following this comment will generate sample documentation
# by a single Java package.
#
# Note, DocFlex/Doclet supports -link/-linkoffline options.
#
# For example, if you are connected to Internet, the following command
# will generate the same sample documentation however with the direct
# hyperlinks to the online Java API docs located at Sun Java Technology
# web-site:
#
# ${JDK}/bin/javadoc ${OPTIONS} \
#  -link http://java.sun.com/j2se/1.4.2/docs/api/ \
#  -d out -sourcepath demo java4
#----------------------------------------------------------------------

${JDK}/bin/javadoc ${OPTIONS} -d ${DFH}/out -sourcepath ${DFH}/demo java4
