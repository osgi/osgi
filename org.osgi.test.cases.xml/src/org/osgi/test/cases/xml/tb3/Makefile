ROOT   = ../../../../../..

VERSION 	= 1.0
NAME		= CustomXMLParserActivator
PACKAGE 	= org.osgi.test.cases.xml.tb3
ON_TARGET	= true
ON_TARGET	= true

IMPORTS = $(ROOT)/jar/saxon.jar

SOURCES = \
	CustomXMLParserActivator.java \
	SimpleDocumentBuilderFactory.java \
	SimpleSAXParserFactory.java 

BTOOL = \
	-package org.osgi.util.xml \
	-package org.osgi.test.cases.util \
	-private $(PACKAGE) \
	-private org.osgi.util.xml \
	-private org.osgi.test.cases.util \
	-analyse \
	-property version $(VERSION) \
	-property activator $(PACKAGE).$(NAME) \
	-importas META-INF/services/javax.xml.parsers.SAXParserFactory javax.xml.parsers.SAXParserFactory \
	-importas META-INF/services/javax.xml.parsers.DocumentBuilderFactory javax.xml.parsers.DocumentBuilderFactory

include $(ROOT)/cnf/package.mk
