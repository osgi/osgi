ROOT   = ../../../../../..

VERSION 	= 1.0
NAME		= A
PACKAGE 	= org.osgi.test.cases.xml.tb2
ON_TARGET	= true

IMPORTS = $(ROOT)/jar/saxon.jar

SOURCES = \
	A.java \
	B.java \
	C.java \
	D.java

BTOOL = \
	-private $(PACKAGE) \
	-private org.osgi.util.xml \
	-package org.osgi.util.xml \
	-analyse \
	-property version $(VERSION) \
	-property activator org.osgi.util.xml.XMLParserActivator \
	-importas META-INF/services/javax.xml.parsers.DocumentBuilderFactory javax.xml.parsers.DocumentBuilderFactory

include $(ROOT)/cnf/package.mk
