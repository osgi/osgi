package com.nokia.test.exampleresourceprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;

/**
 * ExampleResourceProcessor implements a simple ResourceProcessor for testing and 
 * demo purposes. <p>
 * Prints the content of the managed textual resource files on the standard output. 
 * Resource files that have a resource name begining with "bye" are stored persistently 
 * and printed at uninstall.
 */
public class ExampleResourceProcessor /*implements ResourceProcessor, BundleActivator,
		Serializable*/ {
/*
    // the id of the RP (this string is printed before 
    // each tranBuffer lines)
    private transient String            id;
    
    private transient DeploymentPackage	actDp;
	private transient int 				actOperation;

	// in case of commit it is printed otherwise ignored
	private transient StringBuffer	    tranBuffer;
	
    private transient BundleContext 	context;
    
    // in case of commit these resource names are stored
    private transient HashSet			toRemove;
    // in case of commit these resource names are removed
    private transient HashSet			toAdd;
    
    // in case of rollback these saved resource files are deleted
    private transient HashSet			storedFiles;
    
    // managed DPs
    private Hashtable			        dps = new Hashtable();
    
	public void begin(DeploymentPackage dp, int operation) {
		this.actDp = dp;
		this.actOperation = operation;
		tranBuffer = new StringBuffer();
		toAdd = new HashSet();
	    toRemove = new HashSet();
	    storedFiles = new HashSet();
	}

	public void complete(boolean commit) {
		if (commit) {
			System.out.println(tranBuffer);
			
			// update registry of managed resources 
			HashSet set = (HashSet) dps.get(actDp);
			if (null == set) {
			    set = new HashSet();
			    dps.put(actDp, set);
			}
			set.removeAll(toRemove);
			set.addAll(toAdd);

			// delete dropped files
			for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
                String resName = (String) iter.next();
                if (!resName.startsWith("bye"))
                    continue;
                File f = context.getDataFile(getFileNameToResource(resName));
			    if (null != f)
			        f.delete();
            }
			
			// DP is not managed by the RP any more
			if (ResourceProcessor.UNINSTALL == actOperation)
			    dps.remove(actDp);
			
			save();
		} else {
			for (Iterator iter = storedFiles.iterator(); iter.hasNext();) {
                File f = (File) iter.next();
                f.delete();
                
                File old = new File(f.getName() + "_old");
                if (old.exists())
                    old.renameTo(f);
            }
		}
		
		tranBuffer = null;
		actDp = null;
		toAdd = null;
	    toRemove = null;
	    storedFiles = null;
	}

    private void save() {
        File f = context.getDataFile(this.getClass().getName() + ".obj");
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != oos) {
                try {
                    oos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private String getFileNameToResource(String resName) {
        return actDp.getName() + actDp.getVersion() + resName;
    }

    public void process(String name, InputStream stream) throws Exception {
	    if (actOperation == ResourceProcessor.INSTALL ||
	        actOperation == ResourceProcessor.UPDATE) 
	    {
            if (name.startsWith("bye")) {
    	        if (actOperation == ResourceProcessor.UPDATE) {
    	            File f = context.getDataFile(getFileNameToResource(name));
    	            if (f.exists()) {
    	                f.renameTo(new File(getFileNameToResource(name) + "_old"));
    	            }
    	        }
    	        File f = context.getDataFile(getFileNameToResource(name));
                PrintWriter out = new PrintWriter(new FileWriter(f));
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                String line = in.readLine();
                while (null != line) {
                    out.println(line);
                    line = in.readLine();
                }
                out.close();
                storedFiles.add(f);
            } else {
	            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
	    		if (null == r)
	    			return;
	    		String line = r.readLine();
	    		while (null != line) {
	    			tranBuffer.append(id + ": " + line + "\n");
	    			line = r.readLine();
	    		}
            }
            toAdd.add(name);
        } else if (actOperation == ResourceProcessor.UNINSTALL) {
            if (name.startsWith("bye")) {
	            File f = context.getDataFile(getFileNameToResource(name));
	            BufferedReader in = new BufferedReader(new FileReader(f));
	            String line = in.readLine();
	            while (null != line) {
	                tranBuffer.append(id + ": " + line + "\n");
	                line = in.readLine();
	            }
	            in.close();
            } else { 
                // there is nothing to do
            }
            toRemove.add(name);
        }
	}

	public void dropped(String name) throws Exception {
		toRemove.add(name);
	}

	public void dropped() {
		// TODO
	}

	public void start(BundleContext context) throws Exception {
	    this.context = context;
	    
	    id = (String) context.getBundle().getHeaders().get("MyID");
		
		// deregisted by the OSGi framework
		Dictionary	d = new Hashtable();
		d.put("type", "log");
		d.put("id", id);
		context.registerService(ResourceProcessor.class.getName(), this, d);
	}

	public void stop(BundleContext context) throws Exception {
	}*/
	
}
