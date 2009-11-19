package test.xmldoclet;

import javax.xml.parsers.*;

import junit.framework.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import com.sun.tools.javadoc.*;

public class XmlDocletTest extends TestCase {

	public void testSimple() throws Exception {
		Main.main(new String[] {
				"-doclet",
				"org.osgi.tools.xmldoclet.XmlDoclet", /**/ 
				"-docletpath", "bin", /**/
				"-sourcepath", "src", /**/
				"test.xmldoclet.sample", });
		
		Document doc = doc();
	}
	
	
	
    public void assertAttribute(Document doc, String value, String tag,
            String attribute) {
        assertAttribute(doc,value, tag, 0, attribute);
    }
    public void assertAttribute(Document doc, String value, String tag,
            int index, String attribute) {
        assertEquals(value, doc.getElementsByTagName(tag).item(index)
                .getAttributes().getNamedItem(attribute).getNodeValue());
    }

    Document doc() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource("javadoc.xml"));
        return doc;
    }


}
