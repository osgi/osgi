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
configured for issuing the tests. In this distribution the `zcl.xml` file 
contains only fake clusters, because for licensing issues OSGi Alliance 
cannot disclosure any ZCL commands specification details.

The tester must replace the current `zcl.xml` file content the actual cluster 
definitions. Only the cluster identifiers listed in the inputClusters and 
outputClusters attributes of the `<simpleDescriptor>` element in file 
zigbee-ct-template.xml have to be defined.

Please note that all the attributes and commands defined in file `zcl.xml` 
are meant to be actually implemented on the ZigBee devices used for the test, 
if they are referenced by any `simpleDescriptor` element of the 
`zigbee-ct-template.xml` file.

As it happens with the other xml file a  attention has to be payed in filling the 
`zcl.xml` file because any discrepancy between the file content and the ZigBee 
devices  let the CT to fail. 

Here some constraints on the `command` xml element:

* The `id` attribute is mandatory and represents the command identifier.

* `manufacturerCode` is the command manufacturer code ([0, 0xffff]. If missing the
   command is considered not manufacturer specific. 
 
* `isClusterSpecificCommand` is an optional boolean attribute that states if the command
  is cluster specific (`true`) or general (`false`). Its value defaults to "true".
  
* `response_id` is a mandatory attribute that contains the identifier of the command
  that the cluster uses to answer to the current one. Please note that if the response
  command is the Default Response general command, the tester **must** define in the
  cluster (under the client side) a command with the id corresponding to the Default Response
  command and a `isClusterSpecificCommand` attribute set to "false". 

* The `zclFrame` (that is a raw frame) attribute. Contains an even size hex digits 
  string. The first couple of digits represents the frame control field of the ZCL frame.
  This attribute must contain an example of this command. The CT will try to 
  create a ZCLFrame from this raw frame and send it to the real ZigBee device. 
  It is important that this raw frame is consistent with the other attributes
  defined for the `command` element (see above). If the `command` element contains
  also a `response_id`, the response to this raw frame will be compared with 
  the raw frame (that is the `zclFrame` defined for that response command (only the
  sequence number is not taken into account in this comparison.


# How the CT performs the tests

The compliant tests bundle starts by loading the 
`zigbee-ct-template.xml` and `zcl.xml` files. If these files do not
contain all the minimum set of information required to perform 
the tests the CT exit with a failure.

This is the list of additional constraints that **MUST** be satisfied (some of them are 
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
* Each cluster element in zcl.xml file must define at least one server side 
  command.
* The CT will call the ZCLCluster.invoke() method with the raw frame specified 
  by the zclFrame attribute of a command definition in the zcl.xml file. 
  The returned ZCLFrame.getBytes() byte array, will be then compared with the raw 
  frame specified in the xml file for the response command (if response_id is specified).
  If response_id is not specified, the CT will assume that the response **must** be
  the Default Response.
   
  
If the nodes have a UserDescriptor, the CT will change it.

The simplest ZigBee device that could be used to test the implementation is
a ZigBee node implementing the ZigBee Cluster Libraty Basic, OnOff and Identify 
clusters.

# Constraints on the ZBI for being testable with the CT.

Because of ZigBee licensing issues, the ZCLFrame implementation 
provided with the RI and the CT is not complete. In particular,
this implementation, cannot correctly marshal and unmarshal a `ZCLHeader`.

The CT works only with the presence of just one ZigBeeHost service.







