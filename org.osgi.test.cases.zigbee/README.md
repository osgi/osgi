# Introduction 

This README.md file contains information about how to 
prepare the test setup needed to run the CT against an implementation
of the ZigBee OSGi specification. In the following this implementation
will be called ZigBee Implementation (ZBI).

Before starting the CT the tester must:
 * Create a ZigBee network and configure a set of ZigBee end devices 
   (one ZED is enough).
 * Fill the `zcl.xml` and the `zigbee-ct-template.xml` files with some 
   relevant information about the ZigBee host and the ZigBee End Devices 
   part of the ZigBee network.
	  
	  
# How to edit the `zcl.xml` and `zigbee-ct-template.xml` files

The `zigbee-ct-template.xml` file must contain a formal representation of
the ZigBee Host, the ZigBee Nodes, ZigBee Endpoints and Clusters of
the ZigBee devices used during the test.

A schema file (`zigbee-ct.xsd`) is also provided and contains the elements
definitions and documentation. In addition to the schema file it worth to
know about some semantic-related information:

* The `discoveryTimeout` attribute in `<host>` element is used as a maximum time 
  (expressed in milliseconds) the CT waits for a ZigBeeNode or a 
  ZigBeeEndppoint service during the discovery tests. The `discoveryTimeout` 
  value is implementation dependent.
  
* The `invokeTimeout` attribute in <host> element is used as a maximum time 
  (expressed in milliseconds) the CT waits for the resolution of any API method
  that is returning a Promise object. It is also the timeout used for receiving any
  failure in callback ZigBeeListener.onFailure(). The `invokeTimeout` amount is 
  implementation dependent.
  
* Any xml attribute named ieeeAddress is the base 10 representation of an
  IEEE address.
  
* Any of the 'id' fields must contain a base 10 representation of the
  id they represent.
  
* It is necessary to define a <node> element for each ZigBee device that 
  is part of the ZigBee network.

* The <`simpleDescriptor>` element inside the <node> element must contain a **partial** 
  list of the actual input and output clusters identifiers 
  available in the actual ZigBee devices used during the test. 
  The cluster whose id is listed in the `inputCluster` and `outputClusters`
  attributes **must** be defined to in the `zcl.xml` file 
  (see below), otherwise the CT will fail to start.
  Since the list of input and output clusters may be a subset of the actual
  ZigBee device list, the `outputClustersNumber` and `inputClustersNumber`, must
  contain the actual number of them available the ZigBee device endpoint.
	  
* The `<endpoints>` element must contain at least one `<endpoint>` element and it is not
  required to contain the definition of all the active endpoints available on
  the ZigBee device. In any case it is mandatory to specify their number in
  the activeEndpointsNumber attribute. the CT will perform a check.
  
The `zcl.xml` allows to define the clusters used inside the ZigBee devices 
configured for issuing the tests. In this distribution the zcl.xml file 
contains only fake clusters, because for licensing issues OSGi Alliance 
cannot disclosure the ZCL specification details.

The tester must replace the current `zcl.xml` file content the actual cluster 
definitions. Only the cluster identifiers listed in the inputClusters and 
outputClusters attributes of the <simpleDescriptor> element in file 
zigbee-ct-template.xml have to be defined.

Please note that all the attributes and commands defined in the zcl.xml 
must be implemented.

# How the CT perform the tests

The compliant tests bundle starts by loading the 
`zigbee-ct-template.xml` and `zcl.xml` files. If these files do not
contain all the minimum set of information required to perform 
the tests the CT exit with a failure.

This is the list of constraint that MUST be satisfied (some of them are 
also formalized in the schema files:

* At least one <node> element have to be defined.
* The `<node>` or `<host>` IEEE addresses must differs.
* Throughout the xml file there must be at least an <endpoint> that 
  defines the unknownClusterId attribute. This attribute must contain 
  the identifier of a cluster that do not exist in the endpoint neither
  as an input cluster nore as an output cluster.
* Every cluster identifier listed in the inputClusters or outputClusters list
  of the <simpleDescriptor> element must be defined in the zcl.xml file
* Throughout the xml file there must be at least one endpoint with a cluster
  defining a read-only attribute of ZCL type Boolean.
* Throughout the xml file there must be at least one endpoint with a cluster
  defining a reportable attribute of ZCL type Boolean.
* Throughout the xml file there must be at least one endpoint with a cluster
  defining a writable attribute of ZCL type Boolean.  
* Throughout the xml file there must be at least one endpoint with a server 
  cluster having a command and the respective response defined (if the response
  is not the default one).
  
If the nodes have a UserDescriptor, the CT will change it.

The simplest ZigBee device that could be used to test the implementation is
a ZigBee node implementing the ZigBee Cluster Libraty Basic, OnOff and Identify 
clusters.

# Constraints on the ZBI for being testable with the CT.

Because of ZigBee licensing issues, the ZCLFrame implementation 
provided with the RI and the CT is not complete. In particular
this implementation cannot marshal and unmarshal a `ZCLHeader`.

For this reason ZBI in order to be tested with the OSGi CT must
use the `ZCLFrame.getBytes()` method to retrieve the full ZigBee Frame
when the `ZCLFrame` is created by the CT.

The CT works only with just one ZigBeeHost service registered.


















