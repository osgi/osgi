package org.osgi.test.cases.command;

import java.util.*;

import junit.framework.*;

import org.osgi.framework.*;
import org.osgi.service.command.annotations.*;
import org.osgi.service.converter.*;
import org.osgi.service.impl.command.*;

public class MethodMatchingTest extends TestCase {
	BundleContext context = FrameworkUtil.getBundle(MethodMatchingTest.class).getBundleContext();
	Converter converter = getService(Converter.class);
	
	/**
	 * Matching methods
	 */
	
	public class MatchingCommands {
		public String foo() { return "foo()"; }
		public String foo(String abc) { return "foo("+abc+")"; }
		public String foo(String ...abc) { return "foo("+Arrays.toString(abc)+")"; }
	}	
	MetaScopeImpl<MatchingCommands> mscope = new MetaScopeImpl<MatchingCommands>("matching", new String[] {}, new MatchingCommands());
	
	public void testMatching() throws Exception {
		assertEquals("foo([abc,def])", mscope.execute("foo", null, converter,"abc", "def")); // no arguments
		assertEquals("foo()", mscope.execute("foo", null, converter)); // no arguments
		assertEquals("foo(abc)", mscope.execute("foo", null, converter,"abc")); // no arguments
	}

	



	/**
	 * Basic stuff
	 * 
	 */
	public class BasicCommands {
		String what;
		
		@Description(value="Hello world", summary="Summary")
		public void hello() { what = "hello"; } 
		
		@Description(value="This is oneArg", summary="One Arg summary")
		public void oneArg(
				@Description("-f description")
				@Parameter(alias="-f", repeat=true) Collection<String> s,
				@Description("-xf description")
				@Parameter(alias={"-x","--asdadasd"}, absent="y") String y
				) { what = "s"; } 
		public void setArg(String s) { what = "s"; } 
	}
	MetaScopeImpl<BasicCommands> scope = new MetaScopeImpl<BasicCommands>("basic", new String[] {"oneArg"}, new BasicCommands());
	
	
	
	public void testSimple() throws Exception {
		System.out.println(scope);
		
	}

	
	 
	private <T>  T getService(Class<T> c) {
		ServiceReference ref = context.getServiceReference(c.getName());
		return c.cast( context.getService(ref));
	}

}
