package test;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.osgi.tools.btool.*;

public class DependenciesCheck extends TestCase {

	public static void main(String[] args) {
	}

	public void testDependencies() throws IOException {
		Manifest	manifest = new Manifest( getClass().getResourceAsStream("test.mf"));
		Source s = new ZipSource( new File("test/test.jar") );
		Vector v = new Vector();
		collect( v, s, "");
		Source cmjar = new ZipSource( new File("test/org.osgi.service.cm.zip"));
		Source fwjar = new ZipSource( new File("test/org.osgi.framework.jar"));
		Source eejar = new ZipSource( new File("test/ee.minimum.jar"));
		Vector	classpath = new Vector();
		classpath.add(fwjar);
		classpath.add(eejar);
		classpath.add(cmjar);
		
		Dependencies dep = new Dependencies(classpath, manifest,v,null,null);
		dep.calculate();
		System.out.println("Referred " + dep.getReferred() );
		System.out.println("Contained " + dep.getContained() );
	}
	
	void collect( Vector v, Source source, String path ) throws IOException {
		if ( source.isDirectory(path)) {
			String prefix = path.length()==0 ? path : path + "/";
			PackageResource p = new PackageResource(null,source,path);
			v.add(p );
			Collection subs = source.getResources(path);
			for ( Iterator i=subs.iterator(); i.hasNext(); ) {
				String s = (String) i.next();
				collect(v, source, s );
			}
			
		} else {
			Resource r = new Resource(null,source,path);
			v.add(r);
		}
	}

}
