/*
 * Created on Jun 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package test;

import java.io.*;
import junit.framework.TestCase;
import org.osgi.tools.btool.*;

/**
 * @author Peter Kriens
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class SourceCheck extends TestCase {
	public static void main(String[] args) {
	}

	public void testZipSource() throws IOException {
		Source s = new ZipSource(new File("test/test.jar"));
		System.out.println(s.getResources("org/osgi/service/cm"));
		System.out.println(s.getResources("org/osgi/service"));
		System.out.println(s.getResources("org/osgi"));
	}
}
