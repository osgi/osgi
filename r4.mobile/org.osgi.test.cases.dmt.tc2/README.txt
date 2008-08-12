# Scope
This is a short introduction to describe how DMT test suit is organized 
for a better testing performance and Specification coverage. It specially 
defines the approach used to test the failing cases of Dmt Exceptions.

# Main and Plugins
In the main test suit project are the tests of the most important assertions 
of DMT API, and in the plugins test suit project are the tests of Dmt Admin 
and Plugins services contracts.

# Dmt Exceptions
Since all Dmt Exceptions for DmtSession methods have in general the same 
failing cases conditions. This test suit has clustered these tests inside 
org.osgi.test.cases.dmt.main.tb1.DmtSession.TestExceptions.

All the DmtException test cases are organized in the following way:

1) org.osgi.test.cases.dmt.main.tb1.DmtSession.TestExceptions
1.1) DmtException.INVALID_URI for all methods that have only one nodeUri. 
DmtSession.copy and DmtSession.renameNode can throw this exception on the second 
parameter as well, so, these two cases are tested in 
org.osgi.test.cases.dmt.main.tb1.DmtSession.Copy and 
org.osgi.test.cases.dmt.main.tb1.DmtSession.RenameNode classes)

1.2) DmtException.URI_TOO_LONG (if the DmtAdmin service implementation 
throws this exception - this optional feature is set at 
org.osgi.test.cases.dmt.main.tbc.DmtConstants). They can throw this exception 
on the second parameter of DmtSession.copy and DmtSession.renameNode as well, 
so they are also tested in org.osgi.test.cases.dmt.main.tb1.DmtSession.Copy 
and org.osgi.test.cases.dmt.main.tb1.DmtSession.RenameNode)

1.3) DmtException.PERMISSION_DENIED (there are some specific cases in 
DmtSession.copy method. They are tested in 
org.osgi.test.cases.dmt.main.tb1.DmtSession.Copy)

1.4) DmtException.COMAND_FAILED when the URI is not within the current session's 
subtree. There are some specific cases in  DmtSession.close (tested in 
org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.Close),
DmtSession.execute (tested in org.osgi.test.cases.dmt.main.tb1.DmtSession.Execute). 

1.5) DmtIllegalStateException in case of timeout or closed session. The DmtAdmin 
timeout must be defined at org.osgi.test.cases.dmt.main.tbc.DmtConstants). There 
are some methods that throws this exception if the session was created using 
LOCK_TYPE_SHARED: commit, rollback, setNodeAcl, copy, createInteriorNode, 
createLeafNode, deleteNode, renameNode, setDefaultNodeValue, setNodeValue, 
setNodeTitle, setNodeType and they are tested in 
org.osgi.test.cases.dmt.main.tb1.DmtSession.[NameOfTheMethod].

1.6) SecurityException in case of local sessions, if the caller does not have 
DmtPermission for the node.


2) org.osgi.test.cases.dmt.plugins.tbc.MetaNode.MetaData.MetaData
This class is testing all method that throws DmtException.METADATA_MISMATCH exception.


3) In general, the DmtExceptions thrown by the DmtSession methods and not listed 
above are tested in their respective class at
org.osgi.test.cases.dmt.main.tb1.DmtSession.[NameOfTheMethod]. 

The exceptions at close, rollback and commit, are also tested in 
org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.
[NameOfTheMethod].  Some of these exceptions are not tested, such as METADATA_MISMATCH
 and COMMAND_FAILED (they are implementation dependant). 

There are two DmtExceptions that are not tested at all: CONCURRENT_ACCESS, 
DATA_STORE_FAILURE because they cannot be simulated.

