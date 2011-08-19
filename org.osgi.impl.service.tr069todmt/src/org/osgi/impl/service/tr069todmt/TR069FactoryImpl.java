package org.osgi.impl.service.tr069todmt;

import java.io.*;

import org.osgi.framework.*;
import org.osgi.service.dmt.*;
import org.osgi.service.tr069todmt.*;

import aQute.bnd.annotation.component.*;

@Component(servicefactory=true)
public class TR069FactoryImpl implements TR069Factory {

	long id;
	File file;
	
	@Activate
	void activate(BundleContext context) {
		this.file=context.getDataFile("ids");
	}
	
	@Override
	public TR069Connector create(DmtSession session) {
		return new TR069ConnectorImpl(this,session);
	}

	/**
	 * @return
	 */
	public long assignId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
