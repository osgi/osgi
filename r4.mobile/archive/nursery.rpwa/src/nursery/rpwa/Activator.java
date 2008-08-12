package nursery.rpwa;

import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.wireadmin.*;

public class Activator implements BundleActivator  {
	
	public void start(BundleContext context) throws Exception {
		WireAdmin	wa = (WireAdmin) getService(WireAdmin.class, context);
		WireAdminProcessor	wap = new WireAdminProcessor(wa,context);

		Wire [] wires = wa.getWires(null);
		for ( int i=0; wires!=null && i< wires.length; i++ ) {
			wa.deleteWire(wires[i]);
		}
		Dictionary d =new Hashtable();
		d.put("test", "true");
		wa.createWire("A", "B", d );
		wa.createWire("com.bunny.navigation", "com.acme.gps.garmin",  d );
		
		list("Initial, we set 2 entries", wa);

		System.out.println("T1: Normal install of T1.r1 (3 entries)");
		DeploymentPackage dp = new DP(3456789,"T1", "1");
		wap.begin(dp, 0);
		wap.process("r1", getClass().getResourceAsStream("t1.properties"));
		list("T1: Before we commit the three new entries from r1 (should see new entries=5)", wa);
		wap.complete(true);
		list("T1: After commit we committed the entries of r1 (should be 5)", wa);
		
		System.out.println();
		System.out.println();
		System.out.println("T1: Drop r1, i.e. remove 3 entries. Normal commit");
		wap.begin(dp, 0);
		list("T1 Before dropping we should have 5 entries", wa);
		wap.dropped("r1");
		list("T1 After dropping, it should still be 5", wa);
		wap.complete(true);
		list("T1 After dropping commit, we should have 2 entries again", wa);

		System.out.println();
		System.out.println();
		System.out.println("T2: Install r1, but rollback");
		dp = new DP(614212,"T2", "2");
		wap.begin(dp, 0);
		wap.process("r1", getClass().getResourceAsStream("t1.properties"));
		list("T2: Before we committ the install (should be 3 new entries, =5)", wa);
		wap.complete(false);
		list("T2: After we have rolled back the install, we should see 2 entries again", wa);
		
		System.out.println();
		System.out.println();
		System.out.println("T2: Drop r1 with rollback (first installs r1 because it should not have been installed, r2 is also 3 entries)");
		wap.begin(dp, 0);
		wap.process("r1", getClass().getResourceAsStream("t1.properties"));
		wap.complete(true);
		
		wap.begin(dp, 0);
		list("T2: Before we drop r1 we should have 5 entries", wa);
		wap.dropped("r2");
		list("T2: After dropping, we still have 5 entries", wa);
		wap.complete(false);
		list("T2: After dropping r1 but rolling back, we should still have 5 entries", wa);

		System.out.println();
		System.out.println();
		System.out.println("T3: Install r2 and drop r1 then commit");
		
		wap.begin(dp, 0);
		list("T3: Before install r2, we now have 5 entries (base+r1)", wa);
		wap.process("r2", getClass().getResourceAsStream("t2.properties"));
		list("T3: Now installed r2, we now have 7 entries (base+r2+r1)", wa);
		wap.dropped("r1");
		list("T3: After dropping r1, we still have 7 entries (base+r2+r1)", wa);
		wap.complete(true);
		list("T3: After installing r2 and dropping r1, commiting we should have 4 entries (base+r2)", wa);

		System.out.println();
		System.out.println();
		System.out.println("T4: Install r1 and drop r2 then rollback");
		wap.begin(dp, 0);
		list("T4: Before install r1, we now have 4 entries (base+r2)", wa);
		wap.process("r1", getClass().getResourceAsStream("t1.properties"));
		list("T3: Installed r1, we now have 7 entries (base+r2+r2)", wa);
		wap.dropped("r2");
		list("T3: After dropping r2, we still have 7 entries (base+r2+r1)", wa);
		wap.complete(false);
		list("T3: After rollback, we should have 4 entries again (base+r2)", wa);

	}

	/**
	 * @param class1
	 * @param context
	 * @return
	 */
	private Object getService(Class class1, BundleContext context) {
		ServiceReference	ref = context.getServiceReference(class1.getName());
		if ( ref == null )
			throw new RuntimeException("No Such Service "  + class1.getName() );
		Object result = context.getService(ref);
		if ( result == null )
			throw new RuntimeException("Can not get service object " + class1.getName());
		return result;
	}

	private void list(String string, WireAdmin wa) throws InvalidSyntaxException, InterruptedException {
		Thread.sleep(500);
		System.out.println();
		
		Wire [] wires = wa.getWires(null);
//		for ( int i=0; wires!=null && i < wires.length; i++ ) {
//			System.out.println(
//					wires[i].getProperties() );
//		}
		System.out.println(string + "(" + wires.length + " entries )");
		
	}

	public void stop(BundleContext context) throws Exception {
	}


}
