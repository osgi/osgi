/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package unittests;

import javax.xml.parsers.SAXParserFactory;
import junit.framework.TestCase;
import org.osgi.impl.bundle.autoconf.MetaData;
import org.osgi.service.metatype.AttributeDefinition;
import org.xml.sax.InputSource;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ParserTest extends TestCase {
	public InputSource getInputSource(String from) {
		return new InputSource(ParserTest.class.getResourceAsStream(from));
	}
	
	public void testParser1() throws Exception {
		// just a basic test that everything is in place
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		spf.setValidating(true);
		MetaData md = new MetaData(spf,getInputSource("testParser1.xml"));
		assertEquals(1,md.designate.size());
		MetaData.Designate d = (MetaData.Designate)md.designate.get(0);
		assertEquals("foo",d.pid);
		assertEquals(true,d.optional);
		assertEquals(1,d.objects.size());
		MetaData.Object o = (MetaData.Object)d.objects.get(0);
		assertEquals("bar",o.type);
		assertEquals(2,o.attributes.size());
		MetaData.Attribute a = (MetaData.Attribute)o.attributes.get(0);
		assertEquals("attr1",a.name);
		assertEquals("cont1",a.content);
		a = (MetaData.Attribute)o.attributes.get(1);
		assertEquals("attr2",a.name);
		assertEquals("cont2",a.content);
		assertEquals(1,md.ocd.size());
		MetaData.OCD ocd = (MetaData.OCD)md.ocd.get(0);
		assertEquals("ocd1",ocd.name);
		assertEquals("asdf",ocd.description);
		assertEquals("ocd1id",ocd.id);
		assertEquals(1,ocd.icon.size());
		MetaData.Icon icon = (MetaData.Icon)ocd.icon.get(0);
		assertEquals("ic1",icon.resource);
		assertEquals(1234,icon.size);
		assertEquals(1,ocd.ad.size());
		MetaData.AD ad = (MetaData.AD)ocd.ad.get(0);
		assertEquals("ad1",ad.id);
		assertEquals(AttributeDefinition.LONG,ad.type);
		assertEquals(1,ad.option.size());
		MetaData.Option option = (MetaData.Option)ad.option.get(0);
		assertEquals("opt1",option.label);
		assertEquals("optval1",option.value);
	}
}
