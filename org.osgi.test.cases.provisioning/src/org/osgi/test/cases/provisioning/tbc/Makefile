ROOT	= ../../../../../..

NAME		= ProvisioningControl
PACKAGE 	= org.osgi.test.cases.provisioning.tbc
VERSION     = 10.0
DESC        = "Tests the recommended Initial Provisioning specification in Release 3"
BEFORE		= simple
ON_TARGET	= true

start: all



SOURCES = \
	ProvisioningControl.java

BTOOLPARAMS = \
	-export org.osgi.test.cases.util \
    -analyse

RESOURCES = \
	simple.ipa \
	ref.ipa \
	osgi.cert \
	case-ref.ipa \
	delay-ref.ipa \
	spid.ipa \
	ipa-ref-start.ipa \
	ipa-ref.ipa \
	keys-only.ipa \
	unicode.ipa




IPA	= $(BT) -compress -zip $@ \
	-ipa text $@ true \
	-ipa text load-status $@

#
# All bundles and texts are contained in the
# the file
#
simple.ipa	: simple/$(PACKAGE).simple.jar 
	$(IPA) \
	-ipa bundle test-0-prov.jar $< \
	-ipa bundle test-1-prov.jar $< \
	-ipa bundle-url test-2-prov.jar http://NON_EXISTENT_HOST/NON_EXISTENT_FILE \
	-ipa text provisioning.start.bundle test-0-prov.jar \
	-ipa text text-1 TEST1 \
	-ipa text text-2 TEST2 \
	-ipa text /text-3 TEST3 \
	-ipa binary osgi.cert osgi.cert

#
# Should get the test-1.jar bundle from the remote system
# 
ref.ipa : simple/$(PACKAGE).simple.jar
	$(IPA) \
	-ipa bundle test-0-prov.jar $< \
	-entry-text test-1-prov.jar director:simple.jar text/x-osgi-bundle-url \
	-ipa bundle-url test-2-prov.jar http://NON_EXISTENT_HOST/NON_EXISTENT_FILE \
	-ipa text provisioning.start.bundle test-0-prov.jar \
	-ipa text text-1 TEST1 \
	-ipa text text-2 TEST2 \
	-ipa text /text-3 TEST3 \
	-ipa binary osgi.cert osgi.cert

#
# Should get the test-1.jar bundle from the remote system
# but we use an unusual case for the type
#
case-ref.ipa : simple/$(PACKAGE).simple.jar
	$(IPA) \
	-entry-text case-ref-prov.jar director:simple.jar text/X-OSGI-BUNDLE-URL \
	-ipa text provisioning.start.bundle case-ref-prov.jar 

#
# We are referring to a jar that takes some time to load and
# see if start is done after load
#

delay-ref.ipa : simple/$(PACKAGE).simple.jar
	$(IPA) \
	-entry-text simple-prov.jar director:simple.jar text/x-osgi-bundle-URL \
	-entry-text delay-prov.jar director:delay.jar text/x-osgi-bundle-URL \
	-ipa text provisioning.start.bundle simple-prov.jar 

#
# This ipa is used to check if we get a Service Platform
# identifier
# 
spid.ipa :  simple/$(PACKAGE).simple.jar
	$(IPA) \

#
# Simple reference to simple.ipa on the director
#
ipa-ref.ipa : Makefile
	$(IPA) \
	-ipa text provisioning.reference director:simple.ipa \


#
# Will install + start a bundle + get a reference which also starts
# the agent.
#
ipa-ref-start.ipa :	simple/$(PACKAGE).simple.jar
	$(IPA) \
	-ipa bundle local-prov.jar $< \
	-ipa text provisioning.reference director:simple.ipa \
	-ipa text provisioning.start.bundle local-prov.jar \


keys-only.ipa : Makefile
	$(IPA) \
	-ipa text some.key.1 some.value.111 \
	-ipa text some.key.2 some.value.22 \
	-ipa text some.key.4 some.value.4 

unicode.ipa : Makefile
	$(IPA) \
	-ipa text unicode.string v\\u00D0\\u00D9v \


include $(ROOT)/cnf/testbundle.mk


