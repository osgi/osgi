/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.dmt.tc4.rfc141;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.test.support.OSGiTestCase;

public class TestBug1731_StringConstants extends OSGiTestCase{
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println( "tearing down");
	}
	
	/**
	 * tests existence of specified constants for String literals in org.osgi.service.dmt.spi.DataPlugin
	 */
	public void testDataPluginConstants() throws Exception {
		assertConstant("dataRootURIs", "DATA_ROOT_URIS", DataPlugin.class );
	}
	
	/**
	 * tests existence of specified constants for String literals in org.osgi.service.dmt.spi.ExecPlugin
	 */
	public void testExecPluginConstants() throws Exception {
		assertConstant( "execRootURIs", "EXEC_ROOT_URIS", ExecPlugin.class);
	}
	
	/**
	 * tests existence of specified constants for String literals in org.osgi.service.dmt.spi.MountPlugin
	 */
	public void testMountPluginConstants() throws Exception {
		assertConstant( "mountPoints", "MOUNT_POINTS", DataPlugin.class );
		assertConstant( "mountPoints", "MOUNT_POINTS", ExecPlugin.class );
	}

	/**
	 * tests existence of specified constants for String literals in org.osgi.service.dmt.spi.DMTConstants
	 */
	public void testDMTConstants() throws Exception {
		assertConstant( "org.osgi/1.0/LIST", "DDF_LIST", DmtConstants.class );
		assertConstant( "org.osgi/1.0/MAP", "DDF_MAP", DmtConstants.class );
		assertConstant( "org.osgi/1.0/SCAFFOLD", "DDF_SCAFFOLD", DmtConstants.class );

		assertConstant("org/osgi/service/dmt/DmtEvent/ADDED",
				"EVENT_TOPIC_ADDED", DmtConstants.class);
		assertConstant("org/osgi/service/dmt/DmtEvent/DELETED",
				"EVENT_TOPIC_DELETED", DmtConstants.class);
		assertConstant("org/osgi/service/dmt/DmtEvent/REPLACED",
				"EVENT_TOPIC_REPLACED", DmtConstants.class);
		assertConstant("org/osgi/service/dmt/DmtEvent/RENAMED",
				"EVENT_TOPIC_RENAMED", DmtConstants.class);
		assertConstant("org/osgi/service/dmt/DmtEvent/COPIED",
				"EVENT_TOPIC_COPIED", DmtConstants.class);
		assertConstant("org/osgi/service/dmt/DmtEvent/SESSION_OPENED",
				"EVENT_TOPIC_SESSION_OPENED", DmtConstants.class);
		assertConstant("org/osgi/service/dmt/DmtEvent/SESSION_CLOSED",
				"EVENT_TOPIC_SESSION_CLOSED", DmtConstants.class);
		
		assertConstant( "session.id", "EVENT_PROPERTY_SESSION_ID", DmtConstants.class );
		assertConstant( "nodes", "EVENT_PROPERTY_NODES", DmtConstants.class );
		assertConstant( "newnodes", "EVENT_PROPERTY_NEW_NODES", DmtConstants.class );
		//assertConstant( "list.nodes", "EVENT_PROPERTY_LIST_NODES", DmtConstants.class );
		//assertConstant( "list.upcoming.event", "EVENT_PROPERTY_LIST_UPCOMING_EVENT", DmtConstants.class );
		
	}
}
