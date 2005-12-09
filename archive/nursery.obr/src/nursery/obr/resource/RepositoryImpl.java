/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package nursery.obr.resource;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.kxml2.io.KXmlParser;
import org.osgi.service.obr.*;
import org.xmlpull.v1.*;

/**
 * Implements the basic repository. A repository holds a set of resources.
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class RepositoryImpl implements Repository {
	transient Map			resources		= new HashMap();
	URL						url;
	String					date;
	Set						visited			= new HashSet();
	final static Resource[]	EMPTY_RESOURCE	= new Resource[0];
	String					name			= "Untitled";

	/**
	 * Each repository is identified by a single URL.
	 * 
	 * A repository can hold referrals to other repositories. These referred
	 * repositories are included at the point of referall.
	 * 
	 * @param url
	 */
	public RepositoryImpl(URL url) {
		this.url = url;
	}

	/**
	 * Refresh the repository from the URL.
	 * 
	 * @throws Exception
	 */
	public void refresh() throws Exception {
		resources.clear();
		parseDocument(url);
		visited = null;
	}

	/**
	 * Parse the repository.
	 * 
	 * @param parser
	 * @throws Exception
	 */
	private void parseRepository(XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_DOCUMENT, null, null);
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, "repository");
		date = parser.getAttributeValue(null, "time");
		name = parser.getAttributeValue(null, "name");
		if (name == null)
			name = "Untitled";

		while (parser.nextTag() == XmlPullParser.START_TAG) {
			if (parser.getName().equals("resource")) {
				ResourceImpl resource = new ResourceImpl(parser);
				resources.put(resource.getLocalId(), resource);
			}
			else if (parser.getName().equals("referral"))
				referral(parser);
			else
				throw new IllegalArgumentException(
						"Invalid tag in repository: " + url + " "
								+ parser.getName());
		}
		parser.require(XmlPullParser.END_TAG, null, "repository");
	}

	/**
	 * We have a referral to another repository. Just create another parser and
	 * read it inline.
	 * 
	 * @param parser
	 */
	void referral(XmlPullParser parser) {
		try {
			parser.require(XmlPullParser.START_TAG, null, "referral");
			String path = parser.getAttributeValue(null, "url");
			URL url = new URL(this.url, path);
			parseDocument(url);
			parser.next();
			parser.require(XmlPullParser.END_TAG, null, "referral");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse a repository document.
	 * 
	 * @param url
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	void parseDocument(URL url) throws IOException, XmlPullParserException,
			Exception {
		if (!visited.contains(url)) {
			visited.add(url);

			Reader reader = new InputStreamReader(url.openStream());
			XmlPullParser parser = new KXmlParser();
			parser.setInput(reader);
			parseRepository(parser);
		}
	}

	public URL getURL() {
		return url;
	}

	/**
	 * @return
	 */
	public Collection getResourceList() {
		return resources.values();
	}

	public Resource[] getResources() {
		return (Resource[]) getResourceList().toArray(EMPTY_RESOURCE);
	}

	public String getName() {
		return name;
	}
	
	public Resource getResourceById(String id) {
		return (Resource) resources.get(id);
	}

}
