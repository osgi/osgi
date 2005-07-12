package org.osgi.impl.service.deploymentadmin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtExecPlugin;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtReadOnlyDataPlugin;
import org.osgi.service.dmt.DmtSession;

public class PluginDelivered implements DmtReadOnlyDataPlugin, DmtExecPlugin {
    
	private DeploymentAdminImpl da;
	private File                store;

	PluginDelivered(DeploymentAdminImpl da) {
	    String delArea = System.getProperty(DAConstants.DELIVERED_AREA);
	    if (null == delArea)
	        delArea = "/temp";
	    store = new File(delArea);
	    if (!store.exists())
	        throw new RuntimeException("Delivered area ('" + delArea + "') does not exist. " +
                "Set the " + DAConstants.DELIVERED_AREA + " system property");
		this.da = da;		
	}

    public void open(String subtreeUri, DmtSession session) throws DmtException {
    }

    public void close() throws DmtException {
    }

    public boolean isNodeUri(String nodeUri) {
		String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (!"Delivered".equals(nodeUriArr[4]))
         	return false;
        if (l == 5)
            return true;
        if (!Arrays.asList(getFiles(nodeUri)).contains(new File(store, nodeUriArr[5])))
            return false;
        if (l == 6)
            return true;
        if (!nodeUriArr[6].equals("ID") && 
            !nodeUriArr[6].equals("Data") &&
            !nodeUriArr[6].equals("EnvType") &&
            !nodeUriArr[6].equals("Operations"))
            	return false;
        if (l == 7)
            return true;
        if (nodeUriArr[7].equals("Remove"))
            return true;
        if (nodeUriArr[7].equals("InstallAndActivate"))
            return true;
        
        return false;
    }

    public boolean isLeafNode(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (l == 5)
            return false;
        if (!Arrays.asList(getFiles(nodeUri)).contains(new File(store, nodeUriArr[5])))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
        if (l == 6)
            return false;
        if (l == 7) {
            if (nodeUriArr[6].equals("ID") || 
                nodeUriArr[6].equals("Data") ||
                nodeUriArr[6].equals("EnvType"))
                    return true;
            return false;
        }
        if (l == 8)
            return true;
        
        throw new RuntimeException("Internal error");
    }

    public DmtData getNodeValue(String nodeUri) throws DmtException {
	    String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (!Arrays.asList(getFiles(nodeUri)).contains(new File(store, nodeUriArr[5])))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
		if (l == 7) {
		    if (nodeUriArr[6].equals("ID"))
		        return null; // TODO
		    if (nodeUriArr[6].equals("Data")) {
		        File f = new File(store, nodeUriArr[5]);
		        FileInputStream is = null;
		        ByteArrayOutputStream os = new ByteArrayOutputStream();
		        try {
		            is = new FileInputStream(f);
		            byte[] data = new byte[0x1000];
		            int i = is.read(data);
		            while (-1 != i) {
		                os.write(data, 0, i);
		                i = is.read(data);
		            } 
                }
                catch (Exception e) {
                    throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "", e);
                }
                finally {
                    try {
                        if (null != is)
                            is.close();
                    }
                    catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                return new DmtData(os.toByteArray());
            }
		    if (nodeUriArr[6].equals("EnvType"))
		        return new DmtData("OSGi.R4");
		}
		if (l == 8)
		    return DmtData.NULL_VALUE;
		
		throw new RuntimeException("Internal error");
    }

    public String getNodeTitle(String nodeUri) throws DmtException {
        return null;
    }

    public String getNodeType(String nodeUri) throws DmtException {
        return null;
    }

    public int getNodeVersion(String nodeUri) throws DmtException {
        return 0;
    }

    public Date getNodeTimestamp(String nodeUri) throws DmtException {
        return null;
    }

    public int getNodeSize(String nodeUri) throws DmtException {
        return getNodeValue(nodeUri).getSize();
    }

    public String[] getChildNodeNames(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        File[] files = getFiles(nodeUri);
        if (l == 5) {
            String[] ret = new String[files.length];
            for (int i = 0; i < files.length; i++)
                ret[i] = files[i].getName();
        	return ret;
        }
        if (!Arrays.asList(getFiles(nodeUri)).contains(new File(store, nodeUriArr[5])))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
	    if (l == 6)
            return new String[] {"ID", "EnvType", "Data", "Operations"};
        if (l == 7) {
            if (nodeUriArr[6].equals("Operations"))
                return new String[] {"Remove", "InstallAndActivate"};
        }
        
        throw new RuntimeException("Internal error");
    }

    public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (l == 5)
			return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF,
					DmtMetaNode.PERMANENT, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE).orOperation(DmtMetaNode.CMD_ADD);
        if (!Arrays.asList(getFiles(nodeUri)).contains(new File(store, nodeUriArr[5])))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
        if (l == 6)
			return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF,
					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE).orOperation(DmtMetaNode.CMD_ADD);
        if (l == 7) {
            if (nodeUriArr[6].equals("ID"))
                return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING).orOperation(DmtMetaNode.CMD_REPLACE);
		    if (nodeUriArr[6].equals("Data"))
		        return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_BINARY);
		    if (nodeUriArr[6].equals("EnvType"))
		        return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING);
            if (nodeUriArr[6].equals("Operations"))
                return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF,
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_NODE);
        }
        if (l == 8) {
            return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NULL).orOperation(DmtMetaNode.CMD_EXECUTE);
        }
        
        throw new RuntimeException("Internal error");    }

    public void execute(DmtSession session, String nodeUri, String correlator, String data) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l != 8)
            throw new RuntimeException("Internal error");
        if (!Arrays.asList(getFiles(nodeUri)).contains(new File(store, nodeUriArr[5])))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");        
        if (nodeUriArr[7].equals("Remove")) {
            File f = new File(store, nodeUriArr[5]);
            f.delete();
        }
        if (nodeUriArr[7].equals("InstallAndActivate")) {
            install(nodeUri);
        }
    }
    
    private void install(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        File f = new File(store, nodeUriArr[5]);
        FileInputStream is = null;
        try {
            is = new FileInputStream(f);
            da.installDeploymentPackage(is);
        }
        catch (Exception e) {
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "", e);
        }
        finally {
            try {
                if (null != is)
                    is.close();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Private methods
    
    private File[] getFiles(String nodeUri) {
        DmtAdmin dmtA = da.getDmtAdmin();
        if (null == dmtA)
            throw new RuntimeException("DMT Admin doesn't run");
        File[] files = store.listFiles();
        ArrayList ret = new ArrayList();
        for (int i = 0; i < files.length; i++) {
            String a = files[i].getName();
            String b = dmtA.mangle(null, files[i].getName());
            if (a.equals(b) && !files[i].isDirectory())
                ret.add(files[i]);
        }
        return (File[]) ret.toArray(new File[] {});
    }


}
