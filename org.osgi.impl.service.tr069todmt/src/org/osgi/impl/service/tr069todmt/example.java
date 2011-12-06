package org.osgi.impl.service.tr069todmt;

import org.osgi.service.dmt.*;

public class example {
	DmtAdmin admin;
	String $;

	public void example() throws DmtException {
		DmtSession session = admin.getSession($ + "/Framework",
				DmtSession.LOCK_TYPE_ATOMIC);
		try {
			session.createInteriorNode("Bundle/my_bundle");
			session.setNodeValue("Bundle/my_bundle/URL", new DmtData(
					"http://www.example.com/bundles/my_bundle.jar"));
			session.setNodeValue("Bundle/my_bundle/AutoStart",
					DmtData.TRUE_VALUE);
			session.setNodeValue("Bundle/my_bundle/RequestedState",
					new DmtData("ACTIVE"));

			session.setNodeValue("Bundle/up_bundle/URL", new DmtData(
					"http://www.example.com/bundles/up_bundle-2.jar"));

			session.setNodeValue("Bundle/old_bundle/RequestedState",
					new DmtData("UNINSTALLED"));
			try {
				session.commit();
			} catch (Exception e) {
				// ouch
			}
		} catch (Exception e) {
			session.rollback();
		}
	}
	
	public void example2() throws DmtException {
		DmtSession session = admin.getSession($ + "/Framework",
				DmtSession.LOCK_TYPE_ATOMIC);
		try {
			String prefix ="Bundle/my_bundle/Wire/osgi.wiring.package";
			String [] wires = session.getChildNodeNames(prefix);
			for ( String wire : wires ) {
				String name = session.getNodeValue(prefix + "/" + wire + "/Capability/osgi.wiring.package" ).getString();
				String provider = session.getNodeValue(prefix + "/" + wire + "/Provider" ).getString();
				String requirer = session.getNodeValue(prefix + "/" + wire + "/Requirer" ).getString();
				System.out.printf("%-20s %-30s %s\n", name, provider, requirer);
			}
			session.setNodeValue("Bundle/old_bundle/RequestedState",
					new DmtData("UNINSTALLED"));
			try {
				session.commit();
			} catch (Exception e) {
				// ouch
			}
		} catch (Exception e) {
			session.rollback();
		}
	}
	
	public void example3() throws DmtException {
		DmtSession session = admin.getSession($,
				DmtSession.LOCK_TYPE_ATOMIC);
		try {
			session.createInteriorNode("Filter/mq-1");
			session.setNodeValue("Filter/mq-1/Target", new DmtData("Framework/Bundle/*"));
			session.setNodeValue("Filter/mq-1/Filter", new DmtData("(AutoStart=true)"));

			String[] autostarted = session.getChildNodeNames("Filter/mq-1/Result/Framework/Bundle");
			System.out.println("Auto started bundles");
			for ( String location : autostarted)
				System.out.println(location);
			
			session.deleteNode("Filter/mq-1");
			try {
				session.commit();
			} catch (Exception e) {
				// ouch
			}
		} catch (Exception e) {
			session.rollback();
		}
	}
	
	public void example4() throws DmtException {
		DmtSession session = admin.getSession($,
				DmtSession.LOCK_TYPE_ATOMIC);
		try {
			session.createInteriorNode("Log/ls");
			session.setNodeValue("Filter/mq-1/Filter", new DmtData("(AutoStart=true)"));

			String[] autostarted = session.getChildNodeNames("Filter/mq-1/Result/Framework/Bundle");
			System.out.println("Auto started bundles");
			for ( String location : autostarted)
				System.out.println(location);
			
			session.deleteNode("Filter/mq-1");
			try {
				session.commit();
			} catch (Exception e) {
				// ouch
			}
		} catch (Exception e) {
			session.rollback();
		}
	}
	
	
	
	
}
