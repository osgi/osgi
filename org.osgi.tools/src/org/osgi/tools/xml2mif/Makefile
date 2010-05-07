ROOT		= ../../../..
VERSION		= 1.0
NAME		= xml2mif
PACKAGE		= org.osgi.tools.xml2mif
IMPORTS		= $(ROOT)/jar/aelfred.jar


SOURCES	= \
	XML2MIF.java
	

BTOOL   = \
	-analyse \
	-failok \
	-property version $(VERSION)  \
	-property main    $(NAME) \
	-property package $(PACKAGE) \
	-manifest Manifest.mf
	

include $(ROOT)/cnf/package.mk


