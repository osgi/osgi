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
	public static final char	LOCALE_SEP		= '_';						//$NON-NLS-1$

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

		if ((_icon == null) || (_icon.getIconSize() != sizeHint)) {
			return null;
		}
		Bundle b = _icon.getIconBundle();
		URL[] urls = FragmentUtils.findEntries(b, getLocalized(_icon.getIconName()));
		if (urls != null && urls.length > 0) {
			return urls[0].openStream();
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

		String resourceBase = (_localization == null ? Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME
				: _localization);

		// There are seven searching candidates possible:
		// baseName + 
		//		"_" + language1 + "_" + country1 + "_" + variation1	+ ".properties"
		// or	"_" + language1 + "_" + country1					+ ".properties"
		// or	"_" + language1										+ ".properties"
		// or	"_" + language2 + "_" + country2 + "_" + variation2	+ ".properties"
		// or	"_" + language2 + "_" + country2					+ ".properties"
		// or	"_" + language2										+ ".properties"
		// or	""													+ ".properties"
		//
		// Where language1[_country1[_variation1]] is the requested locale,
		// and language2[_country2[_variation2]] is the default locale.

		String[] searchCandidates = new String[7];

		// Candidates from passed locale:
		if (locale != null && locale.length() > 0) {
			int idx1_first = locale.indexOf(LOCALE_SEP);
			if (idx1_first == -1) {
				// locale has only language.
				searchCandidates[2] = MetaTypeProviderImpl.RESOURCE_FILE_CONN
						+ locale;
			}
			else {
				// locale has at least language and country.
				searchCandidates[2] = MetaTypeProviderImpl.RESOURCE_FILE_CONN
						+ locale.substring(0, idx1_first);
				int idx1_second = locale.indexOf(LOCALE_SEP, idx1_first+1);
				if (idx1_second == -1) {
					// locale just has both language and country.
					searchCandidates[1] = MetaTypeProviderImpl.RESOURCE_FILE_CONN
							+ locale;
				}
				else {
					// locale has language, country, and variation all.
					searchCandidates[1] = MetaTypeProviderImpl.RESOURCE_FILE_CONN
							+ locale.substring(0, idx1_second);
					searchCandidates[0] = MetaTypeProviderImpl.RESOURCE_FILE_CONN
							+ locale;
				}
			}
		}

		// Candidates from Locale.getDefault():
		String defaultLocale = Locale.getDefault().toString();
		int idx2_first  = defaultLocale.indexOf(LOCALE_SEP);
		int idx2_second = defaultLocale.indexOf(LOCALE_SEP, idx2_first+1);
		if (idx2_second != -1) {
			// default-locale is format of [language]_[country]_variation.
			searchCandidates[3] = MetaTypeProviderImpl.RESOURCE_FILE_CONN
					+ defaultLocale;
			if (searchCandidates[3].equalsIgnoreCase(searchCandidates[0])) {
				searchCandidates[3] = null;
			}
		}
		if ((idx2_first != -1) && (idx2_second != idx2_first+1)) {
			// default-locale is format of [language]_country[_variation].
			searchCandidates[4] = MetaTypeProviderImpl.RESOURCE_FILE_CONN
					+ ((idx2_second==-1) ? defaultLocale : defaultLocale.substring(0, idx2_second));
			if (searchCandidates[4].equalsIgnoreCase(searchCandidates[1])) {
				searchCandidates[4] = null;
			}			
		}
		if ((idx2_first == -1) && (defaultLocale.length() > 0)) {
			// default-locale has only language.
			searchCandidates[5] = MetaTypeProviderImpl.RESOURCE_FILE_CONN
					+ defaultLocale;
		}
		else if (idx2_first > 0) {
			// default-locale is format of language_[...].
			searchCandidates[5] = MetaTypeProviderImpl.RESOURCE_FILE_CONN
					+ defaultLocale.substring(0, idx2_first);
		}
		if (searchCandidates[5]!= null
				&& searchCandidates[5].equalsIgnoreCase(searchCandidates[2])) {
			searchCandidates[5] = null;
		}			

		// The final candidate.
		searchCandidates[6] = ""; //$NON-NLS-1$

		URL resourceUrl = null;
		URL[] urls = null;

		for (int idx=0; (idx < searchCandidates.length) && (resourceUrl == null); idx++) {
			urls = (searchCandidates[idx] == null
					? null : FragmentUtils.findEntries(bundle,
							resourceBase + searchCandidates[idx] + MetaTypeInformationImpl.RESOURCE_FILE_EXT));
			if (urls != null && urls.length > 0)
				resourceUrl = urls[0];
		}

		if (resourceUrl != null) {
			try {
				return new PropertyResourceBundle(resourceUrl.openStream());
			}
			catch (IOException ioe) {
				// Exception when creating PropertyResourceBundle object.
			}
		}
		return null;
	}
}
