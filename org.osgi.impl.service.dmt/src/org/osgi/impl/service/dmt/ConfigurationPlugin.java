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

// TODO handle mime types of data?
// TODO handle all keys case insensitively
// TODO handle temporary deletion/addition of intermediate entries in array/vector
// TODO vector can store mixed types in Configuration Admin
// TODO take away canReplace for 'type', 'cardinality', and maybe 'value' interior node
// TODO put valid complex type information in a separate class (and maybe valid cardinality info as well)
// TODO synchronization
// TODO put Service and Property into separate java files
// TODO maybe put configuration plugin into separate package
// Nasty things can happen (?) if the ConfigurationAdmin service returns data types not in the spec.
public class ConfigurationPlugin implements DmtDataPlugin {
    // Strings that are valid values of the 'cardinality' leaf node
    private static final String[] validCardinalityStrings = new String[] {
            "scalar", "array", "vector" };
    
    // Same as validCardinalityStrings, but each string is encapsulated in a DmtData.
    // Used in the 'valid values' field of the meta-node.
    private static final DmtData[]	validCardinalityData;
    
    // Array of type classes that can be used as complex data, in arrays and in vectors
    // Primitive type classes must come immediately after their wrapper classes.
    private static final Class[] validComplexTypeClasses = new Class[] {
            String.class, Byte.class, Boolean.class, Boolean.TYPE,
            Character.class, Character.TYPE, Short.class, Short.TYPE,
            Integer.class, Integer.TYPE, Long.class, Long.TYPE, Float.class,
            Float.TYPE, Double.class, Double.TYPE };
    
    // Same as validComplexTypeClasses, but contains the names of the classes, without the package name.
    // Used for checking the type string when a new complex configuration property is created through the plugin.
    private static final String[] validComplexTypeStrings;
    
    // Same as validComplexTypeStrings, but each string is encapsulated in a DmtData, and a "null"
    // string is added for zero-length vectors. Used in the 'valid values' field of the meta-node.
    private static final DmtData[] validComplexTypeData;
    
    static {
		validComplexTypeStrings = new String[validComplexTypeClasses.length];
		validComplexTypeData = new DmtData[validComplexTypeClasses.length + 1];
		for (int i = 0; i < validComplexTypeClasses.length; i++) {
			String fqn = validComplexTypeClasses[i].getName();
			validComplexTypeStrings[i] = fqn
					.substring(fqn.lastIndexOf('.') + 1);
			validComplexTypeData[i] = new DmtData(validComplexTypeStrings[i]);
		}
		validComplexTypeData[validComplexTypeClasses.length] = new DmtData("null");
		Arrays.sort(validCardinalityStrings);
		validCardinalityData = new DmtData[validCardinalityStrings.length];
		for (int i = 0; i < validCardinalityStrings.length; i++)
			validCardinalityData[i] = new DmtData(validCardinalityStrings[i]);
	}

	ConfigurationPlugin(BundleContext bc, ConfigurationAdmin configAdmin) {
		Service.init(bc, configAdmin);
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
		Service service = Service.getService(path[0], nodeUri);
		if (path.length == 1) // ./OSGi/cfg/<service_pid>
			return new DmtMetaNodeImpl(
					"Root node for the configuration of a given service PID.",
					true, false);
		Property prop = service.getProperty(path[1], nodeUri);
		if (path.length == 2) { // ./OSGi/cfg/<service_pid>/<key>
			if (!prop.isSimple())
				return new DmtMetaNodeImpl(
						"Root node for complex configuration data.", true,
						false);
			return new DmtMetaNodeImpl("Simple configuration data.", true,
					null, prop.getValueFormat());
		}
		if (prop.isSimple())
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"The given key '" + path[1] +
			        "' specifies simple configuration data, it has no subnodes.");
		if (path.length == 3) { // ./OSGi/cfg/<service_pid>/<key>/(type|cardinality|value)
			if (path[2].equals("type")) {
				if (!prop.hasType())
					throw new DmtException(nodeUri,
							DmtException.NODE_NOT_FOUND,
							"The type has not been set for the specified configuration item.");
				return new DmtMetaNodeImpl("Data type of configuration value.",
						false, validComplexTypeData, DmtData.FORMAT_STRING);
			}
			else
				if (path[2].equals("cardinality")) {
					if (!prop.hasCardinality())
						throw new DmtException(nodeUri,
								DmtException.NODE_NOT_FOUND,
								"The cardinality has not been set for the specified configuration item.");
					return new DmtMetaNodeImpl(
							"Cardinality of the configuration value.", false,
							validCardinalityData, DmtData.FORMAT_STRING);
					// path[2].equals("value")
				}
				else
					if (prop.isCompleteComplexScalar())
						return new DmtMetaNodeImpl(
								"Complex scalar configuration data.", false,
								null, DmtData.FORMAT_STRING);
					else
						if (prop.isCompleteNonScalar()) // array or vector
							return new DmtMetaNodeImpl(
									"Root node for elements of a non-scalar configuration item.",
									false, false);
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"'value' node not set for the specified configuration item.");
		}
		if (!prop.isCompleteNonScalar())
			throw new DmtException(
					nodeUri,
					DmtException.NODE_NOT_FOUND,
					"The given key '"
							+ path[1]
							+ "' specifies scalar data or 'value' node not set.");
		// ./OSGi/cfg/<service_pid>/<key>/value/<index>
		// prop is non-scalar, path[3] is a number string, so this can be called
		if (prop.getIndex(path[3]) < 0)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Value index out of range.");
		// format is not -1 because prop is non-scalar and cannot be empty
		// vector because of check in getIndex
		int format = prop.getValueFormat();
		return new DmtMetaNodeImpl(
				"Data element for a non-scalar configuration item.", true,
				null, format);
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
		// TODO
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property not supported.");
	}

	public void setNodeValue(String nodeUri, DmtData value) throws DmtException {
		String[] path = prepareUri(nodeUri);
		// path has at least two components because DmtAdmin only calls this
		// method for leaf nodes
		Service service = Service.getService(path[0], nodeUri);
		// path[1] is a key name
		Property prop = service.getProperty(path[1], nodeUri);
		if (path.length == 2) {
			if (!prop.isSimple()) // should not happen because of isLeafNode
				throw new DmtException(nodeUri,
						DmtException.COMMAND_NOT_ALLOWED,
						"The specified URI points to an interior node because the key '"
								+ path[1]
								+ "' references complex configuration data.");
			Object data = getSimpleData(value, nodeUri);
			service.addProperty(new Property(path[1], data, false), true,
					nodeUri);
			return;
		}
		if (prop.isSimple())
			throw new DmtException(
					nodeUri,
					DmtException.NODE_NOT_FOUND,
					"The given key '"
							+ path[1]
							+ "' specifies simple configuration data, it has no subnodes.");
		if (path.length == 3) {
			if (!prop.isCompleteComplexScalar())
				throw new DmtException(nodeUri,
						DmtException.COMMAND_NOT_ALLOWED,
						"Cannot update node values for incomplete or non-scalar properties.");
			if (!path[2].equals("value"))
				throw new DmtException(nodeUri,
						DmtException.COMMAND_NOT_ALLOWED,
						"Only the 'value' leaf node can be updated for complex scalar properties.");
			prop.setValue(value.getString(), service, nodeUri);
			return;
		}
		// path.length == 4
		if (!prop.isCompleteNonScalar())
			throw new DmtException(
					nodeUri,
					DmtException.NODE_NOT_FOUND,
					"The given key '"
							+ path[1]
							+ "' specifies scalar data or 'value' node not set.");
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
		if (prop.isSimple())
			throw new DmtException(
					nodeUri,
					DmtException.NODE_NOT_FOUND,
					"The given key '"
							+ path[1]
							+ "' specifies simple configuration data, it has no subnodes.");
		if (path.length == 3)
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Descendents of key nodes cannot be deleted, except for array/vector members.");
		// path.length == 4
		if (!prop.isCompleteNonScalar())
			throw new DmtException(
					nodeUri,
					DmtException.NODE_NOT_FOUND,
					"The given key '"
							+ path[1]
							+ "' specifies scalar data or 'value' node not set.");
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
		if (path.length == 2) { // create new complex property
			Property prop = new Property(path[1]);
			service.addProperty(prop, false, nodeUri);
			return;
		}
		Property prop = service.getProperty(path[1], nodeUri);
		if (path.length == 3) { // create 'value' node for non-scalar properties
			if (!path[2].equals("value"))
				throw new DmtException(nodeUri,
						DmtException.COMMAND_NOT_ALLOWED,
						"Only the 'value' interior node is allowed in the configuration data tree.");
			if (!prop.isNonScalar())
				throw new DmtException(
						nodeUri,
						DmtException.COMMAND_NOT_ALLOWED,
						"The 'cardinality' node must be set "
								+ "to a non-scalar cardinality before the 'value' interior node can be created.");
			if (prop.isCompleteNonScalar())
				throw new DmtException(nodeUri,
						DmtException.NODE_ALREADY_EXISTS,
						"The 'value' node already exists for the specified configuration property.");
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
		// path has at least one component because the root node always exists,
		// so this method is not called
		if (path.length == 1)
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"The configuration root node cannot have leaf node children.");
		Service service = Service.getService(path[0], nodeUri);
		if (path.length == 2) {
			Object data = getSimpleData(value, nodeUri);
			service.addProperty(new Property(path[1], data, false), false,
					nodeUri);
			return;
		}
		Property prop = service.getProperty(path[1], nodeUri);
		if (prop.isSimple())
			throw new DmtException(
					nodeUri,
					DmtException.NODE_NOT_FOUND,
					"The given key '"
							+ path[1]
							+ "' specifies simple configuration data, it cannot have subnodes.");
		if (path.length == 3) {
			if (value.getFormat() != DmtData.FORMAT_STRING)
				throw new DmtException(nodeUri,
						DmtException.FORMAT_NOT_SUPPORTED,
						"The specified leaf node must contain string data.");
			String data = value.getString();
			if (path[2].equals("type")) {
				if (prop.hasType())
					throw new DmtException(nodeUri,
							DmtException.NODE_ALREADY_EXISTS,
							"The 'type' node already exists for the specified configuration property.");
				int i = Arrays.asList(validComplexTypeStrings).indexOf(data);
				if (i < 0)
					throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
							"Type not supported.");
				prop.setType(validComplexTypeClasses[i]);
			}
			else
				if (path[2].equals("cardinality")) {
					if (!prop.hasType())
						throw new DmtException(nodeUri,
								DmtException.COMMAND_NOT_ALLOWED,
								"The 'type' node must be set before the 'cardinality' node can be created.");
					if (prop.hasCardinality())
						throw new DmtException(
								nodeUri,
								DmtException.NODE_ALREADY_EXISTS,
								"The 'cardinality' node "
										+ "already exists for the specified configuration property.");
					if (Arrays.binarySearch(validCardinalityStrings, data) < 0)
						throw new DmtException(nodeUri,
								DmtException.METADATA_MISMATCH,
								"Unknown cardinality string.");
					prop.setCardinality(data, nodeUri);
				}
				else
					if (path[2].equals("value")) {
						if (!prop.isComplexScalar())
							throw new DmtException(
									nodeUri,
									DmtException.COMMAND_NOT_ALLOWED,
									"The 'cardinality' node must be "
											+ "set to 'scalar' before the 'value' leaf node can be created.");
						if (prop.isCompleteComplexScalar())
							throw new DmtException(nodeUri,
									DmtException.NODE_ALREADY_EXISTS,
									"The 'value' node already exists for the specified configuration property.");
						prop.createValueNode(data, nodeUri);
					}
					else
						throw new DmtException(nodeUri,
								DmtException.COMMAND_NOT_ALLOWED,
								"Illegal node name for new leaf node.");
			return;
		}
		// path.length == 4, path[2].equals("value") because the parent interior
		// node must exist if this method is called
		if (!prop.isCompleteNonScalar())
			throw new DmtException(
					nodeUri,
					DmtException.NODE_NOT_FOUND,
					"The given key '"
							+ path[1]
							+ "' specifies scalar data or 'value' node not set.");
		int i;
		try {
			i = Integer.parseInt(path[3]);
		}
		catch (NumberFormatException e) {
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
		// TODO element is added to Boolean array/vector if the DmtData has STRING "true"/"false" in it, should be error
		prop.addElement(value.toString(), service, nodeUri);
	}

    public void createLeafNode(String nodeUri, DmtData value, String mimeType)
            throws DmtException {
        // TODO check mime type?
        createLeafNode(nodeUri, value);
    }

	public void copy(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		// TODO allow cloning pid, key and index nodes (on the same level)
	}

	public void renameNode(String nodeUri, String newName) throws DmtException {
		// TODO allow renaming? (which ones of pid, key and index can be
		// renamed?)
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
		if (prop.isSimple())
			return false;
		if (path.length == 3) { // ./OSGi/cfg/<service_pid>/<key>/(type|cardinality|value)
			if (path[2].equals("type"))
				return prop.hasType();
			if (path[2].equals("cardinality"))
				return prop.hasCardinality();
			if (path[2].equals("value"))
				return prop.hasValue();
			return false;
		}
		if (!path[2].equals("value") || !prop.isCompleteNonScalar())
			return false;
		return (prop.getIndex(path[3]) >= 0); // ./OSGi/cfg/<service_pid>/<key>/value/<index>
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
            return prop.isSimple();
        
        if (prop.isSimple())
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "The given key '" + path[1] + 
                    "' specifies simple configuration data, it has no subnodes.");
        
        if (path.length == 3) { // ./OSGi/cfg/<service_pid>/<key>/(type|cardinality|value)
            if (path[2].equals("type")) {
                if (!prop.hasType())
                    throw new DmtException(nodeUri,
                            DmtException.NODE_NOT_FOUND,
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
            // path[2].equals("value")
            if (prop.isCompleteComplexScalar())
                return true;
            if (prop.isCompleteNonScalar())
                return false;
            
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "'value' node not set for the specified configuration item.");
        }
        
        // path.length == 4
        if (!prop.isCompleteNonScalar())
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "The given key '" + path[1] +
                     "' specifies scalar data or 'value' node not set.");
        int index = prop.getIndex(path[3]);
        if (index < 0)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "Value index out of range.");

        return true; 
    }

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		String[] path = prepareUri(nodeUri);
		// path has at least two components because DmtAdmin only calls this
		// method for leaf nodes
		Service service = Service.getService(path[0], nodeUri);
		// path[1] is a key name
		Property prop = service.getProperty(path[1], nodeUri);
		if (path.length == 2) {
			if (!prop.isSimple()) // should not happen because of isLeafNode
				throw new DmtException(nodeUri,
						DmtException.COMMAND_NOT_ALLOWED,
						"The specified URI points to an interior node because the key '"
								+ path[1]
								+ "' references complex configuration data.");
			return prop.getDmtData();
		}
		if (prop.isSimple())
			throw new DmtException(
					nodeUri,
					DmtException.NODE_NOT_FOUND,
					"The given key '"
							+ path[1]
							+ "' specifies simple configuration data, it has no subnodes.");
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
			if (prop.isCompleteComplexScalar())
				return prop.getDmtData();
			if (prop.isCompleteNonScalar()) // should not happen because of
											// isLeafNode
				throw new DmtException(nodeUri,
						DmtException.COMMAND_NOT_ALLOWED,
						"The specified URI points to an interior node because the key '"
								+ path[1]
								+ "' references non-scalar configuration data.");
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"'value' node not set for the specified configuration item.");
		}
		// path.length == 4
		if (!prop.isCompleteNonScalar())
			throw new DmtException(
					nodeUri,
					DmtException.NODE_NOT_FOUND,
					"The given key '"
							+ path[1]
							+ "' specifies scalar data or 'value' node not set.");
		int index = prop.getIndex(path[3]);
		if (index < 0)
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"Value index out of range.");
		return prop.getDmtElement(index);
	}

	public String getNodeTitle(String nodeUri) throws DmtException {
		// TODO
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property not supported.");
	}

	public String getNodeType(String nodeUri) throws DmtException {
		// TODO!
		return null;
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		// TODO
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property not supported.");
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		// TODO
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property not supported.");
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		// TODO!
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
		if (prop.isSimple())
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"The specified URI points to a leaf node because the key '"
							+ path[1]
							+ "' references simple configuration data.");
		if (path.length == 2) {
			Vector v = new Vector();
			if (prop.hasType()) {
				v.add("type");
				if (prop.hasCardinality()) {
					v.add("cardinality");
					if (prop.hasValue())
						v.add("value");
				}
			}
			return (String[]) v.toArray(new String[v.size()]);
		}
		if (prop.isCompleteComplexScalar())
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"The specified URI points to a leaf node because the key '"
							+ path[1]
							+ "' references scalar configuration data.");
		if (!prop.isCompleteNonScalar())
			throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
					"'value' node not set for the specified configuration item.");
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
	static Class[] getValidComplexTypeClasses() {
		return validComplexTypeClasses;
	}

	static DmtData[] getValidComplexTypeData() {
		return validComplexTypeData;
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
}
// TODO cannot delete and create the same object in one session

class Service {
	static BundleContext		bc			= null;
	static ConfigurationAdmin	configAdmin	= null;
	static Hashtable			services	= new Hashtable();
	static Vector				deletedPids	= new Vector();
	static int					lockMode	= DmtSession.LOCK_TYPE_SHARED;

	static void init(BundleContext bc, ConfigurationAdmin configAdmin) {
		Service.bc = bc;
		Service.configAdmin = configAdmin;
	}

	static void setLockMode(int lm) {
		// this should not happen if DmtAdmin works correctly
		if (lockMode != DmtSession.LOCK_TYPE_SHARED)
			throw new IllegalStateException(
					"Plugin cannot be opened while in a non-shared transaction.");
		lockMode = lm;
	}

    static void close() throws DmtException {
        // TODO is it OK to forget all partially set properties when the session is closed?
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
				throw new DmtException(
						root + pid,
						DmtException.CONCURRENT_ACCESS,
						"Cannot delete configuration for "
								+ "given service pid, service no longer exists.");
			try {
				config.delete();
			}
			catch (IOException e) {
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
			// (currently it is created again if it was deleted and overwritten if it was created)
			// TODO (how) should the location be set?
			try {
				Configuration conf = configAdmin.getConfiguration(pid, null);
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
		// TODO consistency check?
		/*
		 * // service was found in list try { getConfigData(pid, nodeUri);
		 * if(!service.isStored()) { services.remove(pid); throw new
		 * DmtException(nodeUri, DmtException.CONCURRENT_ACCESS, "New dictionary
		 * appeared for previously non-existent service PID."); } }
		 * catch(DmtException e) { if(service.isStored()) {
		 * services.remove(pid); throw new DmtException(nodeUri,
		 * DmtException.CONCURRENT_ACCESS, "Dictionary for the given service PID
		 * is no longer accessible.", e); } }
		 */
		return service;
	}

	static String[] listServicePids(String nodeUri) throws DmtException {
		// TODO can the service PID contain characters that are not allowed in
		// DMT nodes names?
		// TODO consistency check?
		Configuration[] configs = null;
		ServiceReference[] refs = null;
		try {
			// TODO what about managed service factories?
			configs = configAdmin.listConfigurations(null);
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
				if (prop.isSimple() || prop.hasValue())
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
		// TODO can the configuration key contain characters that are not allowed in DMT nodes names?
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

	// TODO what about managed service factories?
	// TODO escape filter-special characters in service PID if necessary
	private static Configuration getConfig(String pid, String nodeUri)
			throws DmtException {
		String filter = "(service.pid=" + pid + ")";
		try {
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
}

class Property {
	// TODO remove assertions (IllegalStateException)

    // has corresponding OMA DM type
	private static final int SIMPLE                  = 1;
    
    // '<key>' interior node created
	private static final int COMPLEX_EMPTY           = 2;
    
    // '<key>/type' node set
	private static final int COMPLEX_HAS_TYPE        = 3;

    // '<key>/cardinality' node set (implies HAS_TYPE)
	private static final int COMPLEX_HAS_CARDINALITY = 4;
    
    // '<key>/value' node set (implies HAS_CARDINALITY)
	private static final int COMPLEX_HAS_VALUE       = 5;
    
	// SCALAR: simple: String, Boolean or byte[]; complex: Byte, Short, Integer,
	// Long, Float, Double, Character
	private static final int SCALAR					= 1;
	private static final int VECTOR					= 2;
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
		category = COMPLEX_HAS_VALUE;
		cardinality = SCALAR;
		base = value.getClass();
		if ((value instanceof String) || (value instanceof Boolean)
				|| (value instanceof byte[])) {
			category = SIMPLE;
		}
		else
			if (value instanceof Vector) {
				cardinality = VECTOR;
				size = ((Vector) value).size();
				if (size != 0)
					base = ((Vector) value).get(0).getClass();
				else
					base = null;
			}
			else
				if (value.getClass().isArray()) {
					cardinality = ARRAY;
					size = Array.getLength(value);
					base = base.getComponentType();
				}
		Class[] validComplexTypeClasses = ConfigurationPlugin
				.getValidComplexTypeClasses();
		if (category == COMPLEX_HAS_VALUE
				&& Arrays.asList(validComplexTypeClasses).indexOf(base) < 0)
			throw new IllegalArgumentException(
					"Property value has unknown data type.");
	}

	Property(String key) {
		this.key = key;
		value = null;
		stored = false;
		category = COMPLEX_EMPTY;
		cardinality = -1; // should be set before it is retrieved
		size = -1;
		base = null;
	}

	boolean isStored() {
		return stored;
	}

	boolean isSimple() {
		return category == SIMPLE;
	}

	boolean hasType() {
		return category == COMPLEX_HAS_TYPE
				|| category == COMPLEX_HAS_CARDINALITY
				|| category == COMPLEX_HAS_VALUE;
	}

	boolean hasCardinality() {
		return category == COMPLEX_HAS_CARDINALITY
				|| category == COMPLEX_HAS_VALUE;
	}

	boolean hasValue() {
		return category == COMPLEX_HAS_VALUE;
	}

	boolean isCompleteComplexScalar() {
		return category == COMPLEX_HAS_VALUE && cardinality == SCALAR;
	}

	boolean isCompleteNonScalar() {
		return category == COMPLEX_HAS_VALUE && cardinality != SCALAR;
	}

	boolean isCompleteArray() {
		return category == COMPLEX_HAS_VALUE && cardinality == ARRAY;
	}

	boolean isNonScalar() {
		return hasCardinality() && cardinality != SCALAR;
	}

	boolean isComplexScalar() {
		return hasCardinality() && cardinality == SCALAR;
	}

	// TODO consider throwing the NODE_NOT_FOUND exception here instead of at the caller
	int getIndex(String index) {
		if (!isCompleteNonScalar())
			throw new IllegalStateException(
					"Cannot call 'indexInRange()' for scalar property.");
		int i;
		try {
			i = Integer.parseInt(index);
		}
		catch (NumberFormatException e) {
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
		if (!isSimple() && !hasValue())
			throw new IllegalStateException(
					"Cannot call 'getValue()' for incomplete properties.");
		return value;
	}

	int getValueFormat() {
		if (!isSimple() && !hasType())
			throw new IllegalStateException(
					"Cannot call 'getValueFormat()' for properties where type is not set.");
		if (base == null)
			return -1; // zero length stored vector
		if (isCompleteComplexScalar())
			return DmtData.FORMAT_STRING;
		if (isSimple() || isCompleteNonScalar()) {
			if (base == Boolean.class)
				return DmtData.FORMAT_BOOLEAN;
			if (base == byte[].class)
				return DmtData.FORMAT_BINARY;
			return DmtData.FORMAT_STRING;
		}
		return -1; // COMPLEX_HAS_TYPE or COMPLEX_HAS_CARDINALITY (not reached at the moment)
	}

	int getSize() {
		if (!isCompleteNonScalar())
			throw new IllegalStateException(
					"Cannot call 'getSize()' for scalar property.");
		return size;
	}

	DmtData getDmtData() {
		if (!isSimple() && !isCompleteComplexScalar())
			throw new IllegalStateException(
					"Cannot call 'getDmtData()' for non-scalar or incomplete properties.");
		if (base == Boolean.class)
			return new DmtData(((Boolean) value).booleanValue());
		if (base == byte[].class)
			return new DmtData((byte[]) value);
		return new DmtData(value.toString());
	}

	DmtData getDmtType() {
		if (!hasType())
			throw new IllegalStateException(
					"Cannot call 'getDmtType()' for simple or incomplete properties");
		if (base == null) // unkown type for empty vector
			return new DmtData("null");
		int i = Arrays.asList(ConfigurationPlugin.getValidComplexTypeClasses())
				.indexOf(base);
		if (i < 0)
			throw new IllegalStateException(
					"Configuration data has invalid type '" + base.getName()
							+ "'.");
		return ConfigurationPlugin.getValidComplexTypeData()[i];
	}

	DmtData getDmtCardinality() {
		if (!hasCardinality())
			throw new IllegalStateException(
					"Cannot call 'getDmtCardinality()' for simple or incomplete properties");
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
					"Cannot call 'getDmtElement()' for scalar property.");
		Object data;
		boolean isPrimitiveBoolean = false;
		if (isCompleteArray())
			data = Array.get(value, index); // returns primitive types wrapped
											// in the corresponding Object type
		else
			// Vector
			data = ((Vector) value).get(index);
		if (data instanceof Boolean && base == Boolean.class)
			return new DmtData(((Boolean) data).booleanValue());
		return new DmtData(data.toString());
	}

	void setType(Class base) {
		if (category != COMPLEX_EMPTY)
			throw new IllegalStateException(
					"Cannot call 'setType()' for properties where the type is already set.");
		this.base = base;
		category = COMPLEX_HAS_TYPE;
	}

	// returns an error string, or null for no error.
	// TODO find a better solution for this (DmtException not nice because it needs the nodeUri)
	void setCardinality(String cardinalityStr, String nodeUri)
			throws DmtException {
		if (category != COMPLEX_HAS_TYPE)
			throw new IllegalStateException(
					"Cannot call 'setCardinality()' for properties where type is not set "
							+ "or cardinality is already set.");
		if (cardinalityStr.equals("scalar"))
			cardinality = SCALAR;
		else
			if (cardinalityStr.equals("array"))
				cardinality = ARRAY;
			else
				if (cardinalityStr.equals("vector"))
					cardinality = VECTOR;
				else
					throw new IllegalStateException(
							"Invalid cardinality string.");
		if (cardinality != ARRAY && base.isPrimitive())
			throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
					"Property has a primitive base type, so cardinality must be 'array'.");
		if (cardinality == SCALAR
				&& (base == String.class || base == Boolean.class))
			throw new DmtException(nodeUri,	DmtException.METADATA_MISMATCH,
					base.getName() + " type is represented as "
					+ "simple configuration data, cannot be created as complex scalar data.");
		category = COMPLEX_HAS_CARDINALITY;
	}

	void createValueNode() throws DmtException { // create interior node
		if (category != COMPLEX_HAS_CARDINALITY || cardinality == SCALAR)
			throw new IllegalStateException(
					"Cannot call 'createNodeValue()' if cardinality is scalar "
							+ "or if the value node already exists.");
		if (cardinality == ARRAY)
			value = Array.newInstance(base, 0);
		else
			// cardinality == VECTOR
			value = new Vector();
		size = 0;
		category = COMPLEX_HAS_VALUE;
		Service.commitIfNeeded();
	}

	// create leaf node
	void createValueNode(String data, String nodeUri) throws DmtException { 
		if (category != COMPLEX_HAS_CARDINALITY || cardinality != SCALAR)
			throw new IllegalStateException(
					"Cannot call 'createValueNode(data)' if cardinality is non-scalar "
							+ "or if the value node already exists.");
		// base is not null, because this is a scalar property
		value = createInstance(base, data, nodeUri);
		category = COMPLEX_HAS_VALUE;
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
		Object element;
		// TODO what should be done if base type is unknown? (element is added as string at the moment)
		if (base == null) { // zero-element vector
			element = data;
			base = String.class;
		}
		else
			element = createInstance(base, data, nodeUri);
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
		if (!isCompleteComplexScalar())
			throw new IllegalStateException(
					"Cannot call 'setValue()' for simple, incomplete or non-scalar properties.");
		// base is not null, because this is a scalar property
		value = createInstance(base, data, nodeUri);
		stored = false;
        // add this property to the non-stored list (also contains commit)
        parent.addProperty(this, true, nodeUri);
	}

	private static Object createInstance(Class type, String data, String nodeUri)
			throws DmtException {
		if (type == Character.class || type == Character.TYPE)
			return new Character(data.charAt(0));
		if (type.isPrimitive()) {
			Class[] validComplexTypeClasses = ConfigurationPlugin
					.getValidComplexTypeClasses();
			type = validComplexTypeClasses[Arrays.asList(
					validComplexTypeClasses).indexOf(type) - 1];
		}
		try {
			Constructor constructor = type
					.getConstructor(new Class[] {String.class});
			return constructor.newInstance(new Object[] {data});
		}
		catch (InvocationTargetException e) {
			throw new DmtException(nodeUri, DmtException.METADATA_MISMATCH,
					"Cannot create value object " + "of type '"
							+ type.getName()
							+ "' from the specifed value string.", e);
		}
		catch (Exception e) {
			throw new DmtException(
					null,
					DmtException.OTHER_ERROR,
					"Internal error, cannot create complex scalar value object.",
					e);
		}
	}
}
