package org.osgi.impl.service.deploymentadmin;

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

public class ExampleResourceProcessor implements ResourceProcessor, BundleActivator,
		Serializable {

    private transient String            id;
    private transient DeploymentPackage	actDp;
	private transient int 				actOperation;
	private transient StringBuffer	    tranBuffer;
    private transient BundleContext 	context;
    private transient HashSet			willBeRemoved;
    private transient HashSet			willBeAdded;
    private Hashtable			        dps = new Hashtable();
    
	public void begin(DeploymentPackage dp, int operation) {
		this.actDp = dp;
		this.actOperation = operation;
		tranBuffer = new StringBuffer();
		willBeAdded = new HashSet();
	    willBeRemoved = new HashSet();
	}

	public void complete(boolean commit) {
		if (commit) {
			System.out.println(tranBuffer.toString());
			
			HashSet set = (HashSet) dps.get(actDp.getName());
			if (null == set) {
			    set = new HashSet();
			    dps.put(actDp.getName(), set);
			}
			set.removeAll(willBeRemoved);
			if (actOperation == ResourceProcessor.UPDATE)
			    System.out.println(id + " drops resources: " + willBeRemoved);
			set.addAll(willBeAdded);
			if (ResourceProcessor.UNINSTALL == actOperation) {
			    for (Iterator iter = willBeRemoved.iterator(); iter.hasNext();) {
                    String file = (String) iter.next();
                    File f = context.getDataFile(file);
    			    if (null != f)
    			        f.delete();                    
                }
			    
			    dps.remove(actDp.getName());
			}
			
			save();
		} else {
			// there is nothing to do
		}
		tranBuffer = null;
		actDp = null;
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

    public void process(String name, InputStream stream) throws Exception {
	    if (actOperation == ResourceProcessor.INSTALL ||
	        actOperation == ResourceProcessor.UPDATE) {
            if (name.startsWith("bye")) {
                // TODO transactionality
                File f = context.getDataFile(name);
                PrintWriter out = new PrintWriter(new FileWriter(f));
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                String line = in.readLine();
                while (null != line) {
                    out.println(line);
                    line = in.readLine();
                }
                out.close();
            } else if (name.startsWith("hello")) {
	            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
	    		if (null == r)
	    			return;
	    		String line = r.readLine();
	    		while (null != line) {
	    			tranBuffer.append(id + ": " + line + "\n");
	    			line = r.readLine();
	    		}
            }
            willBeAdded.add(name);
        } else if (actOperation == ResourceProcessor.UNINSTALL) {
            if (name.startsWith("hello")) {
                // there is nothing to do
            } else if (name.startsWith("bye")) {
	            File f = context.getDataFile(name);
	            BufferedReader in = new BufferedReader(new FileReader(f));
	            String line = in.readLine();
	            while (null != line) {
	                tranBuffer.append(id + ": " + line + "\n");
	                line = in.readLine();
	            }
	            in.close();
            }
            willBeRemoved.add(name);
        }
	}

	public void dropped(String name) throws Exception {
		willBeRemoved.add(name);
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
	}
	
}
