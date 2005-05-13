/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.dmt;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.cm.*;
import org.osgi.service.dmt.*;
import org.osgi.util.tracker.ServiceTracker;

// TODO Boolean scalar/array/vector value is set/created even if the DmtData has STRING "true"/"false" in it, should be error
// TODO non-Boolean scalar/array/vector value can be set/created using a BOOLEAN DmtData, should be error
// TODO handle mime types of data?
// --> yes, leaf: return text/plain in metadata, and use this as default too
// --> interior: config root should return pointer to DDF, published by PKR (?)
// TODO handle all keys case insensitively
// TODO handle temporary deletion/addition of intermediate entries in array/vector
// --> yes, error at commit if the numbering is not continuous
// TODO only allow atomic writes
// TODO vector can store mixed types in Configuration Admin
// --> no, BJ will correct the CA spec
// TODO take away canReplace for 'type', 'cardinality', and maybe 'values' interior node
// --> check in spec
// TODO put valid type information in a separate class (and maybe valid cardinality info as well)
// TODO synchronization
// TODO put Service and Property into separate java files
// Nasty things can happen (?) if the ConfigurationAdmin service returns data types not in the spec.
// Changes in configuration admin during a write session are not taken into account.
public class ConfigurationPlugin implements DmtDataPlugin {
    // Strings that are valid values of the 'cardinality' leaf node
    private static final String[] validCardinalityStrings = new String[] {
            "scalar", "array", "vector" };
    
    // Same as validCardinalityStrings, but each string is encapsulated in a DmtData.
    // Used in the 'valid values' field of the meta-node.
    private static final DmtData[]	validCardinalityData;
    
    // Array of valid type classes (for scalar data/arrays/vectors)
    // Primitive type classes must come immediately after their wrapper classes.
    private static final Class[] validTypeClasses = new Class[] {
            String.class, Byte.class, Byte.TYPE, Boolean.class, Boolean.TYPE,
            Character.class, Character.TYPE, Short.class, Short.TYPE,
            Integer.class, Integer.TYPE, Long.class, Long.TYPE, Float.class,
            Float.TYPE, Double.class, Double.TYPE };
    
    // Same as validTypeClasses, but contains the names of the classes, without the package name.
    // Used for checking the type string when a new configuration property is created through the plugin.
    private static final String[] validTypeStrings;
    
    // Same as validTypeStrings, but each string is encapsulated in a DmtData, and a "null"
    // string is added for zero-length vectors. Used in the 'valid values' field of the meta-node.
    private static final DmtData[] validTypeData;
    
    static {
		validTypeStrings = new String[validTypeClasses.length];
		validTypeData = new DmtData[validTypeClasses.length + 1];
		for (int i = 0; i < validTypeClasses.length; i++) {
			String fqn = validTypeClasses[i].getName();
			validTypeStrings[i] = fqn.substring(fqn.lastIndexOf('.') + 1);
			validTypeData[i] = new DmtData(validTypeStrings[i]);
		}
		validTypeData[validTypeClasses.length] = new DmtData("null");
        
		Arrays.sort(validCardinalityStrings);
		validCardinalityData = new DmtData[validCardinalityStrings.length];
		for (int i = 0; i < validCardinalityStrings.length; i++)
			validCardinalityData[i] = new DmtData(validCardinalityStrings[i]);
	}

	ConfigurationPlugin(BundleContext bc, ServiceTracker configTracker) {
		Service.init(bc, configTracker);
	}

	//----- DmtDataPlugin methods -----//
	public void open(int lockMode, DmtSession session) throws DmtException {
		// TODO support transactions
		// DmtSession not needed because this plugin does not need to indicate alerts
		Service.setLockMode(lockMode);
	}

	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		open(lockMode, session);
	}

	public DmtMetaNode getMetaNode(String nodeUri)
			throws DmtException {
		String[] path = prepareUri(nodeUri);

        if (path.length == 0) // ./OSGi/cfg
			return new DmtMetaNodeImpl(
					"Root node of the configuration subtree.", false, true);
        
		if (path.length == 1) // ./OSGi/cfg/<service_pid>
			return new DmtMetaNodeImpl(
					"Root node for the configuration of a given service PID.",
					true, false);
        
		if (path.length == 2) // ./OSGi/cfg/<service_pid>/<key>
		    return new DmtMetaNodeImpl(
                    "Root node for a configuration entry.", true, false);
        
		if (path.length == 3) { // ./OSGi/cfg/<service_pid>/<key>/(type|cardinality|value|values)
			if (path[2].equals("type"))
				return new DmtMetaNodeImpl("Data type of configuration value.",
						false, validTypeData, DmtData.FORMAT_STRING);
			
            if (path[2].equals("cardinality"))
			    return new DmtMetaNodeImpl(
                        "Cardinality of the configuration value.", false,
			            validCardinalityData, DmtData.FORMAT_STRING);
            
            if(path[2].equals("value"))
                return new DmtMetaNodeImpl(
                        "Scalar configuration data.", false, null, 
                        DmtData.FORMAT_STRING);
            
            if(path[2].equals("values"))
                return new DmtMetaNodeImpl(
                        "Root node for elements of a non-scalar configuration item.",
                        false, false);

            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "No such node defined in the configuration tree.");
		}
        
		if(path.length == 4) { // ./OSGi/cfg/<service_pid>/<key>/values/<index>
            try {
                // TODO maybe skip int checking, it might be enough to specify name pattern in meta-data
                Integer.parseInt(path[3]); 
                if(path[2].equals("values"))
                    return new DmtMetaNodeImpl(
                            "Data element for a non-scalar configuration item.", 
                            true, null, DmtData.FORMAT_STRING);
            } catch(NumberFormatException e) {}
            
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "No such node defined in the configuration tree.");
        }
        
        throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                "No such node defined in the configuration tree.");
	}

	public boolean supportsAtomic() {
		return true;
	}

	//----- Dmt methods -----//
    public void commit() throws DmtException {
        Service.commit(true);
    }
    
	public void rollback() throws DmtException {
		Service.reset();
	}

	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property not supported.");
	}

	public void setNodeValue(String nodeUri, DmtData value) throws DmtException {
		String[] path = prepareUri(nodeUri);
		// path has at least three components because DmtAdmin only calls this
		// method for leaf nodes
		Service service = Service.getService(path[0], nodeUri);
		// path[1] is a key name
		Property prop = service.getProperty(path[1], nodeUri);
        
		if (path.length == 3) {
			if (!prop.isCompleteScalar())
				throw new DmtException(nodeUri,
						DmtException.COMMAND_NOT_ALLOWED,
						"Cannot update node values for incomplete or non-scalar properties.");
			if (!path[2].equals("value"))
				throw new DmtException(nodeUri,
						DmtException.COMMAND_NOT_ALLOWED,
						"Only the 'value' leaf node can be updated for scalar properties.");
			prop.setValue(value.getString(), service, nodeUri);
			return;
		}
        
		// path.length == 4
		if (!prop.isCompleteNonScalar())
			throw new DmtException(nodeUri,	DmtException.NODE_NOT_FOUND,
					"The given key '" + path[1]	+ "' specifies scalar data or 'values' node not created.");
        
		int index = prop.getIndex(path[3]);
		if (index < 0)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Value index out of range.");
        
		// TODO element is set in Boolean array/vector if the DmtData has STRING "true"/"false" in it, should be error
		// TODO likewise, element may be set in non-Boolean array/vector even if DmtData has a BOOLEAN value
		prop.setElement(value.toString(), index, service, nodeUri);
    }
    
    public void setDefaultNodeValue(String nodeUri) throws DmtException {
        throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH, 
                "The specified node has no default value.");
    }
    
	public void setNodeType(String nodeUri, String type) throws DmtException {
        // TODO allow for leaf nodes (see TODO at top of file)
		throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
				"Cannot set type property of configuration nodes.");
	}

	public void deleteNode(String nodeUri) throws DmtException {
		String[] path = prepareUri(nodeUri);

        // path has at least one component because the root node is permanent
        Service service = Service.getService(path[0], nodeUri);
		if (path.length == 1) {
			Service.deleteService(service, nodeUri);
			return;
		}
        
		Property prop = service.getProperty(path[1], nodeUri);
		if (path.length == 2) {
			service.deleteProperty(prop, nodeUri);
			return;
		}

        if (path.length == 3)
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Descendents of key nodes cannot be deleted, except for array/vector members.");

        // path.length == 4
		if (!prop.isCompleteNonScalar())
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"The given key '" + path[1] + "' specifies scalar data or 'values' node not created.");
        
		int index = prop.getIndex(path[3]);
		if (index < 0)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Value index out of range.");
        
		if (index != prop.getSize() - 1)
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot delete intermediate entry in array/vector.");
        
		prop.deleteElement(service, nodeUri);
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		String[] path = prepareUri(nodeUri);
		// path has at least one component because the root node always exists,
		// so this method is not called
		if (path.length == 1) {
			Service.createService(path[0], nodeUri);
			return;
		}
		Service service = Service.getService(path[0], nodeUri);
		if (path.length == 2) { // create new property
			Property prop = new Property(path[1]);
			service.addProperty(prop, false, nodeUri);
			return;
		}
		Property prop = service.getProperty(path[1], nodeUri);
		if (path.length == 3) { // create 'values' node for non-scalar properties
			if (!path[2].equals("values"))
				throw new DmtException(nodeUri,
                        DmtException.COMMAND_NOT_ALLOWED,
						"Only the 'values' interior node is allowed in the configuration data tree.");
			if (!prop.isNonScalar())
				throw new DmtException(nodeUri,
						DmtException.COMMAND_NOT_ALLOWED,
						"The 'cardinality' node must be set "
								+ "to a non-scalar cardinality before the 'values' interior node can be created.");
			if (prop.isCompleteNonScalar())
				throw new DmtException(nodeUri,
						DmtException.NODE_ALREADY_EXISTS,
						"The 'values' node already exists for the specified configuration property.");
			prop.createValueNode();
			return;
		}
		throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
				"Illegal node name for new interior node.");
	}

	public void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
				"Cannot set type property of configuration nodes.");
	}

    public void createLeafNode(String nodeUri) throws DmtException {
        throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
                "The specified node has no default value.");
    }   

	public void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
		String[] path = prepareUri(nodeUri);
        // path has at least three components because there are no leaf nodes
        // above that level

		Service service = Service.getService(path[0], nodeUri);
		Property prop = service.getProperty(path[1], nodeUri);

        if (path.length == 3) {
			String data = value.getString();
			if (path[2].equals("type")) {
				if (prop.hasType())
					throw new DmtException(nodeUri,
							DmtException.NODE_ALREADY_EXISTS,
							"The 'type' node already exists for the specified configuration property.");
				int i = Arrays.asList(validTypeStrings).indexOf(data);
				if (i < 0)
					throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
							"Type not supported.");
				prop.setType(validTypeClasses[i]);
			}
			else if (path[2].equals("cardinality")) {
			    if (!prop.hasType())
			        throw new DmtException(nodeUri,
			                DmtException.COMMAND_NOT_ALLOWED,
			                "The 'type' node must be set before the 'cardinality' node can be created.");
			    if (prop.hasCardinality())
			        throw new DmtException(nodeUri,
			                DmtException.NODE_ALREADY_EXISTS,
			                "The 'cardinality' node already exists for the specified configuration property.");
			    if (Arrays.binarySearch(validCardinalityStrings, data) < 0)
			        throw new DmtException(nodeUri,
                            DmtException.METADATA_MISMATCH,
                            "Unknown cardinality string.");
			    prop.setCardinality(data, nodeUri);
			} else if (path[2].equals("value")) {
			    if (!prop.isScalar())
			        throw new DmtException(nodeUri,
			                DmtException.COMMAND_NOT_ALLOWED,
			                "The 'cardinality' node must be 'scalar' for the 'value' leaf node to be valid.");
			    if (prop.isCompleteScalar())
			        throw new DmtException(nodeUri,
			                DmtException.NODE_ALREADY_EXISTS,
			                "The 'value' node already exists for the specified configuration property.");
			    prop.createValueNode(data, nodeUri);
			}

            return;
		}
        
		// path.length == 4, path[2].equals("values") because the parent interior
		// node must exist if this method is called
		if (!prop.isCompleteNonScalar())
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"The given key '" + path[1]	+ "' specifies scalar data or 'values' node not created.");
        
		int i;
		try {
			i = Integer.parseInt(path[3]);
		} catch (NumberFormatException e) {
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Invalid URI, last segment not parseable as an integer.", e);
		}
        
		int size = prop.getSize();
		if (i < 0 || i > size)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Value index out of range.");
		if (i < size)
			throw new DmtException(nodeUri, DmtException.NODE_ALREADY_EXISTS,
					"The specified index is already set.");
        
		prop.addElement(value.toString(), service, nodeUri);
	}

    public void createLeafNode(String nodeUri, DmtData value, String mimeType)
            throws DmtException {
        // TODO check mime type?
        // --> no need to check if only text/plain is allowed in the meta data
        createLeafNode(nodeUri, value);
    }

	public void copy(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		// TODO allow cloning pid, key (on the same level)
        throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                "Cannot copy configuration nodes.");
	}

	public void renameNode(String nodeUri, String newName) throws DmtException {
		// TODO allow renaming index, key 
	    // TODO check if pid can be renamed in configuration admin
		throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
				"Cannot rename configuration nodes.");
	}

	//----- DmtReadOnly methods -----//
	public void close() throws DmtException {
		Service.close();
	}

	public boolean isNodeUri(String nodeUri) {
		String[] path = prepareUri(nodeUri);

        if (path.length == 0) // ./OSGi/cfg
			return true;
		if (path.length > 4)
			return false;
        
		Service service;
		try {
			service = Service.getService(path[0], nodeUri);
		}
		catch (DmtException e) {
			return false;
		}
        
		if (path.length == 1) // ./OSGi/cfg/<service_pid>
			return true;
        
		Property prop;
		try {
			prop = service.getProperty(path[1], nodeUri);
		}
		catch (DmtException e) {
			return false;
		}
        
		if (path.length == 2) // ./OSGi/cfg/<service_pid>/<key>
			return true;

        if (path.length == 3) { // ./OSGi/cfg/<service_pid>/<key>/(type|cardinality|value|values)
			if (path[2].equals("type"))
				return prop.hasType();
			if (path[2].equals("cardinality"))
				return prop.hasCardinality();
			if (path[2].equals("value"))
				return prop.isCompleteScalar();
            if (path[2].equals("values"))
                return prop.isCompleteNonScalar();
			return false;
		}
        
		if (!path[2].equals("values") || !prop.isCompleteNonScalar())
			return false;
        
		return (prop.getIndex(path[3]) >= 0); // ./OSGi/cfg/<service_pid>/<key>/values/<index>
	}

    public boolean isLeafNode(String nodeUri) throws DmtException {
        String[] path = prepareUri(nodeUri);
        if (path.length == 0) // ./OSGi/cfg
            return false;

        Service service = Service.getService(path[0], nodeUri);
        if (path.length == 1) // ./OSGi/cfg/<service_pid>
            return false;

        Property prop = service.getProperty(path[1], nodeUri);
        if (path.length == 2) // ./OSGi/cfg/<service_pid>/<key>
            return false;
        
        if (path.length == 3) { // ./OSGi/cfg/<service_pid>/<key>/(type|cardinality|value|values)
            if (path[2].equals("type")) {
                if (!prop.hasType())
                    throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                            "The type has not been set for the specified configuration item.");
                return true;
            }
            if (path[2].equals("cardinality")) {
                if (!prop.hasCardinality())
                    throw new DmtException(nodeUri,
                            DmtException.NODE_NOT_FOUND,
                            "The cardinality has not been set for the specified configuration item.");
                return true;
            }
            if (path[2].equals("values")) {
                if (!prop.isCompleteNonScalar())
                    throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                        "The configuration item is scalar or the 'values' node has not been created.");
            
                return false;
            }
            
            // path[2].equals("value")
            if(prop.isCompleteScalar())
                return true;
            
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "The configuration item is not scalar or the 'value' node has not been set.");
        }
        
        // path.length == 4
        if (!prop.isCompleteNonScalar())
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "The given key '" + path[1] +
                    "' specifies scalar data or 'values' node not created.");
        int index = prop.getIndex(path[3]);
        if (index < 0)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "Value index out of range.");

        return true; 
    }

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		String[] path = prepareUri(nodeUri);
		// path has at least three components because DmtAdmin only calls this
		// method for leaf nodes
		Service service = Service.getService(path[0], nodeUri);
		// path[1] is a key name
		Property prop = service.getProperty(path[1], nodeUri);
        
		if (path.length == 3) {
			String stringData = null;
			if (path[2].equals("type")) {
				if (!prop.hasType())
					throw new DmtException(nodeUri,
							DmtException.NODE_NOT_FOUND,
							"The type has not been set for the specified configuration item.");
				return prop.getDmtType();
			}
			if (path[2].equals("cardinality")) {
				if (!prop.hasCardinality())
					throw new DmtException(nodeUri,
							DmtException.NODE_NOT_FOUND,
							"The cardinality has not been set for the specified configuration item.");
				return prop.getDmtCardinality();
			}
			// path[2].equals("value")
			if (prop.isCompleteScalar())
				return prop.getDmtData();

            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"'value' node not set for the specified configuration item.");
		}
		// path.length == 4
		if (!prop.isCompleteNonScalar())
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"The given key '" + path[1]	+ 
                    "' specifies scalar data or 'values' node not created.");
		int index = prop.getIndex(path[3]);
		if (index < 0)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Value index out of range.");
		return prop.getDmtElement(index);
	}

	public String getNodeTitle(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property not supported.");
	}

	public String getNodeType(String nodeUri) throws DmtException {
		// TODO see TODO at top of file
		return null;
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property not supported.");
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property not supported.");
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		// TODO return size for leaf nodes
		return 0;
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		String[] path = prepareUri(nodeUri);
		if (path.length == 0)
			// get service PIDs
			return Service.listServicePids(nodeUri);
		Service service = Service.getService(path[0], nodeUri);
		if (path.length == 1)
			return service.listPropertyNames(nodeUri);
		Property prop = service.getProperty(path[1], nodeUri);
		if (path.length == 2) {
			Vector v = new Vector();
			if (prop.hasType()) {
				v.add("type");
				if (prop.hasCardinality()) {
					v.add("cardinality");
					if (prop.isCompleteScalar())
						v.add("value");
                    else if (prop.isCompleteNonScalar())
                        v.add("values");
				}
			}
			return (String[]) v.toArray(new String[v.size()]);
		}
		if (prop.isCompleteScalar())
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"The specified URI points to a leaf node because the key '"
							+ path[1]
							+ "' references scalar configuration data.");
		if (!prop.isCompleteNonScalar())
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"'values' node not set for the specified configuration item.");
		if (path.length == 3)
			return prop.getIndexNames();
		// should not happen because of !isLeafNode...
		if (prop.getIndex(path[3]) < 0)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Value index out of range.");
		throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
				"The specified URI points to a leaf node.");
	}

	//----- Package private methods -----//
	static Class[] getValidTypeClasses() {
		return validTypeClasses;
	}

	static DmtData[] getValidTypeData() {
		return validTypeData;
	}

	//----- Private utility methods -----//
	private static String[] prepareUri(String nodeUri) {
		String relativeUri = Utils.relativeUri(
				ConfigurationPluginActivator.PLUGIN_ROOT, nodeUri);
		// relativeUri will not be null because the DmtAdmin only gives us nodes
		// in our subtree
		String[] path = Splitter.split(relativeUri, '/', -1);
		if (path.length == 1 && path[0].equals(""))
			return new String[] {};
		return path;
	}

    /* // TODO remove this
	private static Object getSimpleData(DmtData value, String nodeUri)
			throws DmtException {
		switch (value.getFormat()) {
			case DmtData.FORMAT_STRING :
				return value.getString();
			case DmtData.FORMAT_BOOLEAN :
				return new Boolean(value.getBoolean());
			case DmtData.FORMAT_BINARY :
				return value.getBinary();
			default :
				throw new DmtException(nodeUri,
						DmtException.FORMAT_NOT_SUPPORTED,
						"The specified leaf node must contain string, boolean or binary data.");
		}
	}
    */
}

// TODO cannot delete and create the same object in one session
class Service {
	static BundleContext  bc            = null;
	static ServiceTracker configTracker = null;
	static Hashtable      services      = new Hashtable();
	static Vector         deletedPids   = new Vector();
	static int            lockMode      = DmtSession.LOCK_TYPE_SHARED;

	static void init(BundleContext bc, ServiceTracker configTracker) {
		Service.bc = bc;
		Service.configTracker = configTracker;
	}

	static void setLockMode(int lm) {
		// this should not happen if DmtAdmin works correctly
		if (lockMode != DmtSession.LOCK_TYPE_SHARED)
			throw new IllegalStateException(
					"Plugin cannot be opened while in a non-shared transaction.");
		lockMode = lm;
	}

    static void close() throws DmtException {
        // forgetting all partially set properties when the session is closed
        try {
            commit(false);
        } finally {
            lockMode = DmtSession.LOCK_TYPE_SHARED;
            reset();
        }
    }

    /*
     * Commit pending changes to the Configuration Admin. Intermediate commit
     * leaves partially set properties in place (this is used in EXCLUSIVE
     * sessions and at intermediate commits of ATOMIC sessions), while a
     * non-intermediate commit (called when a session is closed) throws away all
     * incomplete information.
     */
	static void commit(boolean intermediate) throws DmtException {
		System.out.println("Commit (intermediate=" + intermediate
				+ ", lockMode=" + lockMode + ")");
        
		String root = ConfigurationPluginActivator.PLUGIN_ROOT + '/';
		String pid;
        // TODO handle case when deletedPids or services is not empty because of an error
        // (currently the 'close' following the 'commit' error will try to continue committing)
		Iterator i = deletedPids.iterator();
		while (i.hasNext()) {
			pid = (String) i.next();
            i.remove();
			Configuration config = getConfig(pid, root + pid);
			if (config == null)
				throw new DmtException(root + pid, 
                        DmtException.CONCURRENT_ACCESS,
						"Cannot delete configuration for given service pid, " +
                        "service no longer exists.");
			try {
				config.delete();
			} catch (IOException e) {
				throw new DmtException(root + pid,
						DmtException.DATA_STORE_FAILURE,
						"Error deleting configuration.", e);
			}
		}
        
		i = services.values().iterator();
		while (i.hasNext()) {
			Service service = (Service) i.next();
			pid = service.getPid();
			// TODO maybe check that service exists (for stored services) or that it does not exist (for new ones)
            // --> don't check, write comment
			// (currently it is created again if it was deleted and overwritten if it was created)
			// TODO set location according to RFC87
			try {
				Configuration conf = getConfigurationAdmin().getConfiguration(pid, null);
				CMDictionary properties = service.getStoredProperties(false,
						root + pid);
				CMDictionary incompleteProperties = service.addTempProperties(
						properties, true);
				if (!properties.equals(conf.getProperties()))
					conf.update(properties);
				if (incompleteProperties.size() != 0)
				    service.resetService(incompleteProperties);
                else
                    i.remove();
			}
			catch (IOException e) {
				throw new DmtException(root + pid,
						DmtException.DATA_STORE_FAILURE,
						"Error updating configuration.", e);
			}
		}
	}

    static void reset() {
        // forget everything
        services = new Hashtable();
        deletedPids = new Vector();        
    }
    
	static void commitIfNeeded() throws DmtException {
		System.out.println("Optional commit (lockMode=" + lockMode + ")");
		switch (lockMode) {
			case DmtSession.LOCK_TYPE_SHARED :
				throw new IllegalStateException(
						"Write operation requested in a shared session.");
			case DmtSession.LOCK_TYPE_EXCLUSIVE :
				commit(true);
				break;
			case DmtSession.LOCK_TYPE_ATOMIC :
				break; // do nothing
		}
	}

	static Service getService(String pid, String nodeUri) throws DmtException {
		Service service = (Service) services.get(pid);
		if (service == null) {
			if (deletedPids.contains(pid))
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"There is no configuration data for the given service PID.");
			getConfigProperties(pid, nodeUri);
			service = new Service(pid, true);
			services.put(pid, service);
			return service;
		}
		return service;
	}

	static String[] listServicePids(String nodeUri) throws DmtException {
		// TODO handle special characters in service PIDs according to RFC87
		Configuration[] configs = null;
		ServiceReference[] refs = null;
		try {
			// TODO handle managed service factories according to RFC87
			configs = getConfigurationAdmin().listConfigurations(null);
			refs = bc
					.getServiceReferences(ManagedService.class.getName(), null);
		}
		catch (IOException e) {
			throw new DmtException(nodeUri, DmtException.DATA_STORE_FAILURE,
					"Error looking up configurations.", e);
		}
		catch (InvalidSyntaxException e) {
			// never happens with null filter
		}
		Set children = new HashSet();
		if (configs != null)
			for (int i = 0; i < configs.length; i++)
				children.add(configs[i].getPid());
		if (refs != null)
			for (int i = 0; i < refs.length; i++) {
				String pid = (String) refs[i].getProperty("service.pid");
				if (pid != null) // ignore services with no service.pid
					children.add(pid);
			}
		Iterator i = services.values().iterator();
		while (i.hasNext()) {
			Service service = (Service) i.next();
			if (service.isStored()) {
				if (!children.contains(service.getPid()))
					i.remove();
			}
			else
				children.add(service.getPid());
		}
		return (String[]) children.toArray(new String[children.size()]);
	}

	static void createService(String pid, String nodeUri) throws DmtException {
		if (deletedPids.contains(pid)) // TODO this is NOT GOOD!
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot create a service node that was deleted in the same session.");
		Service service = (Service) services.get(pid);
		if (service != null && !service.isStored())
			throw new DmtException(nodeUri, DmtException.NODE_ALREADY_EXISTS,
					"Cannot create the specified node, service node already created/modified.");
		Configuration config = getConfig(pid, nodeUri);
		if (config != null)
			throw new DmtException(nodeUri, DmtException.NODE_ALREADY_EXISTS,
					"Cannot create the specified node.");
		service = new Service(pid, false);
		services.put(pid, service);
		commitIfNeeded();
	}

	static void deleteService(Service service, String nodeUri)
			throws DmtException {
		// service cannot be deleted, because Service.getService would not have
		// returned it
		if (!service.isStored()) // TODO this is NOT GOOD!
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot delete a service node that was created/modified in the same session.");
		deletedPids.add(service.getPid());
		commitIfNeeded();
	}

	final String	pid;
	final boolean	stored;
	CMDictionary	tempProperties;
	Collection		deletedPropertyNames;

	Service(String pid, boolean stored) {
		this.pid = pid;
		this.stored = stored; // true if this service is backed by the Configuration Admin
		tempProperties = new CMDictionary();
		deletedPropertyNames = new Vector();
	}

	String getPid() {
		return pid;
	}

	boolean isStored() {
		return stored;
	}

	void resetService(CMDictionary newTempProperties) {
		deletedPropertyNames = new Vector();
		tempProperties = newTempProperties;
	}

	CMDictionary getStoredProperties(boolean wrap, String nodeUri)
			throws DmtException {
		CMDictionary properties = new CMDictionary();
		if (stored) {
			Dictionary dict = getConfigProperties(pid, nodeUri);
			Enumeration e = dict.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				if (!deletedPropertyNames.contains(key)) {
					Object value = dict.get(key);
					properties.put(key, wrap ? new Property(key, value, true)
							: value);
				}
			}
		}
		return properties;
	}

	CMDictionary addTempProperties(CMDictionary properties, boolean unwrap)
			throws DmtException {
		CMDictionary incompleteProperties = new CMDictionary();
		Iterator i = tempProperties.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry e = (Map.Entry) i.next();
			Property prop = (Property) e.getValue();
			if (unwrap) {
				if (prop.hasValue())
					properties.put(e.getKey(), prop.getValue());
				else
					incompleteProperties.put(e.getKey(), prop);
			}
			else
				properties.put(e.getKey(), prop);
		}
		return incompleteProperties;
	}

	String[] listPropertyNames(String nodeUri) throws DmtException {
		// TODO handle configuration keys that contain characters that are not allowed in DMT nodes names
		CMDictionary properties = getStoredProperties(false, nodeUri);
		addTempProperties(properties, false);
		return (String[]) properties.keySet().toArray(new String[] {});
	}

	Property getProperty(String key, String nodeUri) throws DmtException {
		Property prop = getPropertyNoCheck(key, nodeUri);
		if (prop == null)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"The specified key does not exist in the configuration.");
		return prop;
	}

	void addProperty(Property property, boolean overwrite, String nodeUri)
			throws DmtException {
		String key = property.getKey();
		if (deletedPropertyNames.contains(key)) // TODO this is NOT GOOD!
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot create a property that was deleted in the same session.");
		if (!overwrite && getPropertyNoCheck(key, nodeUri) != null)
			throw new DmtException(nodeUri, DmtException.NODE_ALREADY_EXISTS,
					"The specified key already exists in the configuration.");
		tempProperties.put(key, property);
		Service.commitIfNeeded();
	}

	void deleteProperty(Property property, String nodeUri) throws DmtException {
		String key = property.getKey();
		// cannot be a deleted property, because service.getProperty would not return it
		if (!property.isStored()) // TODO this is NOT GOOD!
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot delete a property that was created/modified in the same session.");
		deletedPropertyNames.add(key);
		Service.commitIfNeeded();
	}

	private Property getPropertyNoCheck(String key, String nodeUri)
			throws DmtException {
		CMDictionary properties = getStoredProperties(true, nodeUri);
		addTempProperties(properties, false);
		return (Property) properties.get(key);
	}

	private static Dictionary getConfigProperties(String pid, String nodeUri)
			throws DmtException {
		Configuration config = getConfig(pid, nodeUri);
		if (config == null)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"There is no configuration data for the given service PID '"
							+ pid + "'.");
		Dictionary properties = config.getProperties();
		if (properties == null) {
			properties = new CMDictionary();
			properties.put("service.pid", pid);
		}
		return properties;
	}

	// TODO handle managed service factories according to RFC87
	// TODO escape filter-special characters in service PID if necessary
	private static Configuration getConfig(String pid, String nodeUri)
			throws DmtException {
		String filter = "(service.pid=" + pid + ")";
		try {
            ConfigurationAdmin configAdmin = getConfigurationAdmin();
			Configuration[] configs = configAdmin.listConfigurations(filter);
			if (configs != null)
				return configs[0];
			ServiceReference[] refs = bc.getServiceReferences(
					ManagedService.class.getName(), filter);
			return refs == null ? null : configAdmin
					.getConfiguration(pid, null);
		}
		catch (IOException e) {
			throw new DmtException(nodeUri, DmtException.DATA_STORE_FAILURE,
					"Error looking up configuration.", e);
		}
		catch (InvalidSyntaxException e) {
			throw new DmtException(nodeUri, DmtException.OTHER_ERROR,
					"The specified service PID '" + pid
							+ "' is syntactically incorrect.", e);
		}
	}
    
    private static ConfigurationAdmin getConfigurationAdmin() {
        ConfigurationAdmin configAdmin = 
            (ConfigurationAdmin) configTracker.getService();
        
        if(configAdmin == null)
            throw new MissingResourceException("Configuration Admin not found.",
                    ConfigurationAdmin.class.getName(), null);
        
        return configAdmin;
    }
}

class Property {
    // TODO handle byte[] specially (what should "type" and "cardinality" be?)
	// TODO remove assertions (IllegalStateException)

    // '<key>' interior node created
	private static final int EMPTY           = 1;
    
    // '<key>/type' node set
	private static final int HAS_TYPE        = 2;

    // '<key>/cardinality' node set (implies HAS_TYPE)
	private static final int HAS_CARDINALITY = 3;
    
    // '<key>/value(s)' node set (implies HAS_CARDINALITY)
	private static final int HAS_VALUE       = 4;
    
    // String, Boolean, Byte, Short, Integer, Long, Float, Double, Character
	private static final int SCALAR					= 1;
    
    // any uniform Vector of the scalar types above
	private static final int VECTOR					= 2;
    
    // any array of the scalar types or their corresponding primitive types
	private static final int ARRAY					= 3;
    
    String  key;
    Object  value;
    boolean stored;
    int     category;
    int     cardinality;
    int     size;
    Class   base;

	Property(String key, Object value, boolean stored) {
		this.key = key;
		this.value = value;
		this.stored = stored;
		size = -1;
		category = HAS_VALUE;
		cardinality = SCALAR;
		base = value.getClass();

        if (value instanceof Vector) {
		    cardinality = VECTOR;
		    Vector v = (Vector) value;
		    size = v.size();
		    base = null;
		    Iterator i = v.iterator();
		    while (i.hasNext()) {
		        Object element = i.next();
		        if(element != null) {
		            if(base == null)
		                base = element.getClass();
                    else if(element.getClass() != base)
                        throw new IllegalArgumentException(
                                "Property value vector contains mixed types.");
                        
                }
		    }
            // assume vector of Strings if base type cannot be determined
            if(base == null)
                base = String.class;
		} else if (value.getClass().isArray()) {
		    cardinality = ARRAY;
		    size = Array.getLength(value);
		    base = base.getComponentType();
		}
		Class[] validTypeClasses = ConfigurationPlugin.getValidTypeClasses();
		if (category == HAS_VALUE
				&& Arrays.asList(validTypeClasses).indexOf(base) < 0)
			throw new IllegalArgumentException(
					"Property value has unknown data type.");
	}

	Property(String key) {
		this.key = key;
		value = null;
		stored = false;
		category = EMPTY;
		cardinality = -1; // should be set before it is retrieved
		size = -1;
		base = null;
	}

	boolean isStored() {
		return stored;
	}

	boolean hasType() {
		return category == HAS_TYPE
				|| category == HAS_CARDINALITY
				|| category == HAS_VALUE;
	}

	boolean hasCardinality() {
		return category == HAS_CARDINALITY || category == HAS_VALUE;
	}

	boolean hasValue() {
		return category == HAS_VALUE;
	}

	boolean isCompleteScalar() {
		return category == HAS_VALUE && cardinality == SCALAR;
	}

	boolean isCompleteNonScalar() {
		return category == HAS_VALUE && cardinality != SCALAR;
	}

	boolean isCompleteArray() {
		return category == HAS_VALUE && cardinality == ARRAY;
	}

	boolean isScalar() {
		return hasCardinality() && cardinality == SCALAR;
	}

	boolean isNonScalar() {
		return hasCardinality() && cardinality != SCALAR;
	}

	// TODO consider throwing the NODE_NOT_FOUND exception here instead of at the caller
	int getIndex(String index) {
		if (!isCompleteNonScalar())
			throw new IllegalStateException(
					"Cannot call 'getIndex()' for scalar property.");
		int i;
		try {
			i = Integer.parseInt(index);
		} catch (NumberFormatException e) {
			return -2;
		}
		if (i < 0 || i >= size)
			return -1;
		return i;
	}

	String[] getIndexNames() {
		if (!isCompleteNonScalar())
			throw new IllegalStateException(
					"Cannot call 'getIndexNames()' for scalar property.");
		String[] names = new String[size];
		for (int i = 0; i < size; i++)
			names[i] = Integer.toString(i);
		return names;
	}

	String getKey() {
		return key;
	}

	Object getValue() {
		if (!hasValue())
			throw new IllegalStateException(
					"Cannot call 'getValue()' for incomplete properties.");
		return value;
	}

	int getSize() {
		if (!isCompleteNonScalar())
			throw new IllegalStateException(
					"Cannot call 'getSize()' for incomplete or scalar properties.");
		return size;
	}

	DmtData getDmtData() {
		if (!isCompleteScalar())
			throw new IllegalStateException(
					"Cannot call 'getDmtData()' for incomplete or non-scalar properties.");
        
		if (base == Boolean.class)
			return new DmtData(((Boolean) value).booleanValue());

        return new DmtData(value.toString());
	}

	DmtData getDmtType() {
		if (!hasType())
			throw new IllegalStateException(
					"Cannot call 'getDmtType()' for properties with unspecified type.");
		int i = Arrays.asList(ConfigurationPlugin.getValidTypeClasses())
				.indexOf(base);
		if (i < 0)
			throw new IllegalStateException(
                    "Configuration data has invalid type '" + base.getName()
                    + "'.");
		return ConfigurationPlugin.getValidTypeData()[i];
	}

	DmtData getDmtCardinality() {
		if (!hasCardinality())
			throw new IllegalStateException(
					"Cannot call 'getDmtCardinality()' for properties with unspecified cardinality.");
		switch (cardinality) {
			case SCALAR :
				return new DmtData("scalar");
			case ARRAY :
				return new DmtData("array");
			case VECTOR :
				return new DmtData("vector");
			default : // impossible
				throw new IllegalStateException("Invalid cardinality value '"
						+ cardinality + "'.");
		}
	}

	DmtData getDmtElement(int index) {
		if (!isCompleteNonScalar())
			throw new IllegalStateException(
					"Cannot call 'getDmtElement()' for incomplete or scalar property.");
		Object data;
		boolean isPrimitiveBoolean = false;
		if (isCompleteArray())
			data = Array.get(value, index); // returns primitive types wrapped
											// in the corresponding Object type
		else
			// Vector
			data = ((Vector) value).get(index);
        
        if(data == null)
            return DmtData.NULL_VALUE;
        
		if (data instanceof Boolean)
			return new DmtData(((Boolean) data).booleanValue());
        
		return new DmtData(data.toString());
	}

	void setType(Class base) {
		if (category != EMPTY)
			throw new IllegalStateException(
					"Cannot call 'setType()' for properties where the type is already set.");
		this.base = base;
		category = HAS_TYPE;
	}

	// TODO find a better solution for this (DmtException not nice because it needs the nodeUri)
	void setCardinality(String cardinalityStr, String nodeUri)
			throws DmtException {
		if (category != HAS_TYPE)
			throw new IllegalStateException("Cannot call 'setCardinality()' for "
			        + "properties where type is not set or cardinality is already set.");

        if (cardinalityStr.equals("scalar"))
			cardinality = SCALAR;
		else if (cardinalityStr.equals("array"))
				cardinality = ARRAY;
		else if (cardinalityStr.equals("vector"))
		    cardinality = VECTOR;
		else
		    throw new IllegalStateException("Invalid cardinality string.");
        
		if (cardinality != ARRAY && base.isPrimitive())
			throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
					"Property has a primitive base type, so cardinality must be 'array'.");

        category = HAS_CARDINALITY;
	}

	void createValueNode() throws DmtException { // create interior node
		if (category != HAS_CARDINALITY || cardinality == SCALAR)
			throw new IllegalStateException(
					"Cannot call 'createNodeValue()' if cardinality is scalar "
							+ "or if the value node already exists.");
        
		if (cardinality == ARRAY)
			value = Array.newInstance(base, 0);
		else // cardinality == VECTOR
			value = new Vector();
		size = 0;
		category = HAS_VALUE;
		Service.commitIfNeeded();
	}

	// create leaf node
	void createValueNode(String data, String nodeUri) throws DmtException { 
		if (category != HAS_CARDINALITY || cardinality != SCALAR)
			throw new IllegalStateException(
					"Cannot call 'createValueNode(data)' if cardinality is non-scalar "
							+ "or if the value node already exists.");
		// base is not null, because this is a scalar property
		value = createInstance(base, data, nodeUri);
		category = HAS_VALUE;
		Service.commitIfNeeded();
	}

	// change a value in array/vector
	void setElement(String data, int index, Service parent, String nodeUri)
			throws DmtException {
		if (!isCompleteNonScalar())
			throw new IllegalStateException(
					"Cannot call 'setElement()' for scalar properties.");
		Object element = createInstance(base, data, nodeUri);
		if (cardinality == ARRAY)
			Array.set(value, index, element);
		else
			// cardinality == VECTOR
			((Vector) value).set(index, element);
		stored = false;
		// add this property to the non-stored list (also contains commit)
		parent.addProperty(this, true, nodeUri);
	}

	// add a new data node to an array/vector
	void addElement(String data, Service parent, String nodeUri)
			throws DmtException {
		if (!isCompleteNonScalar())
			throw new IllegalStateException(
					"Cannot call 'addElement()' for scalar properties.");
		Object element = createInstance(base, data, nodeUri);
		if (cardinality == ARRAY) {
			// TODO remove this assertion
			if (size != Array.getLength(value))
				throw new IllegalStateException(
						"Internal error, array size inconsistent with stored value.");
			Object newArray = Array.newInstance(base, size + 1);
			for (int i = 0; i < size; i++)
				Array.set(newArray, i, Array.get(value, i));
			Array.set(newArray, size, element);
			value = newArray;
		}
		else
			// cardinality == VECTOR
			((Vector) value).add(element);
		size++;
		stored = false;
        // add this property to the non-stored list (also contains commit)
		parent.addProperty(this, true, nodeUri);
	}

	void deleteElement(Service parent, String nodeUri) throws DmtException {
		if (!isCompleteNonScalar())
			throw new IllegalStateException(
					"Cannot call 'deleteElement()' for scalar properties.");
		if (cardinality == ARRAY) {
			// TODO remove this assertion
			if (size != Array.getLength(value))
				throw new IllegalStateException(
						"Internal error, array size inconsistent with stored value.");
			Object newArray = Array.newInstance(base, size - 1);
			for (int i = 0; i < size - 1; i++)
				Array.set(newArray, i, Array.get(value, i));
			value = newArray;
		}
		else { // cardinality == VECTOR
			// TODO remove this assertion
			if (size != ((Vector) value).size())
				throw new IllegalStateException(
						"Internal error, vector size inconsistent with stored value.");
			((Vector) value).removeElementAt(size - 1);
		}
		size--;
		stored = false;
        // add this property to the non-stored list (also contains commit)
		parent.addProperty(this, true, nodeUri);
	}

	void setValue(String data, Service parent, String nodeUri)
			throws DmtException {
		if (!isCompleteScalar())
			throw new IllegalStateException(
					"Cannot call 'setValue()' for incomplete or non-scalar properties.");
		// base is not null, because this is a scalar property
		value = createInstance(base, data, nodeUri);
		stored = false;
        // add this property to the non-stored list (also contains commit)
        parent.addProperty(this, true, nodeUri);
	}

	private static Object createInstance(Class type, String data, String nodeUri)
			throws DmtException {
        // TODO check that 'type' does not specify a primitive type?
        if(data == null)
            return null;
        
		if (type == Character.class || type == Character.TYPE) {
            if(data.length() == 0)
                throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
                        "Cannot create character value, parameter string is empty.");
            
			return new Character(data.charAt(0));
        }
		if (type.isPrimitive()) {
			Class[] validTypeClasses = ConfigurationPlugin
					.getValidTypeClasses();
			type = validTypeClasses[Arrays.asList(
					validTypeClasses).indexOf(type) - 1];
		}
		try {
			Constructor constructor = type
					.getConstructor(new Class[] {String.class});
			return constructor.newInstance(new Object[] {data});
		}
		catch (InvocationTargetException e) {
			throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
					"Cannot create value object of type '" + type.getName()
							+ "' from the specifed value string.", e);
		}
		catch (Exception e) {
			throw new DmtException(null, DmtException.OTHER_ERROR,
					"Internal error, cannot create scalar value object.", e);
		}
	}
}
