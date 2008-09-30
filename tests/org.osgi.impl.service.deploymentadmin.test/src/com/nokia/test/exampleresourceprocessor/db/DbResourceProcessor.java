package com.nokia.test.exampleresourceprocessor.db;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.DeploymentSession;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.deploymentadmin.spi.ResourceProcessorException;

import com.nokia.test.db.Db;
import com.nokia.test.db.FieldDef;
import com.nokia.test.exampleresourceprocessor.db.api.DbRpTest;

public class DbResourceProcessor 
		implements ResourceProcessor, BundleActivator, Serializable, DbRpTest 
	{
    
    /*
     * The action value INSTALL indicates this session is associated with the 
     * installation of a deployment package.  
     */
    private static final int INSTALL = 0;
    
    /*
     * The action value UPDATE indicates this session is associated with the 
     * update of a deployment package.  
     */
    private static final int UPDATE = 1;

    /*
     * The action value UNINSTALL indicates this session is associated with the 
     * uninstalling of a deployment package.  
     */
    private static final int UNINSTALL = 2;

	private static final String DATAFILE = "dps.data";   
    
    // refrence to the database service
    private transient Db				db;
    
    private static class DpRec implements Serializable {
        public String name;
        public String ver;
        
        public DpRec(String name, String ver) {
            this.name = name;
            this.ver = ver;
        }
        
        public boolean equals(Object obj) {
            if (null == obj)
                return false;
            if (!(obj instanceof DpRec))
                return false;
            DpRec other = (DpRec) obj;
            return name.equals(other.name);
        }
        
        public int hashCode() {
            return name.hashCode();
        }
        
        public String toString() {
            return name + " " + ver;
        }
    }
    
    // current Deployment Package and operation
    private transient DpRec             actDp;
    
    // current session
    private transient DeploymentSession session;
    
    // It contains the following hierarchy (dp is the key).
    // A artifact is a created table.
    //
    // dp_1 ----+---- res_1 ----+---- artifact_1
    //          |               +---- artifact_2
    //          |               +---- artifact_3
    //          |
    //          +---- res_2 ----+---- artifact_1
    //                          +---- artifact_4
    //
    // dp_2 ----+---- res_1 ----+---- artifact_1
    //          |               +---- artifact_8
    //          |
    //          +---- res_3 ----+---- artifact_1
    //                          +---- artifact_9
    private Hashtable		  	dps = new Hashtable();
    
    private transient Object                dbSession;
    private transient ByteArrayOutputStream copy;
    private transient String 				pid;
    private transient BundleContext			context;
    private transient File                  bundlePrivateArea;
    
    private void save(OutputStream out) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(dps);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void load(InputStream in) {
    	try {
            ObjectInputStream ois = new ObjectInputStream(in);
            dps = (Hashtable) ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void saveToStore() throws FileNotFoundException {
    	File f = context.getDataFile(DATAFILE);
    	save(new FileOutputStream(f));
    }
    
    private void loadFromStore() throws FileNotFoundException {
    	File f = context.getDataFile(DATAFILE);
    	if (!f.exists())
    		return;
    	load(new FileInputStream(f));
    }

    /*
     * Artifact means table creation in case of this Resource Processor
     */
    private void putArtifact(String resName, String tableName) {
        Hashtable ht = (Hashtable) dps.get(actDp);
        if (null == ht) {
            ht = new Hashtable();
            dps.put(actDp, ht);
        }
        
        Set s = (Set) ht.get(resName);
        if (null == s) { 
            s = new HashSet();
            ht.put(resName, s);
        }
        s.add(tableName);
    }
    
    public void begin(DeploymentSession session) {
        this.session = session;
        if (INSTALL == getDeploymentAction())
            this.actDp = new DpRec(session.getSourceDeploymentPackage().getName(),
                    session.getSourceDeploymentPackage().getVersion().toString());
        else if (UPDATE == getDeploymentAction())
            this.actDp = new DpRec(session.getTargetDeploymentPackage().getName(),
                    session.getTargetDeploymentPackage().getVersion().toString());
        else
            this.actDp = new DpRec(session.getTargetDeploymentPackage().getName(),
                    session.getTargetDeploymentPackage().getVersion().toString());
        dbSession = db.begin();
        
        copy = new ByteArrayOutputStream();
        save(copy);
    }

    private int getDeploymentAction() {
        Version verZero = new Version(0, 0, 0);
        Version verSrc = session.getSourceDeploymentPackage().getVersion();
        Version verTarget = session.getTargetDeploymentPackage().getVersion();
        if (!verZero.equals(verSrc) && !verZero.equals(verTarget))
            return UPDATE;
        if (verZero.equals(verTarget))
            return INSTALL;
        return UNINSTALL;
    }

    public void process(String resName, InputStream stream) throws ResourceProcessorException {
        deleteTables(resName);
        
        try {
	        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
	        String line = br.readLine();
	        while (null != line) {
	            if (line.startsWith("TABLE")) {
	                String[] parts = Splitter.split(line, ' ', 0);
	                String tableName = parts[1]; 

	                String[] tables = db.tableNames(dbSession);
	                if (null != tables) {
	                    for (int i = 0; i < tables.length; i++) {
                            if (tables[i].equals(tableName))
                                throw new ResourceProcessorException(
                                		ResourceProcessorException.CODE_RESOURCE_SHARING_VIOLATION, 
                                        "Table '" + tableName + "' alrady exists");
                        }
	                }
	                
	                FieldDef[] fieldDefs = getFieldDefs(parts);
	                db.createTable(dbSession, tableName, fieldDefs);
	                putArtifact(resName, tableName);
	            } else if (line.startsWith("INSERT")) {
	                String[] parts = Splitter.split(line, ' ', 0);
	                String tableName = parts[1];
	                Object[] row = getRow(db.getFieldDefs(dbSession, tableName), parts[2]);
	                db.insertRow(dbSession, tableName, row);
	            } else if (line.startsWith("DELETE")) {
	                String[] parts = Splitter.split(line, ' ', 0);
	                String tableName = parts[1];
	                FieldDef[] fieldDefs = db.getFieldDefs(dbSession, tableName);
	                int keyInd = FieldDef.indexOfKeyField(fieldDefs);
	                switch (fieldDefs[keyInd].type.intValue()) {
	                    case FieldDef.INTEGER :
	                        db.deleteRow(dbSession, tableName, new Integer(parts[2]));
	                        break;
	                    case FieldDef.STRING :
	                        db.deleteRow(dbSession, tableName, new String(parts[2]));
	                        break;
	                    default :
	                        break;
	                }
	            } else if (line.startsWith("SLEEP")) {
	                String[] parts = Splitter.split(line, ' ', 0);
	                long time = Integer.parseInt(parts[1]);
	                try {
	                    System.out.println("SLEEP " + time + "ms");
	                    Thread.sleep(time);
	                }
	                catch (InterruptedException e) {
	                }
	            } else if (line.startsWith("GETDATAFILE")) {
	                String[] parts = Splitter.split(line, ' ', 0);
	                String symbName = parts[1];
	                Bundle b = session.getSourceDeploymentPackage().getBundle(symbName);
	                File f = session.getDataFile(b);
	                this.bundlePrivateArea = f;
	            } else if (line.startsWith("CREATEFILE")) {
	                String[] parts = Splitter.split(line, ' ', 0);
	                String fileName = parts[1];
	                File newFile = new File(bundlePrivateArea, fileName);
                    newFile.createNewFile();
	            }
	            line = br.readLine();
	        }
        } catch (ResourceProcessorException e) {
			throw e;
        } catch (Exception e) {
            throw new ResourceProcessorException(
            		ResourceProcessorException.CODE_OTHER_ERROR, e.getMessage(), e);
        }
    }

    private void deleteTables(String resName) {
        Hashtable artifacts = (Hashtable) dps.get(actDp);
        if (null == artifacts)
            return;
        Set artifactsForRes = (Set) artifacts.get(resName);
        if (null == artifactsForRes)
            return;
        for (Iterator iter = artifactsForRes.iterator(); iter.hasNext();) {
            String tableName = (String) iter.next();
            db.dropTable(dbSession, tableName);
            iter.remove();
        }
    }

    private Object[] getRow(FieldDef[] fieldDefs, String line) {
        String[] parts = Splitter.split(line, '/', 0);
        Vector ret = new Vector();
        for (int i = 0; i < parts.length; i++) {
            switch (fieldDefs[i].type.intValue()) {
                case FieldDef.INTEGER :
                    ret.add(new Integer(parts[i]));
                    break;
                case FieldDef.STRING :
                    ret.add(new String(parts[i]));
                    break;
                default :
                    break;
            }
        }
        return ret.toArray();
    }

    private FieldDef[] getFieldDefs(String[] parts) {
        Vector ret = new Vector();
        int keyInd = Integer.parseInt(parts[parts.length - 1]);
        for (int i = 2; i < parts.length - 2; i += 2) {
            String n = i == 2 ? parts[i].substring(1) : parts[i];
            String t = parts[i + 1];
            if (t.startsWith("Integer")) {
                ret.add(new FieldDef(FieldDef.INTEGER, n, i - 2 == keyInd));
            } else if (t.startsWith("String")) {
                ret.add(new FieldDef(FieldDef.STRING, n, i - 2 == keyInd));
            }
        }
        return (FieldDef[]) ret.toArray(new FieldDef[] {});
    }

    public void dropped(String resName) throws ResourceProcessorException {
        Hashtable ht = (Hashtable) dps.get(actDp);
        Set effects = (Set) ht.get(resName);
        for (Iterator iter = effects.iterator(); iter.hasNext();) {
            String tableName = (String) iter.next();
            db.dropTable(dbSession, tableName);
        }
        ht.remove(resName);
    }

    public void start(BundleContext context) throws Exception {
        this.context = context;
        String s = (String) context.getBundle().getHeaders().get("pid");
        pid = null == s ? "default_pid" : s;
        ServiceReference ref = context.getServiceReference(Db.class.getName());
        db = (Db) context.getService(ref);
        Dictionary	d = new Hashtable();
		d.put("type", "db");
		d.put("id", pid);
		d.put(Constants.SERVICE_PID, pid);
        context.registerService(ResourceProcessor.class.getName(), this, d);
        
        loadFromStore();
        
        System.out.println("DbResourceProcessor started. Id: " + pid);
    }

    public void stop(BundleContext context) throws Exception {
    	saveToStore();
    	
        System.out.println("DbResourceProcessor stopped. Id: " + pid);
    }
    
    // FOR TEST ONLY
    public Set getResources(DeploymentPackage dp, String resName) {
        DpRec dpRec = new DpRec(dp.getName(), dp.getVersion().toString());
        Hashtable ht = (Hashtable) dps.get(dpRec);
        if (null == ht)
            return null;
        Set s = (Set) ht.get(resName);
        return s;
    }
    
    // FOR TEST ONLY
    public File getBundlePrivateArea() {
        return bundlePrivateArea;
    }

    /**
     * @see org.osgi.service.deploymentadmin.spi.ResourceProcessor#dropAllResources()
     */
    public void dropAllResources() throws ResourceProcessorException {
        Hashtable ht = (Hashtable) dps.get(actDp);
        if (null == ht)
            return;
        for (Iterator iter = ht.keySet().iterator(); iter.hasNext();) {
            String resName = (String) iter.next();
            Set effects = (Set) ht.get(resName);
            for (Iterator iter2 = effects.iterator(); iter2.hasNext();) {
                String tableName = (String) iter2.next();
                db.dropTable(dbSession, tableName);
            }
            iter.remove();
        }
        dps.remove(actDp);
    }

    /**
     * @throws DeploymentException
     * @see org.osgi.service.deploymentadmin.spi.ResourceProcessor#prepare()
     */
    public void prepare() throws ResourceProcessorException {
    }

    /**
     * @see org.osgi.service.deploymentadmin.spi.ResourceProcessor#commit()
     */
    public void commit() {
        db.commit(dbSession);
    }

    /**
     * @see org.osgi.service.deploymentadmin.spi.ResourceProcessor#rollback()
     */
    public void rollback() {
        db.rollback(dbSession);
        
        load(new ByteArrayInputStream(copy.toByteArray()));
    }

    /**
     * @see org.osgi.service.deploymentadmin.spi.ResourceProcessor#cancel()
     */
    public void cancel() {
        // TODO
    }

}
