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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.util.*;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * Implementation of ObjectClassDefinition
 * 
 * @author Julian Chen
 * @version 1.0
 */
public class ObjectClassDefinitionImpl extends LocalizationElement implements ObjectClassDefinition,
		Cloneable {

	public static final int		PID				= 0;
	public static final int		FPID			= 1;

	String						_name;
	String						_id;
	String						_description;
	int							_type;
	Vector						_required		= new Vector(7);
	Vector						_optional		= new Vector(7);
	Icon						_icon;

	/*
	 * Constructor of class ObjectClassDefinitionImpl.
	 */
	public ObjectClassDefinitionImpl(String name,
			String description, String localization) {

		this._name = name;
		this._description = description;
		this._localization = localization;
	}

	/*
	 * Constructor of class ObjectClassDefinitionImpl.
	 */
	public ObjectClassDefinitionImpl(String name, String id,
			String description, int type, String localization) {

		this._name = name;
		this._id = id;
		this._description = description;
		this._type = type;
		this._localization = localization;
	}

	/*
	 * 
	 */
	public synchronized Object clone() {

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl(_name,
				_id, _description, _type, _localization);
		for (int i = 0; i < _required.size(); i++) {
			AttributeDefinitionImpl ad = (AttributeDefinitionImpl) _required
					.elementAt(i);
			ocd.addAttributeDefinition((AttributeDefinitionImpl) ad.clone(),
					true);
		}
		for (int i = 0; i < _optional.size(); i++) {
			AttributeDefinitionImpl ad = (AttributeDefinitionImpl) _optional
					.elementAt(i);
			ocd.addAttributeDefinition((AttributeDefinitionImpl) ad.clone(),
					false);
		}
		if (_icon != null) {
			ocd.setIcon((Icon) _icon.clone());
		}
		return ocd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.ObjectClassDefinition#getName()
	 */
	public String getName() {
		return getLocalized(_name);
	}

	/**
	 * Method to set the name of ObjectClassDefinition.
	 */
	void setName(String name) {
		this._name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.ObjectClassDefinition#getID()
	 */
	public String getID() {
		return _id;
	}

	/*
	 * Method to set the ID of ObjectClassDefinition.
	 */
	void setID(String id) {
		this._id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.ObjectClassDefinition#getDescription()
	 */
	public String getDescription() {
		return getLocalized(_description);
	}

	/*
	 * Method to set the description of ObjectClassDefinition.
	 */
	void setDescription(String description) {
		this._description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.ObjectClassDefinition#getAttributeDefinitions(int)
	 */
	public AttributeDefinition[] getAttributeDefinitions(int filter) {

		AttributeDefinition[] atts;
		switch (filter) {
			case REQUIRED :
				atts = new AttributeDefinition[_required.size()];
				_required.toArray(atts);
				return atts;
			case OPTIONAL :
				atts = new AttributeDefinition[_optional.size()];
				_optional.toArray(atts);
				return atts;
			case ALL :
			default :
				atts = new AttributeDefinition[_required.size()
						+ _optional.size()];
				Enumeration e = _required.elements();
				int i = 0;
				while (e.hasMoreElements()) {
					atts[i] = (AttributeDefinition) e.nextElement();
					i++;
				}
				e = _optional.elements();
				while (e.hasMoreElements()) {
					atts[i] = (AttributeDefinition) e.nextElement();
					i++;
				}
				return atts;
		}
	}

	/*
	 * Method to add one new AD to ObjectClassDefinition.
	 */
	public void addAttributeDefinition(AttributeDefinition ad,
			boolean isRequired) {

		if (isRequired) {
			_required.addElement(ad);
		}
		else {
			_optional.addElement(ad);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.ObjectClassDefinition#getIcon(int)
	 */
	public InputStream getIcon(int sizeHint) throws IOException {

		Bundle b = _icon.getIconBundle();
		java.net.URL u = b.getResource(getLocalized(_icon.getIconName()));
		if (u != null) {
			return u.openStream();
		}
		else {
			return null;
		}
	}

	/**
	 * Method to set the icon of ObjectClassDefinition.
	 */
	public void setIcon(Icon icon) {
		this._icon = icon;
	}

	/**
	 * Method to get the type of ObjectClassDefinition.
	 */
	int getType() {
		return _type;
	}

	/**
	 * Method to set the type of ObjectClassDefinition.
	 */
	void setType(int type) {
		this._type = type;
	}

	/**
	 * Method to set the resource bundle for this OCD and all its ADs.
	 */
	void setResourceBundle(String assignedLocale, Bundle bundle) {

		if (assignedLocale == null)
			assignedLocale = Locale.getDefault().toString();
		_rb = getResourceBundle(assignedLocale, bundle);

		Enumeration allADReqs = _required.elements();
		while (allADReqs.hasMoreElements()) {
			AttributeDefinitionImpl ad = (AttributeDefinitionImpl) allADReqs
					.nextElement();
			ad.setResourceBundle(_rb);
		}

		Enumeration allADOpts = _optional.elements();
		while (allADOpts.hasMoreElements()) {
			AttributeDefinitionImpl ad = (AttributeDefinitionImpl) allADOpts
					.nextElement();
			ad.setResourceBundle(_rb);
		}
	}

	/*
	 * Internal Method - to get resource bundle.
	 */
	private ResourceBundle getResourceBundle(String locale, final Bundle bundle) {

		final String localeFile = (_localization == null ? Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME
				: _localization)
				+ MetaTypeProviderImpl.RESOURCE_FILE_CONN
				+ locale
				+ MetaTypeInformationImpl.RESOURCE_FILE_EXT;
		URL resourceUrl = null;
		try {
			resourceUrl = (URL) AccessController
					.doPrivileged(new PrivilegedExceptionAction() {
						public Object run() throws IOException {
							return bundle.getResource(localeFile);
						}
					});
		}
		catch (PrivilegedActionException pae) {
			// Do nothing, since resourceUrl is still null.
		}
		if (resourceUrl != null) {
			try {
				return new PropertyResourceBundle(resourceUrl.openStream());
			}
			catch (IOException ioe) {
				// Exception when creating PropertyResourceBundle
				// object.
			}
		}
		return null;
	}
}
