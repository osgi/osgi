ROOT   = ../../../../../..

VERSION 	= 1.0
NAME		= A
PACKAGE 	= org.osgi.test.cases.xml.tb1
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
	-importas META-INF/services/javax.xml.parsers.SAXParserFactory javax.xml.parsers.SAXParserFactory

include $(ROOT)/cnf/package.mk
