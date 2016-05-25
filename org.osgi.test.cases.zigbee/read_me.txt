Test Cases of org.osgi.test.cases.zigbee: Explanations

Authors:
- Andre BOTTARO, Orange.
- Antonin CHAZALET, Orange.
Date (last edited): 8th September 2014.

1) Introduction

%% à completer... cf. tes slides. %%%%%%%%%%%%%%%%%%%%%%%%%%%%

--------------



Test Cases of org.osgi.test.cases.enocean: Explanations

Authors:
- Andre BOTTARO, Orange.
- Victor PERRON, Orange.
- Maïlys ROBIN, Orange.
- Antonin CHAZALET, Orange.
Date (last edited): 19th August 2014.

1) Introduction

These test cases are located in the “residential” part of https://www.osgi.org/members/git/build.git
(Use https://www.osgi.org/members/gitweb/build.git/shortlog/refs/heads/residential for web-browsing).

One project is involved:
- org.osgi.test.cases.enocean, is the main project. It contains all the tests, 
and almost all the elements they required.


The test cases can be launched via the Ant task named “test” available in the 
build.xml file located in org.osgi.test.cases.enocean.

2) org.osgi.test.cases.enocean project

The org.osgi.test.cases.enocean project contains several test cases. They are 
named:
- org.osgi.test.cases.enocean.BasesTests,
- org.osgi.test.cases.enocean.EnOceanHostTests,
- org.osgi.test.cases.enocean.EventTests,
- org.osgi.test.cases.enocean.ExportTestCase,
- org.osgi.test.cases.enocean.ManualRegistrationTestCase,
- org.osgi.test.cases.enocean.PersistencyTests,
- org.osgi.test.cases.enocean.RegistrationTests,
- and org.osgi.test.cases.enocean.SignatureTests.

BaseTests contains the following tests:
- testRPC, tests RPC sending and receiving, i.e. insert an EnOcean temperature 
sensor device, and test a RPC invocation on this device.
- testInterfaceExceptions, tests that common errors cases are properly handled 
(i.e. that the relevant exceptions are thrown).
- testUseOfDescriptions, tests that a properly set profile ID in a raw 
EnOceanMessage is enough to extract all the needed information, provided the 
necessary descriptions are known.

EnOceanHostTests is intented to contain the tests related to EnOceanHost:
- testEnOceanHostServiceAvailability, tests that at least one EnOceanHost 
service is registered when an implementation of the EnOcean specification is 
running.

EventTests:
- testSelfEventReception, tests that the test suite is able to locally send 
and receive messages.
- testEventNotification, tests event notification when passing an actual 
message to the Base Driver. This also tests the MessageSet registration since 
the Base Driver needs to know about it before firing on EventAdmin.

ExportTestCase:
- testDeviceExport, tests device exportation: registers a local device as a 
service to be exported by the base driver, checks that a chip ID has been 
created and set as a property, sends a message on the EnOcean network, checks 
that the message has been sent, received by the base driver, and checked by 
the CT through the step service.

ManualRegistrationTestCase:
- testManualDeviceRegistration, tests initial device registration from a raw 
Radio teach-in packet that doesn’t contain any profile data, and that is 
triggered through the step service. This test thus requires the end-user 
involvement. Finally, the CT checks that the device's profile has been properly 
updated.

PersistencyTests:
- testDeviceExportPersistency, tests device export persistency: registers a 
device, get its chip ID (referred to as the original chip ID), stops, and 
restarts the base driver, finally get the device’s new chip ID, and checks 
that the original chip ID, and the new one are equal.

RegistrationTests:
- testAutoDeviceRegistration, tests initial, and automatic device registration 
from a raw Radio teach-in packet that is triggered through the step service.

SignatureTests:
- testSignatures, cf. testSignatures() method from org.osgi.test.support.
signature.SignatureTestCase.
