ROOT = ../../../..

VERSION	= 1.0.1
NAME	= Index
PACKAGE = org.osgi.tools.indexer

SOURCES = \
	Index.java

BTOOL   = \
	-analyse \
	-property version $(VERSION)  \
	-property main    $(NAME) \
	-property package $(PACKAGE) \
	-manifest Manifest.mf


include $(ROOT)/cnf/package.mk
