/*
 * copyright (c) 2004 Nokia, all rights reserved, COMPANY CONFIDENTAL
 *
 * author: Ivan Zahoranszky
 */
package org.osgi.impl.service.application;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.dmt.*;
import org.osgi.util.tracker.ServiceTracker;


public class ApplicationPlugin implements BundleActivator, DmtDataPlugIn, DmtExecPlugIn 
    {
    
    // URI constants
    static final String URI_ROOT_APP      = "./OSGi/applications";
    static final String URI_ROOT_APPINST  = "./OSGi/application_instances";
    
	private BundleContext  context;
	private Hashtable      execIds = new Hashtable();
    private ServiceTracker tracker; 

    public void start(BundleContext context) throws Exception {
        this.context = context;
        
        // registers the data and exec DMT plugin
        Dictionary dict = new Hashtable();
        dict.put("dataRootURIs", new String[] {URI_ROOT_APP, URI_ROOT_APPINST});
        dict.put("execRootURIs", new String[] {URI_ROOT_APP, URI_ROOT_APPINST});
        String[] ifs = new String[] {
            DmtDataPlugIn.class.getName(),
            DmtExecPlugIn.class.getName()
        };
        // unregistered by the OSGi framework
        context.registerService(ifs, this, dict);
        
        // start track ApplicationManager
        tracker = new ServiceTracker(context, ApplicationManager.class.getName(), null);
        tracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        // stop track ApplicationManager
        tracker.close();
    }

    private ServiceReference getApplicationDescriptorRef(String uid) {
        try {
            ServiceReference[] refs = context.getServiceReferences(
                ApplicationDescriptor.class.getName(), "(unique_id=" + uid + ")");
            if (null == refs || refs.length < 1)
                return null;
            else
                return refs[0];
        } catch (InvalidSyntaxException e) {
        	throw new RuntimeException("Internal error.");
        }
    }

	public void open(int lockMode, DmtSession session) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void open(String subtreeUri, int lockMode, DmtSession session) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public DmtMetaNode getMetaNode(String nodeUri, DmtMetaNode generic) throws DmtException {
        String[] sarr = Splitter.split(nodeUri, '/', 0);
        
        if (3 == sarr.length)
            return new ApplicationMetaNode(
                !ApplicationMetaNode.ISLEAF,
                ApplicationMetaNode.CANGET);
        
        if ("application_instances".equals(sarr[2])) {
            if (4 == sarr.length) {
                return new ApplicationMetaNode(
                    !ApplicationMetaNode.CANDELETE,
                    !ApplicationMetaNode.CANADD,
                    ApplicationMetaNode.CANGET,
                    !ApplicationMetaNode.CANREPLACE,
                    ApplicationMetaNode.CANEXECUTE,
                    !ApplicationMetaNode.ISLEAF);
            }
            else if (5 == sarr.length) {
                return new ApplicationMetaNode(
                    ApplicationMetaNode.ISLEAF,
                    ApplicationMetaNode.CANGET);
            } else {
                throw new RuntimeException("Internal error.");
            }
        }
        
        String uid = sarr[3];
        ServiceReference sref = getApplicationDescriptorRef(uid);    
        if (null == sref)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
                "Node (" + nodeUri + ") not found.");
        
        if (4 == sarr.length)
            return new ApplicationMetaNode(
                !ApplicationMetaNode.ISLEAF,
                ApplicationMetaNode.CANGET);

        if (5 == sarr.length && "launch".equals(sarr[4]))
            return new ApplicationMetaNode(
                ApplicationMetaNode.CANDELETE,
                ApplicationMetaNode.CANADD,
                ApplicationMetaNode.CANGET,
                !ApplicationMetaNode.CANREPLACE,
                !ApplicationMetaNode.CANEXECUTE,
                !ApplicationMetaNode.ISLEAF);

        if (5 == sarr.length)
            return new ApplicationMetaNode(
                ApplicationMetaNode.ISLEAF,
                ApplicationMetaNode.CANGET);
        
        if (6 == sarr.length)
            return new ApplicationMetaNode(
                ApplicationMetaNode.CANDELETE,
                ApplicationMetaNode.CANADD,
                ApplicationMetaNode.CANGET,
                !ApplicationMetaNode.CANREPLACE,
                ApplicationMetaNode.CANEXECUTE,
                !ApplicationMetaNode.ISLEAF);

        if (7 == sarr.length)
            return new ApplicationMetaNode(
                ApplicationMetaNode.ISLEAF,
                ApplicationMetaNode.CANGET);
            
        throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
            "Node (" + nodeUri + ") not found.");
	}

	public boolean supportsAtomic() {
		// TODO Auto-generated method stub
		return false;
	}

	public void rollback() throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void setNode(String nodeUri, DmtData data) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void setNodeType(String nodeUri, String type) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void deleteNode(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
        String[] sarr = Splitter.split(nodeUri, '/', 0);
        
        if (6 != sarr.length)
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                "Operation is not allowed.");
            
        String uid = sarr[3]; 
        ServiceReference sref = getApplicationDescriptorRef(uid);
        if (null == sref)
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                "Node (" + nodeUri + ") not found.");
        
        String key = new String(sarr[0] + "/" + sarr[1] + "/" + sarr[2] + "/" + 
            sarr[3] + "/" + sarr[4] + "/" + sarr[5]);
        execIds.put(key, new Hashtable());
	}

	public void createInteriorNode(String nodeUri, String type) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void createLeafNode(String nodeUri, DmtData value) throws DmtException {
        String[] sarr = Splitter.split(nodeUri, '/', 0);
        
        if (7 != sarr.length)
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, 
                "Operation is not allowed.");

        String execId = sarr[5];
        String key = new String(sarr[0] + "/" + sarr[1] + "/" + sarr[2] + "/" + 
                sarr[3] + "/" + sarr[4] + "/" + sarr[5]);
        Hashtable ht = (Hashtable) execIds.get(key);
        if (null == ht)
            throw new DmtException(nodeUri, DmtException.COMMAND_FAILED,
                "Parent node " + execId + " does not exist.");

        ht.put(sarr[6], value);
	}

	public void renameNode(String nodeUri, String newName) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void close() throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public boolean isNodeUri(String nodeUri) {
        String[] sarr = Splitter.split(nodeUri, '/', 0);
        
        if (3 == sarr.length)
        	return true;
        
        ServiceReference sref = null;
        if (4 <= sarr.length && !"application_instances".equals(sarr[2])) {
            String uid = sarr[3];
            sref = getApplicationDescriptorRef(uid);
            if (null == sref)
                return false;
        }
        
        if (4 == sarr.length && !"application_instances".equals(sarr[2]))
            return true;
        
        if (4 == sarr.length && "application_instances".equals(sarr[2])) {
            try {
				ServiceReference[] hrefs = context.getServiceReferences(ApplicationHandle.class.getName(), null);
                if (null == hrefs)
                    return false;
                for (int i = 0; i < hrefs.length; ++i) {
                    long l = Long.parseLong(sarr[3]);
                    if (l == ((Long) hrefs[i].getProperty("service.id")).longValue())
                        return true;
                }
                return false;
			} catch (InvalidSyntaxException e) {
                throw new RuntimeException("Internal error.");                
			}
        }
        
        if (5 == sarr.length && !"application_instances".equals(sarr[2])) {
            String key = sarr[4];
            if ("launch".equals(key))
            	return true;
            return -1 != Arrays.asList(sref.getPropertyKeys()).indexOf(key);
        }
        
        if (5 == sarr.length && "application_instances".equals(sarr[2])) {
            if ("state".equals(sarr[4]) || "type".equals(sarr[4]))
                return true;
            return false;
        }

        if (6 == sarr.length && !"application_instances".equals(sarr[2])) {
            String key = new String(sarr[0] + "/" + sarr[1] + "/" + sarr[2] + "/" + 
                sarr[3] + "/" + sarr[4] + "/" + sarr[5]);
            return execIds.get(key) != null;
        }
        
        if (7 == sarr.length && !"application_instances".equals(sarr[2])) {
            String key = new String(sarr[0] + "/" + sarr[1] + "/" + sarr[2] + "/" + 
                sarr[3] + "/" + sarr[4] + "/" + sarr[5]);
            String param = sarr[6];
            Object data = ((Hashtable) execIds.get(key)).get(param);
            return null != data;
        } 

        return false;
	}

	public DmtData getNodeValue(String nodeUri) throws DmtException {
        String[] sarr = Splitter.split(nodeUri, '/', 0);
        
        ServiceReference sref = null;
        if (!"application_instances".equals(sarr[2])) {
            String uid = sarr[3]; 
            sref = getApplicationDescriptorRef(uid);
            if (null == sref)
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "Node (" + nodeUri + ") not found.");
        }
        
        if (5 == sarr.length && !"application_instances".equals(sarr[2])) {
            String key = sarr[4];
            if ("localized_name".equals(key))
                return new DmtData((String) sref.getProperty(key));
            else if ("version".equals(key))
                return new DmtData((String) sref.getProperty(key));
            else if ("category".equals(key))
                return new DmtData((String) sref.getProperty(key));
            else if ("autostart".equals(key))
                return new DmtData(Boolean.getBoolean(((String) sref.getProperty(key))));
            else if ("locked".equals(key))
                return new DmtData(Boolean.getBoolean(((String) sref.getProperty(key))));
            else if ("container_id".equals(key))
                return new DmtData((String) sref.getProperty(key));
            else {
                String prop = (String) sref.getProperty(key);
                if (null == prop)
                	throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                	    "Node (" + nodeUri + ") not found.");
                return new DmtData(prop);
            }
        }
        
        if (5 == sarr.length && "application_instances".equals(sarr[2])) {
            ServiceReference[] hrefs;
			try {
				hrefs = context.getServiceReferences(ApplicationHandle.class.getName(), 
                    "(service.id=" + sarr[3] +")");
			} catch (InvalidSyntaxException e) {
                throw new RuntimeException("Internal error.");
			}
			if (null == hrefs)
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "Node (" + nodeUri + ") not found.");
            
            ApplicationHandle handle = (ApplicationHandle) context.getService(hrefs[0]);
            if ("state".equals(sarr[4]))
                return new DmtData(handle.getAppStatus());
            if ("type".equals(sarr[4]))
                return new DmtData(handle.getAppDescriptor().getUniqueID());
            context.ungetService(hrefs[0]);
        }

        if (7 == sarr.length && !"application_instances".equals(sarr[2])) {
            String key = new String(sarr[0] + "/" + sarr[1] + "/" + sarr[2] + "/" + 
                sarr[3] + "/" + sarr[4] + "/" + sarr[5]);
            String param =  sarr[6];
            DmtData data = (DmtData) ((Hashtable) execIds.get(key)).get(param); 
            if (null == data)
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "Node (" + nodeUri + ") not found.");
            else 
                return data;
        } 

        throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
            "Node (" + nodeUri + ") not found.");
	}

	public String getNodeTitle(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNodeType(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
        String[] sarr = Splitter.split(nodeUri, '/', 0);
            
		try {
            if (3 == sarr.length && !"application_instances".equals(sarr[2])) {
            	return gatherChildren();
            }
            if (3 == sarr.length && "application_instances".equals(sarr[2])) {
                ServiceReference[] hrefs;
                try {
                    hrefs = context.getServiceReferences(ApplicationHandle.class.getName(), null);
                } catch (InvalidSyntaxException e) {
                    throw new RuntimeException("Internal error.");
                }
                if (null == hrefs)
                    return new String[0];
                String[] ret = new String[hrefs.length];
                for (int i = 0; i < hrefs.length; ++i) {
                    Long l = (Long) hrefs[i].getProperty("service.id");
                    ret[i] = l.toString();
                }
                return ret;
            }
            if (4 == sarr.length && !"application_instances".equals(sarr[2])){
                String uid = sarr[3];
               	return gatherChildren(nodeUri, uid);
            } 
            if (4 == sarr.length && "application_instances".equals(sarr[2])){
                return new String[] {"state", "type"};
            } 
            if (5 == sarr.length && !"application_instances".equals(sarr[2])) {
                String[] keys = (String[]) execIds.keySet().toArray(new String[0]);
                String[] ret = new String[keys.length];
                for (int i = 0; i < keys.length; ++i)
                    ret[i] = keys[i].substring(keys[i].lastIndexOf("/") + 1, keys[i].length());
                return ret;
            } 
            if(6 == sarr.length) {
                String param = sarr[5];
                String key = new String(sarr[0] + "/" + sarr[1] + "/" + sarr[2] + "/" + 
                    sarr[3] + "/" + sarr[4] + "/" + sarr[5]);
                return (String[]) ((Hashtable) execIds.get(key)).keySet().toArray(new String[0]);
            } else {
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
                    "Node (" + nodeUri + ") not found.");
            }
		} catch (Exception e) {
			throw new DmtException(nodeUri, DmtException.OTHER_ERROR, e.getMessage(), e);
		}
	}
    
    private String getUidFromUri(String nodeUri) {
        String uid = null;
        int s = nodeUri.indexOf('/', URI_ROOT_APP.length());
        if (-1 == s)
            return uid;
        int ss = nodeUri.indexOf('/', s + 1);
        if (-1 == ss) {
            uid = nodeUri.substring(URI_ROOT_APP.length() + 1);
            if ("".equals(uid))
                return null;
        }
        else {
            uid = nodeUri.substring(s + 1, ss);
        }
        return uid;
    }

	private String[] gatherChildren() throws Exception {
        ServiceReference[] refs = context.getServiceReferences(
            ApplicationDescriptor.class.getName(), null);
        if (null == refs)
            return new String[0];
        String[] ret = new String[refs.length];
        for (int i = 0; i < refs.length; ++i)
            ret[i] = (String) refs[i].getProperty("unique_id");
        return ret;
	}

    private String[] gatherChildren(String nodeUri, String uid) throws DmtException {
		try {
            ServiceReference[] refs = context.getServiceReferences(
    		    ApplicationDescriptor.class.getName(), "(unique_id=" + uid + ")");
        	if (null == refs) 
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
                    "Node (" + nodeUri + ") not found.");
            
            String[] keys = refs[0].getPropertyKeys();
            String[] ret = new String[keys.length + 1];
            for (int i = 0; i < keys.length; ++i)
                ret[i] = keys[i];
            ret[ret.length - 1] = "launch";
            return ret;
        } catch (InvalidSyntaxException e) {
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, e.getMessage());
        }
    }

	public void clone(String nodeUri, String newNodeUri, boolean recursive) throws DmtException {
		// TODO Auto-generated method stub
	}

    public void execute(DmtSession session, String nodeUri, String data) throws DmtException {
        String[] sarr = Splitter.split(nodeUri, '/', 0);
        
        if ("application_instances".equals(sarr[2])) {
            try {
                ServiceReference[] hrefs = context.getServiceReferences(ApplicationHandle.class.getName(), 
                    "(service.id=" + sarr[3] + ")");
                if (null == hrefs)
                    throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
                        "Node (" + nodeUri + ") not found.");
                ApplicationHandle handle = (ApplicationHandle) context.getService(hrefs[0]);
                if ("STOP".equalsIgnoreCase(data))
                	handle.destroyApplication();
                if ("SUSPEND".equalsIgnoreCase(data))
                    handle.suspendApplication();
                if ("RESUME".equalsIgnoreCase(data))
                    handle.resumeApplication();
                context.ungetService(hrefs[0]);
            } catch (Exception e) {
                throw new RuntimeException("Internal error.");                
            }
        }
        
        if ( 6 != sarr.length && !(4 == sarr.length && "application_instances".equals(sarr[2])) )
            throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
                "Execution of " + nodeUri + " is not allowed.");
        
        if (6 == sarr.length) {
            String uid = sarr[3]; 
            ServiceReference sref = getApplicationDescriptorRef(uid);
            if (null == sref)
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
                    "Node (" + nodeUri + ") not found.");
            
            String key = new String(sarr[0] + "/" + sarr[1] + "/" + sarr[2] + "/" + 
                sarr[3] + "/" + sarr[4] + "/" + sarr[5]);
            Hashtable args = (Hashtable) execIds.get(key);
            if (null == args)
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, 
                    "Node (" + nodeUri + ") not found.");
                
            ApplicationDescriptor descr = (ApplicationDescriptor) context.getService(sref);
            ApplicationManager am = (ApplicationManager) tracker.getService();
            try {
    			am.launchApplication(descr, args);
    		} catch (Exception e) {
    			throw new DmtException(nodeUri, DmtException.OTHER_ERROR, 
                    e.getMessage());
    		}
            context.ungetService(sref);
        }
    }

}
