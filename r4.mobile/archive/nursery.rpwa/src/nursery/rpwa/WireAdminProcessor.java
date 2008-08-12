package nursery.rpwa;

import java.io.InputStream;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.deploymentadmin.*;
import org.osgi.service.wireadmin.*;

public class WireAdminProcessor implements ResourceProcessor {
	WireAdmin			admin;
	DeploymentPackage	current;
	List				createdWires		= new Vector();
	List				toBeDeletedWires	= new Vector();

	public WireAdminProcessor(WireAdmin admin, BundleContext context)
			throws Exception {
		this.admin = admin;
		Dictionary properties = new Hashtable();
		properties.put(Constants.SERVICE_PID, "wire.admin.processor");
		context.registerService(ResourceProcessor.class.getName(), this,
				properties);
	}

	public void begin(DeploymentPackage dp, int operation) {
		current = dp;
	}

	public void complete(boolean commit) {
		delete(commit ? toBeDeletedWires : createdWires);
		toBeDeletedWires.clear();
		createdWires.clear();
	}

	public void process(String name, InputStream in) throws Exception {
		Properties properties = new Properties();
		properties.load(in);
		System.out.println("Properties " + properties );			
		Dictionary dict = new Hashtable();
		dict.put("deployment.package", current.getName());
		for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
			dict.put("resource.id", name );
			String producer = (String) i.next();
			String consumer = properties.getProperty(producer);
			Wire wire = admin.createWire(producer, consumer, dict);
			System.out.println("Creating " + wire.getProperties() );			
			createdWires.add(wire);
		}
	}

	public void dropped(String name) throws Exception {
		List l = getWires("(&(resource.id=" + name + ")(deployment.package="
				+ current.getName() + "))");
		toBeDeletedWires.addAll(l);
	}

	public void dropped() {
		List l = getWires("(deployment.package=" + current.getName() + ")");
		delete(l);

	}

	void delete(List wires) {
		while ( ! wires.isEmpty() ) {
			Wire wire = (Wire) wires.remove(0);
			System.out.println("Deleting " + wire);
			admin.deleteWire(wire);
		}
	}

	List getWires(String filter) {
		try {
			Wire[] wires = admin.getWires(filter);
			return Arrays.asList(wires == null ? new Wire[0] : wires );
		}
		catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		return new Vector();
	}
}