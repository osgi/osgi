package org.osgi.meg.demo.app.dmtclient;

import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;

public class ReadWriteDataPlugin implements ReadWriteDataSession, DataPlugin {
    public static final String ROOT = "RWPlugin";
    public static final String LEAF = "Leaf";
    public static final String RAW_FORMAT_NAME = "x-rawstring";
    public static final String MIME_TYPE = "text/plain";
    
    protected static final String DEFAULT_VALUE = "String, yo!";

    private static final String[] OSGI_ROOT_PATH;
    
    
    static {
        Vector path = new Vector();
        String root = System.getProperty("info.dmtree.osgi.root");
        if(!root.startsWith("."))
            throw new IllegalStateException("Invalid root property.");
        path.add(".");
        root = root.substring(1);
        while(root.length() > 0) {
            if(!root.startsWith("/"))
                throw new IllegalStateException("Invalid root property.");
            root = root.substring(1);

            int i = 0;
            boolean escape = false;
            while(i < root.length() && (escape || root.charAt(i) != '/')) {
                escape = !escape && (root.charAt(i) == '\\');
                i++;
            }
            path.add(root.substring(0, i));
            root = root.substring(i);
        }
        OSGI_ROOT_PATH = (String[]) path.toArray(new String[path.size()]);
    }
    
    private int version = 0;
    private Date timestamp = new Date(System.currentTimeMillis());
    private String value = DEFAULT_VALUE;
    
    private String leafTitle = "String leaf";
    private String rootTitle = "Complex interior node";
    
    
    
    public TransactionalDataSession openAtomicSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        return null;
    }

    public ReadableDataSession openReadOnlySession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        return this;
    }

    public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        return this;
    }

    public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
            throws DmtException {
        throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
                "Copy operation not supported.");
    }

    public void createInteriorNode(String[] nodePath, String type)
            throws DmtException {
        throw new DmtException(nodePath, DmtException.METADATA_MISMATCH, 
            "Cannot create nodes in this MO");
    }

    public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
            throws DmtException {
        throw new DmtException(nodePath, DmtException.METADATA_MISMATCH, 
            "Cannot create nodes in this MO");
    }

    public void deleteNode(String[] nodePath) throws DmtException {
        throw new DmtException(nodePath, DmtException.METADATA_MISMATCH, 
            "Cannot delete nodes in this MO");
    }

    public void renameNode(String[] nodePath, String newName)
            throws DmtException {
        throw new DmtException(nodePath, DmtException.METADATA_MISMATCH, 
            "Cannot rename nodes in this MO");
    }

    public void setNodeTitle(String[] nodePath, String title)
            throws DmtException {
        String[] path = chopRoot(nodePath);
        if(path.length == 0)
            rootTitle = title;
        else // path.length == 1
            leafTitle = title;
        
        nodeChanged(nodePath);
    }

    public void setNodeType(String[] nodePath, String type) throws DmtException {
        String[] path = chopRoot(nodePath);
        if(path.length == 0) {
            if(type != null)
                throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
                        "Cannot set the node type for the plugin root.");
        } else { // path.length == 1
            if(!MIME_TYPE.equals(type))
                throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
                        "The given mime type is not supported by this node.");
        }
        
        // Cannot really change the type of either node
        //nodeChanged(nodePath);
    }

    public void setNodeValue(String[] nodePath, DmtData data)
            throws DmtException {
        String[] path = chopRoot(nodePath);
        
        if(data == null)
            data = getMetaNode(nodePath).getDefault();
        if(path.length == 0)
            value = (String) data.getNode();
        else // path.length == 1
            if(data.getFormat() == DmtData.FORMAT_STRING)
                value = data.getString();
            else
                value = data.getRawString();
        
        nodeChanged(nodePath);
    }

    public void nodeChanged(String[] nodePath) throws DmtException {
        // storing only one version number and timestamp for both nodes, as 
        // they represent the same value
        version++;
        timestamp = new Date(System.currentTimeMillis());
    }

    public void close() throws DmtException {}

    public String[] getChildNodeNames(String[] nodePath) throws DmtException {
        String[] path = chopRoot(nodePath);
        
        if(path.length == 0)
            return new String[] { LEAF };
        
        throw new IllegalStateException("Invalid path (too long): " + nodePath);
    }
    
    public MetaNode getMetaNode(String[] nodePath) throws DmtException {
        String[] path = chopRoot(nodePath);
        
        if(path.length == 0)
            return new MetaNode() {
                public Object getExtensionProperty(String key) {
                    throw new IllegalArgumentException(
                            "Extension property keys not supported");
                }
            
                public String[] getExtensionPropertyKeys() {
                    return null;
                }
            
                public boolean isValidName(String name) {
                    return ROOT.equals(name);
                }
            
                public String[] getValidNames() {
                    return new String[] { ROOT };
                }
            
                public boolean isValidValue(DmtData value) {
                    if(value.getFormat() != DmtData.FORMAT_NODE)
                        return false;
                    
                    return (value.getNode() instanceof String);
                }
            
                public String[] getRawFormatNames() {
                    return null;
                }
            
                public int getFormat() {
                    return DmtData.FORMAT_NODE;
                }
            
                public DmtData[] getValidValues() {
                    return null;
                }
            
                public double getMin() {
                    return Double.MIN_VALUE;
                }
            
                public double getMax() {
                    return Double.MAX_VALUE;
                }
            
                public String[] getMimeTypes() {
                    return null;
                }
            
                public DmtData getDefault() {
                    return new DmtData((Object) new String(
                            "Default Complex Value"));
                }
            
                public boolean isZeroOccurrenceAllowed() {
                    return false;
                }
            
                public int getMaxOccurrence() {
                    return 1;
                }
            
                public String getDescription() {
                    return "Interior node supporting complex values.";
                }
            
                public int getScope() {
                    return DYNAMIC;
                }
            
                public boolean isLeaf() {
                    return false;
                }
            
                public boolean can(int operation) {
                    return operation == CMD_GET || operation == CMD_REPLACE;
                }
            
            };
            
        if(!path[0].equals(LEAF))
            throw new IllegalArgumentException("Invalid path (not in plugin): " +
                    Arrays.asList(nodePath));
            
        if(path.length == 1)
            return new MetaNode() {
                public Object getExtensionProperty(String key) {
                    throw new IllegalArgumentException(
                        "Extension property keys not supported");
                }
            
                public String[] getExtensionPropertyKeys() {
                    return null;
                }
            
                public boolean isValidName(String name) {
                    return LEAF.equals(name);
                }
            
                public String[] getValidNames() {
                    return new String[] { LEAF };
                }
            
                public boolean isValidValue(DmtData value) {
                    int format = value.getFormat();
                    if(format == DmtData.FORMAT_STRING)
                        return true;
                    if(format == DmtData.FORMAT_RAW_STRING)
                        return RAW_FORMAT_NAME.equals(value.getFormatName());
                    return false;
                }
            
                public String[] getRawFormatNames() {
                    return new String[] { RAW_FORMAT_NAME };
                }
            
                public int getFormat() {
                    return DmtData.FORMAT_STRING | DmtData.FORMAT_RAW_STRING;
                }
            
                public DmtData[] getValidValues() {
                    return null;
                }
            
                public double getMin() {
                    return Double.MIN_VALUE;
                }
            
                public double getMax() {
                    return Double.MAX_VALUE;
                }
            
                public String[] getMimeTypes() {
                    return new String[] { MIME_TYPE };
                }
            
                public DmtData getDefault() {
                    return new DmtData(DEFAULT_VALUE);
                }
            
                public boolean isZeroOccurrenceAllowed() {
                    return false;
                }
            
                public int getMaxOccurrence() {
                    return 1;
                }
            
                public String getDescription() {
                    return "Leaf node containing a string";
                }
            
                public int getScope() {
                    return DYNAMIC;
                }
            
                public boolean isLeaf() {
                    return true;
                }
            
                public boolean can(int operation) {
                    return operation == CMD_GET || operation == CMD_REPLACE;
                }
            };

        throw new IllegalArgumentException("Invalid path (too long): " +
                Arrays.asList(nodePath));
    }

    public int getNodeSize(String[] nodePath) throws DmtException {
        String[] path = chopRoot(nodePath);
        if(path.length != 1)
            throw new IllegalStateException("Invalid path: " + 
                    Arrays.asList(nodePath));

        return new DmtData(value).getSize();
    }

    public Date getNodeTimestamp(String[] nodePath) throws DmtException {
        return timestamp;
    }

    public String getNodeTitle(String[] nodePath) throws DmtException {
        String[] path = chopRoot(nodePath);
        if(path.length == 0)
            return rootTitle;
        
        // path.length == 1
        return leafTitle;
    }

    public String getNodeType(String[] nodePath) throws DmtException {
        String[] path = chopRoot(nodePath);
        if(path.length == 0)
            return null;

        // path.length == 1
        return MIME_TYPE;
    }

    public boolean isNodeUri(String[] nodePath) {
        String[] path = chopRoot(nodePath);
        
        return path.length == 0 || (path.length == 1 && LEAF.equals(path[0]));
    }

    public boolean isLeafNode(String[] nodePath) throws DmtException {
        String[] path = chopRoot(nodePath);
        return path.length == 1;
    }

    public DmtData getNodeValue(String[] nodePath) throws DmtException {
        String[] path = chopRoot(nodePath);
        if(path.length == 0)
            return new DmtData((Object) new String(value));

        // path.length == 1
        return new DmtData(value);
    }

    public int getNodeVersion(String[] nodePath) throws DmtException {
        return version;
    }

    
    private String[] chopRoot(String[] nodePath) {
        if(nodePath.length <= OSGI_ROOT_PATH.length)
            throw new IllegalStateException("Invalid node path (too short): " +
                    Arrays.asList(nodePath));
        
        for(int i = 0; i < OSGI_ROOT_PATH.length; i++) {
            if(!OSGI_ROOT_PATH[i].equals(nodePath[i]))
                throw new IllegalStateException("Invalid node path (does not " +
                        "start with OSGi root): " + Arrays.asList(nodePath));
        }
        
        if(!nodePath[OSGI_ROOT_PATH.length].equals(ROOT))
            throw new IllegalStateException("Invalid node path (does not " +
                "start with plugin root): " + Arrays.asList(nodePath));
        
        String[] relativePath = 
            new String[nodePath.length - OSGI_ROOT_PATH.length - 1];
        
        for(int i = OSGI_ROOT_PATH.length+1; i < nodePath.length; i++)
            relativePath[i-OSGI_ROOT_PATH.length-1] = nodePath[i];
        
        return relativePath;
    }
}
