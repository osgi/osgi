/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.filter.tb1;

import java.util.*;
import org.osgi.framework.*;

/**
 * Bundle used to install a bunch of services later interrogated with various
 * filters.
 * 
 * @author Ericsson Radio Systems AB
 */
public class InstallServices implements BundleActivator {
	ServiceA			_sa1;
	Properties			_sa1Props;
	ServiceRegistration	_sra1;
	ServiceB			_sb1;
	Properties			_sb1Props;
	ServiceRegistration	_srb1;
	ServiceB			_sb2;
	Properties			_sb2Props;
	ServiceRegistration	_srb2;
	ServiceC			_sc1;
	Properties			_sc1Props;
	ServiceRegistration	_src1;
	ServiceC			_sc2;
	Properties			_sc2Props;
	ServiceRegistration	_src2;
	ServiceC			_sc3;
	Properties			_sc3Props;
	ServiceRegistration	_src3;
	ServiceD			_sd1;
	Properties			_sd1Props;
	ServiceRegistration	_srd1;

	/**
	 * Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		_sa1 = new ServiceA();
		_sa1Props = new Properties();
		_sa1Props.put("id", "a1");
		_sa1Props.put("name", "ServiceA");
		_sa1Props.put("version", new Float(1.0));
		_sa1Props.put("compatible", new Float(1.0));
		_sa1Props.put("description", "Service A");
		_sa1Props.put("Integer", new Integer(1));
		_sa1Props.put("Long", new Long(1));
		_sa1Props.put("Byte", new Byte("1"));
		_sa1Props.put("Short", new Short("1"));
		_sra1 = bc
				.registerService(TestService.class.getName(), _sa1, _sa1Props);
		org.osgi.test.cases.framework.filter.tbc.FilterControl.serviceA = _sra1;
		org.osgi.test.cases.framework.filter.tbc.FilterControl.serviceAProperties = _sa1Props;
		_sb1 = new ServiceB();
		_sb1Props = new Properties();
		_sb1Props.put("id", "b1");
		_sb1Props.put("name", "ServiceB");
		_sb1Props.put("version", new Float(1.0));
		_sb1Props.put("compatible", new Float(1.0));
		_sb1Props.put("description", "Service B");
		_sb1Props.put("Integer", new Integer(1));
		_sb1Props.put("Long", new Long(1));
		_sb1Props.put("Byte", new Byte("1"));
		_sb1Props.put("Short", new Short("1"));
		_srb1 = bc
				.registerService(TestService.class.getName(), _sb1, _sb1Props);
		_sb2 = new ServiceB();
		_sb2Props = new Properties();
		_sb2Props.put("id", "b2");
		_sb2Props.put("name", "ServiceB");
		_sb2Props.put("version", new Float(2.0));
		_sb2Props.put("compatible",
				new Float[] {new Float(1.0), new Float(2.0)});
		_sb2Props.put("description", "Service B version 2");
		_sb2Props.put("Integer", new Integer(2));
		_sb2Props.put("Long", new Long(2));
		_sb2Props.put("Byte", new Byte("2"));
		_sb2Props.put("Short", new Short("2"));
		_srb2 = bc
				.registerService(TestService.class.getName(), _sb2, _sb2Props);
		_sc1 = new ServiceC();
		_sc1Props = new Properties();
		_sc1Props.put("id", "c1");
		_sc1Props.put("name", "ServiceC");
		_sc1Props.put("version", new Float(1.0));
		_sc1Props.put("compatible", new Float(1.0));
		_sc1Props.put("description", "Service C");
		_sc1Props.put("Integer", new Integer(1));
		_sc1Props.put("Long", new Long(1));
		_sc1Props.put("Byte", new Byte("1"));
		_sc1Props.put("Short", new Short("1"));
		_src1 = bc
				.registerService(TestService.class.getName(), _sc1, _sc1Props);
		_sc2 = new ServiceC();
		_sc2Props = new Properties();
		_sc2Props.put("id", "c2");
		_sc2Props.put("name", "ServiceC");
		_sc2Props.put("version", new Float(2.0));
		_sc2Props.put("compatible", new Float[] {new Float(1.0),
				new Float(1.5), new Float(2.0)});
		_sc2Props.put("description", "Service C version 2");
		_sc2Props.put("Integer", new Integer(2));
		_sc2Props.put("Long", new Long(2));
		_sc2Props.put("Byte", new Byte("2"));
		_sc2Props.put("Short", new Short("2"));
		_src2 = bc
				.registerService(TestService.class.getName(), _sc2, _sc2Props);
		_sc3 = new ServiceC();
		_sc3Props = new Properties();
		_sc3Props.put("id", "c25");
		_sc3Props.put("name", "ServiceC");
		_sc3Props.put("version", new Float(2.5));
		Vector v = new Vector();
		v.addElement(new Float(2.0));
		v.addElement(new Float(2.1));
		v.addElement(new Float(2.2));
		v.addElement(new Float(2.3));
		_sc3Props.put("compatible", v);
		_sc3Props.put("description", "Service C version 2.5 \",*'()");
		_sc3Props.put("Integer", new Integer(3));
		_sc3Props.put("Long", new Long(3));
		_sc3Props.put("Byte", new Byte("3"));
		_sc3Props.put("Short", new Short("3"));
		_src3 = bc
				.registerService(TestService.class.getName(), _sc3, _sc3Props);
		
		// Install test service ServiceD with several properties used
		// for testing several filters
		_sd1 = new ServiceD();
		_sd1Props = new Properties();
		_sd1Props.put("id", "d25");
		_sd1Props.put("name", "ServiceD");
		_sd1Props.put("version", new Float(2.5));
		_sd1Props.put("compatible", new Float(1.0));
		_sd1Props.put("description", "Service D");
		_sd1Props.put("Integer", new Integer(4));
		_sd1Props.put("Long", new Long(4));
		_sd1Props.put("Byte", new Byte("4"));
		_sd1Props.put("Short", new Short("4"));
		_sd1Props.put("ObjectA", new ObjectA("4"));
		_sd1Props.put("ObjectB", new ObjectB("4"));
		_sd1Props.put("ObjectC", new ObjectC());
		_srd1 = bc
				.registerService(TestService.class.getName(), _sd1, _sd1Props);
	}

	/**
	 * Stops the bundle by unregistering the services.
	 */
	public void stop(BundleContext bc) {
		try {
			_sra1.unregister();
			_srb1.unregister();
			_srb2.unregister();
			_src1.unregister();
			_src2.unregister();
			_src3.unregister();
			_srd1.unregister();
		}
		catch (IllegalStateException e) { /* Ignore */
		}
		_sra1 = null;
		_sa1 = null;
		_srb1 = null;
		_sb1 = null;
		_srb2 = null;
		_sb2 = null;
		_src1 = null;
		_sc1 = null;
		_src2 = null;
		_sc2 = null;
		_src3 = null;
		_sc3 = null;
		_srd1 = null;
		_sd1 = null;
	}
}
