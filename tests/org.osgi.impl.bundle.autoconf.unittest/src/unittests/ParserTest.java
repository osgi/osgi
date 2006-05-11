/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package unittests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.osgi.impl.bundle.autoconf.MetaData;
import org.osgi.service.metatype.AttributeDefinition;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import unittests.metadata.AD;
import unittests.metadata.Designate;
import unittests.metadata.OCD;
import unittests.metadata.Object;
import unittests.metadata.ObjectFactory;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ParserTest extends TestCase {
	JAXBContext jaxbContext;
	ObjectFactory of;
	unittests.metadata.MetaData metaData;
	MetaData resultMetaData;
	SAXParserFactory spf;
	AD	ad;
	MetaData.AD ad0;
    public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	public void setUp() throws Exception {
		of = new ObjectFactory();
		jaxbContext = JAXBContext.newInstance("unittests.metadata");
		metaData = of.createMetaData();
		spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		spf.setValidating(true);
		resultMetaData = null;
		ad = null;
		ad0 = null;
	}
	
	public void tearDown() throws Exception {
		of = null;
		jaxbContext = null;
		spf = null;
		resultMetaData = null;
		ad = null;
		ad0 = null;
	}

	public InputSource getInputSource(String from) {
		return new InputSource(ParserTest.class.getResourceAsStream(from));
	}
	
	public void testParser1() throws Exception {
		// just a basic test that everything is in place
		MetaData md = new MetaData(getParser(),getInputSource("testParser1.xml"));
		assertEquals(1,md.designates.length);
		MetaData.Designate d = md.designates[0];
		assertEquals("foo",d.pid);
		assertEquals(true,d.optional);
		assertEquals(1,d.objects.length);
		MetaData.Object o = d.objects[0];
		assertEquals("bar",o.ocdref);
		assertEquals(2,o.attributes.length);
		MetaData.Attribute a = o.attributes[0];
		assertEquals("attr1",a.adref);
		assertEquals("cont1",a.content);
		a = o.attributes[1];
		assertEquals("attr2",a.adref);
		assertEquals("cont2",a.content);
		assertEquals(1,md.ocds.length);
		MetaData.OCD ocd = md.ocds[0];
		assertEquals("ocd1",ocd.name);
		assertEquals("asdf",ocd.description);
		assertEquals("ocd1id",ocd.id);
		assertEquals(1,ocd.icons.length);
		MetaData.Icon icon = ocd.icons[0];
		assertEquals("ic1",icon.resource);
		assertEquals(1234,icon.size);
		assertEquals(1,ocd.ads.length);
		MetaData.AD ad = ocd.ads[0];
		assertEquals("ad1",ad.id);
		assertEquals(AttributeDefinition.LONG,ad.type);
		assertEquals(1,ad.options.length);
		MetaData.Option option = ad.options[0];
		assertEquals("opt1",option.label);
		assertEquals("optval1",option.value);
	}

	private SAXParser getParser() throws ParserConfigurationException, SAXException, SAXNotRecognizedException, SAXNotSupportedException, MalformedURLException {
		SAXParser sp = spf.newSAXParser();
		sp.setProperty(JAXP_SCHEMA_LANGUAGE,W3C_XML_SCHEMA);
		URL autoconfURL = new File(System.getProperty("metatype.schema")).toURL();
		sp.setProperty(JAXP_SCHEMA_SOURCE,autoconfURL.toExternalForm());
		return sp;
	}
	
	public void testMinimal() throws Exception {
		Designate des = of.createDesignate();
		metaData.getDesignate().add(des);
		des.setPid("foo");
		Object obj = of.createObject();
		des.setObject(obj);
		obj.setOcdref("bar");
		convert();
		assertEquals(1,resultMetaData.designates.length);
		assertEquals("foo",resultMetaData.designates[0].pid);
		assertEquals(1,resultMetaData.designates[0].objects.length);
		assertEquals("bar",resultMetaData.designates[0].objects[0].ocdref);
	}

	public void testMultipleDesignates() throws Exception {
		Designate des = of.createDesignate();
		metaData.getDesignate().add(des);
		metaData.getDesignate().add(des);
		metaData.getDesignate().add(des);
		des.setPid("foo");
		Object obj = of.createObject();
		des.setObject(obj);
		obj.setOcdref("bar");
		convert();
		assertEquals(3,resultMetaData.designates.length);
	}
	
	private void setupBasicAD() throws Exception {
		Designate des = of.createDesignate();
		metaData.getDesignate().add(des);
		des.setPid("foo");
		Object obj = of.createObject();
		des.setObject(obj);
		obj.setOcdref("bar");
		
		OCD ocd = of.createOCD();
		metaData.getOCD().add(ocd);
		ocd.setName("ocd1");
		ocd.setId("id1");
		ad = of.createAD();
		ocd.getAD().add(ad);
		ad.setId("ad1");
	}

	public void testIntegerAD() throws Exception {
		setupBasicAD();
		
		ad.setType("Integer");
		convert();
		assertEquals(AttributeDefinition.INTEGER,ad0.type);
		assertEquals(0,ad0.cardinality);
		
		ad.setDefault("3");
		convert();
		assertEquals(1,ad0.defaultValue.length);
		assertEquals("3",ad0.defaultValue[0]);
		
		ad.setDefault(null);
		ad.setCardinality(0);
		convert();
		assertEquals(0,ad0.cardinality);
		
		ad.setCardinality(1);
		convert();
		assertEquals(1,ad0.cardinality);
		
		ad.setCardinality(4);
		ad.setDefault("1,2,3,4");
		convert();
		assertEquals(4,ad0.defaultValue.length);
		assertEquals("1",ad0.defaultValue[0]);
		assertEquals("4",ad0.defaultValue[3]);
	}
	
	public void testStringAD() throws Exception {
		setupBasicAD();
		
		ad.setType("String");
		convert();
		assertEquals(AttributeDefinition.STRING,ad0.type);
		assertEquals(0,ad0.cardinality);
		
		ad.setDefault("foo");
		convert();
		assertEquals("foo",ad0.defaultValue[0]);
		
		ad.setCardinality(4);
		ad.setDefault("a\\,b,b\\,c,c\\\\,d");
		convert();
		assertEquals(4,ad0.defaultValue.length);
		assertEquals("a,b",ad0.defaultValue[0]);
		assertEquals("b,c",ad0.defaultValue[1]);
		assertEquals("c\\",ad0.defaultValue[2]);
		assertEquals("d",ad0.defaultValue[3]);
	}
	
	public void convert() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		jaxbContext.createMarshaller().marshal(metaData,baos);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		resultMetaData = new MetaData(getParser(),new InputSource(bais));
		if ((resultMetaData!=null)&&(resultMetaData.ocds!=null)&&(resultMetaData.ocds.length>0)&&
				(resultMetaData.ocds[0]!=null)&&(resultMetaData.ocds[0].ads!=null)
				&&(resultMetaData.ocds[0].ads.length>0))
		{
			ad0 = resultMetaData.ocds[0].ads[0];
		}
		
	}
}
