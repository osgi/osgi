package org.osgi.test.cases.dmt.tc4.rfc141;

import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.DmtConstants;
import info.dmtree.spi.ExecPlugin;
import info.dmtree.spi.MountPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.osgi.test.support.OSGiTestCase;

public class TestBug1731_StringConstants extends OSGiTestCase{
	
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println( "tearing down");
	}
	
	/**
	 * tests existence of specified constants for String literals in info.dmtree.spi.DataPlugin
	 */
	public void testDataPluginConstants() throws Exception {
		checkStringConstant(DataPlugin.class, "DATA_ROOT_URIS", "dataRootURIs" );
	}
	
	/**
	 * tests existence of specified constants for String literals in info.dmtree.spi.ExecPlugin
	 */
	public void testExecPluginConstants() throws Exception {
		checkStringConstant(ExecPlugin.class, "EXEC_ROOT_URIS", "execRootURIs" );
	}
	
	/**
	 * tests existence of specified constants for String literals in info.dmtree.spi.MountPlugin
	 */
	public void testMountPluginConstants() throws Exception {
		checkStringConstant(MountPlugin.class, "MOUNT_POINTS", "mountPoints" );
	}

	/**
	 * tests existence of specified constants for String literals in info.dmtree.spi.DMTConstants
	 */
	public void testDMTConstants() throws Exception {
		checkStringConstant(DmtConstants.class, "DDF_LIST_SUBTREE", "org.osgi/1.0/ListSubtree" );
		checkStringConstant(DmtConstants.class, "DDF_SCAFFOLD", "org.osgi/1.0/ScaffoldNode" );

		checkStringConstant(DmtConstants.class, "EVENT_TOPIC_ADDED", "info/dmtree/DmtEvent/ADDED" );
		checkStringConstant(DmtConstants.class, "EVENT_TOPIC_DELETED", "info/dmtree/DmtEvent/DELETED" );
		checkStringConstant(DmtConstants.class, "EVENT_TOPIC_REPLACED", "info/dmtree/DmtEvent/REPLACED" );
		checkStringConstant(DmtConstants.class, "EVENT_TOPIC_RENAMED", "info/dmtree/DmtEvent/RENAMED" );
		checkStringConstant(DmtConstants.class, "EVENT_TOPIC_COPIED", "info/dmtree/DmtEvent/COPIED" );
		
		checkStringConstant(DmtConstants.class, "EVENT_PROPERTY_SESSION_ID", "session.id" );
		checkStringConstant(DmtConstants.class, "EVENT_PROPERTY_NODES", "nodes" );
		checkStringConstant(DmtConstants.class, "EVENT_PROPERTY_NEW_NODES", "newnodes" );
		checkStringConstant(DmtConstants.class, "EVENT_PROPERTY_LIST_NODES", "list.nodes" );
		checkStringConstant(DmtConstants.class, "EVENT_PROPERTY_LIST_UPCOMING_EVENT", "list.upcoming.event" );
		
	}

	
	/**
	 * helper method to check a given class for existence and correct type, modifiers and value of a given field name
	 * @param testClass ... the class to be checked
	 * @param name ... the name of the field
	 * @param value ... the value that the field should have
	 */
	private void checkStringConstant( Class testClass, String name, String value ) {
		Field field = null;
		try {
			field = testClass.getDeclaredField( name );
		} catch (NoSuchFieldException e) {
			fail( "Class '" + testClass.getName() + "' does not define a field of name " + name );
			return;
		}
		if ( ! String.class.equals( field.getType() )) {
			fail( "Field '" + testClass.getName() + "." + name + "' is not of type String" );
			return;
		}
		int mod = field.getModifiers();
		if ( ! Modifier.isFinal(mod) || ! Modifier.isPublic(mod) || ! Modifier.isStatic(mod) ) {
			fail( "Field '" + testClass.getName() + "." + name + "' is not 'public final static'" );
		}
		try {
			if ( ! value.equals( field.get(null))) {
				fail( "Field '" + testClass.getName() + "." + name + "' doesn't have the value '" + value + "'" );
				return;
			}
		} catch (IllegalAccessException e) {
			fail( "IllegalAccessException while checking value of field '" + name + "'" );
		}

	}
}
