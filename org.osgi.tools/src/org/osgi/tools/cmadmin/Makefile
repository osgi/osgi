ROOT			= ../../../..

NAME		= CMCmd
PACKAGE		= org.osgi.tools.cmadmin
VERSION		= 1.0.0
IMPORTS		= $(ROOT)/jar/osgi.jar $(ROOT)/jar/org.osgi.tools.console.jar

SOURCES		= \
	 CMCmd.java
	

BTOOL = \
	-analyse \
	-private 	org.osgi.tools.cmadmin \
	-property name $(NAME) \
	-property activator $(PACKAGE).CMCmd \
	-property version $(VERSION)

include $(ROOT)/cnf/package.mk




