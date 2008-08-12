ROOT		= ../../../..
VERSION		= 1.0
NAME		= XmlDoclet
PACKAGE		= org.osgi.tools.xmldoclet
JAVA_HOME	:= c:/java/j2sdk1.4.1_01/bin/java/../..
IMPORTS		= tools.jar


SOURCES	= \
	XmlDoclet.java \
	HtmlCleaner.java
	

BTOOL   = \
	-analyse \
	-failok \
	-property version $(VERSION)  \
	-property main    $(NAME) \
	-property package $(PACKAGE) \
	-manifest Manifest.mf
	

include $(ROOT)/cnf/package.mk


