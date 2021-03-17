Test Cases of org.osgi.test.cases.enocean: Explanations

Authors:
- Andre BOTTARO, Orange.
- Victor PERRON, Orange.
- Maïlys ROBIN, Orange.
- Antonin CHAZALET, Orange.
- Cyrille BAREAU, Orange.
Date (last edited): 17th June 2015.

1) Introduction

These test cases are located in the “residential” part of https://www.osgi.org/members/git/build.git
(Use https://www.osgi.org/members/gitweb/build.git/shortlog/refs/heads/residential for web-browsing).

One project is involved:
- org.osgi.test.cases.enocean, is the main project. It contains all the tests, 
and almost all the elements they required.


The test cases can be launched via the Ant task named “test” available in the 
build.xml file located in org.osgi.test.cases.enocean.

2) org.osgi.test.cases.enocean project

The org.osgi.test.cases.enocean project contains several test cases. They are named:
- org.osgi.test.cases.enocean.EnOceanBasicTestCase,
- org.osgi.test.cases.enocean.EnOceanBroadcastRPCTestCase.
- org.osgi.test.cases.enocean.EnOceanHostTestCase,
- org.osgi.test.cases.enocean.EventTestCase,
- org.osgi.test.cases.enocean.ExportTestCase,
- org.osgi.test.cases.enocean.ManualRegistrationTestCase,
- org.osgi.test.cases.enocean.PersistencyTestCase,
- org.osgi.test.cases.enocean.RegistrationTestCase,
- org.osgi.test.cases.enocean.SignatureTestCase.

EnOceanBasicTestCase contains the following tests:
- testInterfaceExceptions, tests that common errors cases are properly handled 
(i.e. that the relevant exceptions are thrown).
- testRPC, tests RPC sending and receiving, i.e. insert an EnOcean device, 
and test a RPC invocation on this device.
- testUseOfDescriptions, tests that a properly set profile ID in a raw 
EnOceanMessage is enough to extract all the needed information, provided the 
necessary descriptions are known.

EnOceanBroadcastRPCTestCase:
- testExportBroadcastRPC, tests broadcast RPC exportation: creates a RPC,
sends it as a TOPIC_RPC_BROADCAST event, checks that the message has been
sent, received by the base driver, and checked through the step service, and
that the payload of the received event, processed as a SYS_EX message, is
equal to the payload of the original RPC.

EnOceanHostTestCase is intented to contain the tests related to EnOceanHost:
- testEnOceanHostServiceAvailability, tests that at least one EnOceanHost 
service is registered when an implementation of the EnOcean specification is 
running.

EventTestCase:
- testEventNotification, tests event notification when passing an actual 
message to the Base Driver. This also tests the MessageSet registration since 
the Base Driver needs to know about it before firing on EventAdmin.

ExportTestCase:
- testDeviceExport, tests device exportation: registers a local device as a 
service to be exported by the base driver, checks that a chip ID has been 
created and set as a property, sends a message on the EnOcean network, checks 
that the message has been sent, received by the base driver, and checked by 
the TCK through the step service.

ManualRegistrationTestCase:
- testManualDeviceRegistration, tests initial device registration from a raw 
Radio teach-in packet that doesn’t contain any profile data, and that is 
triggered through the step service. This test thus requires the end-user 
involvement. Finally, the TCK checks that the device's profile has been properly 
updated.

PersistencyTestCase:
- testDeviceExportPersistency, tests device export persistency: registers a 
device, get its chip ID (referred to as the original chip ID), stops, and 
restarts the base driver, finally get the device’s new chip ID, and checks 
that the original chip ID, and the new one are equal.

RegistrationTestCase:
- testAutoDeviceRegistration, tests initial, and automatic device registration 
from a raw Radio teach-in packet that is triggered through the step service.

SignatureTestCase:
- testSignatures, cf. testSignatures() method from org.osgi.test.support.
signature.SignatureTestCase.
