package org.osgi.test.cases.tr069todmt;

import java.util.*;

import org.osgi.service.dmt.*;
import org.osgi.service.log.*;
import org.osgi.test.support.compatibility.*;

public class FrameworkTest extends DefaultTestBundleControl {
	DmtAdmin			dmtAdmin	= getService(DmtAdmin.class);
	LogReaderService	log			= getService(LogReaderService.class);

	@SuppressWarnings("unchecked")
	public void testSimple() throws DmtException {

		DmtSession session = dmtAdmin.getSession(".",
				DmtSession.LOCK_TYPE_ATOMIC);
		try {

			traverse(session, ".", "");

			System.out.println(session.getNodeValue("./OSGi/Framework/StartLevel"));
			session.commit();
		}
		finally {
			session.close();
			List<LogEntry> les = Collections.list(log.getLog());
			for (LogEntry le : les) {
				System.out.println(le.getMessage());
			}
		}

	}

	private void traverse(DmtSession session, String name, String indent)
			throws DmtException {
		if ( session.isLeafNode(name)) {
			System.out.println(indent + name + " = " + session.getNodeValue(name));			
		} else {
			System.out.println(indent + name);			
			String[] names = session.getChildNodeNames(name);
			for (String sub : names) {
				traverse(session, name + "/" + sub, indent + " ");
			}
		}
			
	}

}
