ROOT   = ../../../../..

NAME		= ProvisioningTestCase
PACKAGE 	= org.osgi.test.cases.provisioning
VERSION     = 3.0
DESC        = "Tests the recommended Initial Provisioning"
IMPORTS		= $(ROOT)/jar/servlet.jar
BEFORE      = tbc
NO_BOOTCP	= 1

SOURCES = \
	ProvisioningTestCase.java \
	RshServlet.java \
	RSHTest.java

BTOOLPARAMS = \
	-skip javax.crypto \
	-private org.osgi.test.cases.provisioning \
	javax.servlet.Servet \
    $(IMPORTTBC) \
    $(IMPORTLOG) \
	-importas www/simple.ipa tbc/simple.ipa \
	-importas www/test-1.jar tbc/simple/$(PACKAGE).tbc.simple.jar \
	-importas www/delay.jar tbc/simple/$(PACKAGE).tbc.simple.jar \
	-importas www/simple.jar tbc/simple/$(PACKAGE).tbc.simple.jar
	


include $(ROOT)/cnf/testcase.mk
