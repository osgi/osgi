OT   = ../../../../../..

IMPORTS	=
VERSION = 1.0
NAME = Activator
PACKAGE = org.osgi.test.cases.dynpkgimport.tb3
ON_TARGET	= true

SOURCES = \
	Activator.java \
	tlx/TestLib.java

BTOOL = \
	-export $(PACKAGE) \
	-analyse \
	-showmanifest \
	-property version $(VERSION) \
	-property activator $(PACKAGE).$(NAME) \
	-importas org/osgi/test/cases/dynpkgimport/tlx/TestLib.class tlx/TestLib.class

include $(ROOT)/cnf/package.mk
