/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */
package org.osgi.impl.service.metatype;

import java.util.Enumeration;
import java.util.Vector;

import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;
import org.osgi.service.metatype.MetaTypeInformation;

/**
 * Implementation of MetaTypeProvider
 * <p>
 * Extension of MetaTypeProvider
 * <p>
 * Provides methods to:
 * <p> - getPids() get the Pids for a given Locale
 * <p> - getFactoryPids() get the Factory Pids for a given Locale
 * <p>
 * 
 * @author Julian Chen
 * @version 1.0
 */
public class MetaTypeInformationImpl extends MetaTypeProviderImpl implements
		MetaTypeInformation {

	/**
	 * Constructor of class MetaTypeInformationImpl.
	 */
	MetaTypeInformationImpl(Bundle bundle, SAXParserFactory parserFactory)
			throws java.io.IOException {
		super(bundle, parserFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.MetaTypeInformation#getPids()
	 */
	public String[] getPids() {

		if (_allPidOCDs.size() == 0) {
			return new String[0];
		}

		Vector pids = new Vector(7);
		Enumeration e = _allPidOCDs.elements();
		while (e.hasMoreElements()) {
			ObjectClassDefinitionImpl ocd = (ObjectClassDefinitionImpl) e
					.nextElement();
			pids.addElement(ocd.getID());
		}

		String[] retvalue = new String[pids.size()];
		pids.toArray(retvalue);
		return retvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.MetaTypeInformation#getFactoryPids()
	 */
	public String[] getFactoryPids() {

		if (_allFPidOCDs.size() == 0) {
			return new String[0];
		}

		Vector fpids = new Vector(7);
		Enumeration e = _allFPidOCDs.elements();
		while (e.hasMoreElements()) {
			ObjectClassDefinitionImpl ocd = (ObjectClassDefinitionImpl) e
					.nextElement();
			fpids.addElement(ocd.getID());
		}

		String[] retvalue = new String[fpids.size()];
		fpids.toArray(retvalue);
		return retvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.MetaTypeInformation#getBundle()
	 */
	public Bundle getBundle() {
		return this._bundle;
	}
}
