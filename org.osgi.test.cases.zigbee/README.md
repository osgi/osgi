# Introduction 

This README.md file contains information about how to 
prepare the test setup needed to run the CT against an implementation
of the Device Service Specification for ZigBee.

Before starting the CT the tester **MUST**:
 * Create a ZigBee network and configure a set of ZigBee End Devices 
   (one ZED is enough).
 * Fill the *zcl.xml* and the *zigbee-ct-template.xml* files with the relevant 
   information about the ZigBee host and the ZigBee End Devices 
   that are part of the previously configured ZigBee network.
	  
# The *zcl.xml* file

This file allows to define a subset of the clusters used inside the ZigBee device(s) 
used for the test(s). The *zcl.xml* file provided with the CT contains only 
fake clusters definitions, because for licensing issues, OSGi Alliance 
cannot disclosure any ZCL commands specification details.
 
Only the clusters referenced by the *zigbee-ct-template.xml*file (see next section) 
have to be actually described here.

This file has to be filled carefully, because any discrepancy between 
its content and the used physical ZigBee devices might cause the CT to fail. 

The *zcl.xsd* file contains the schema definition for the *zigbee.xml*.

See below some information related to the *command* xml element:

* The *id* attribute is mandatory and represents the ZCL command identifier. 
  It have to be an 8 bit hex number.

* *manufacturerCode* is the command manufacturer code ([0, 0xffff]. If missing the
   command is considered not manufacturer specific. 
 
* *isClusterSpecificCommand* is an optional boolean attribute that states 
  whether the command is cluster-specific (*true*) or general (*false*). 
  If not provided, it defaults to "true" (cluster-specific).
  
* *response_id* is a mandatory attribute that contains the command identifier 
  of the expected response. Please note that if the response command is the 
  Default Response general command, the tester **MUST** define in the
  cluster (under the *<client>* section) a command with an *id* corresponding to the Default Response
  command and an *isClusterSpecificCommand* attribute set to "false".

* The *zclFrame* attribute must contain the raw representation of the ZCL frame for this command.
  The string contains the hex representation of a byte array. The first couple of digits are
  the frame control field of the ZCL frame. The CT will try to 
  create a ZCLFrame object from this raw frame representation and send it 
  (using the ZCLCluser.invoke() method) the to the physical ZigBee device.
  Because above method returns a response ZCLFrame. The CT calls on this frame 
  the ZCLFrame.getBytes() method and compares the returned byte array with the 
  frame defined in the *zclFrame* attribute of the expected response 
  command definition. If they differs (apart the ZCL frame transaction sequence number),
  the test fails.
  
See below some information related to the *attribute* xml element:

* The *id* attribute is mandatory and represents the ZCL attribute identifier. 
  It have to be a 16 bit hex number.
  
* The *dataType* xml element attribute is mandatory and must contain the name of the ZCL
  data type of the ZCL attribute. The name is the class name defining the data type 
  in the org.osgi.service.zigbee.types package of this specification 
  
Please note that each <cluster> element in *zcl.xml* file must define at least 
one server side command.

# The *zigbee-ct-template.xml* file

The *zigbee-ct-template.xml* file must contain a formal representation of
the ZigBee Host, the ZigBee Nodes, ZigBee Endpoints and Clusters of
the real ZigBee devices used during the CT.

Its schema file (*zigbee-ct.xsd*) contains also the types definition and 
some documentation. Some additional information are also provided in the following:

* The *discoveryTimeout* attribute in *<host>* element is used as a maximum time 
  (expressed in milliseconds) the CT waits for a ZigBeeNode or a 
  ZigBeeEndppoint service during the discovery tests. The *discoveryTimeout* 
  value is implementation dependent.
  
* The *invokeTimeout* attribute in *<host>* element is used as a maximum time 
  (expressed in milliseconds) the CT waits for the resolution of any API method
  that is returning a Promise object. It is also the timeout used for receiving any
  failure in callback ZigBeeListener.onFailure(). The *invokeTimeout* value is 
  implementation dependent.
  
* Any xml attribute named *ieeeAddress* is the base 10 representation of an
  IEEE address.
  
* The 'id' fields must contain a base 10 number.
  
* It is necessary to define a *<node>* element for each ZigBee device that 
  is part of the ZigBee network. At least one *<node>* element has to be defined.

* The *<simpleDescriptor>* element inside the *<node>* element must 
  contain a **partial list** of the input and output clusters identifiers 
  available in the actual ZigBee device used for the CT. 
  A cluster whose *id* is listed in attributes *inputClusters* or *outputClusters* 
  of the simple descriptor **MUST** be also defined in the *zcl.xml* file (see below), 
  otherwise the CT will exit with a failure.
  The *outputClustersNumber* and *inputClustersNumber* attributes, must
  contain the exact number of input and output clusters available in the real
  device used for the test.
	  
* The *<endpoints>* element must contain at least one *<endpoint>* element and it is not
  required to contain the definition of all the active endpoints available on
  the ZigBee device. In any case it is mandatory to specify their number in
  the *activeEndpointsNumber* attribute. The CT will perform a check on this number.
  
* *<node>* and *<host>* elements must have different IEEE addresses.

* Throughout this file the CT must find in an inputCluster (it must not be 
  necessarily always the same) that in its definition (see zigbee.xml above) 
  contains:
  
  * A read-only attribute of ZCL type Boolean.
  * A reportable attribute of ZCL type Boolean.
  * A writable attribute of ZCL type Boolean.
  * A *<command>* element and its respective response command defined.

# Some information about how operates the CT

The CT bundle starts by loading the *zcl.xml* and the *zigbee-ct-template.xml* 
files. If these files do not contain all the minimum set of information 
required to perform the tests, the CT exits with a failure.
  
If the nodes listed in the *zigbee-ct-template.xml* file have a User Descriptor, 
it will be modified by the CT.

# ZED devices suitable for the CT

According to all the constraints described above, the simplest ZigBee device 
that could be used to test with the CT an implementation of this specification 
is a ZigBee node implementing the Basic, OnOff and Identify server clusters. 

# Constraints on the ZBI for being testable with the CT.

Because of ZigBee licensing issues, the ZCLFrame implementation 
provided with the RI and the CT is not complete. In particular,
this implementation, cannot correctly marshal and unmarshal a *ZCLHeader*.

The CT does expects to find a single ZigBeeHost service registered in the
framework (the CT do not support multiple ZigBee radios).


