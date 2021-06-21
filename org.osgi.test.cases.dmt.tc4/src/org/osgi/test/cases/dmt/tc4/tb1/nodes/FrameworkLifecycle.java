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
package org.osgi.test.cases.dmt.tc4.tb1.nodes;

import org.osgi.framework.BundleContext;
import org.osgi.service.dmt.MetaNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.InteriorMetaNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.InteriorNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.LeafNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;

public class FrameworkLifecycle extends InteriorNode {
	private LeafNode update;
	private LeafNode restart;
	private LeafNode shutdown;
	
	
	public FrameworkLifecycle(BundleContext context) {
		update = new Update(context);
		restart = new Restart(context);
		shutdown = new Shutdown(context);
	}

	
	@Override
	public void open() {
		update.open();
		restart.open();
		shutdown.open();
	}
	
	@Override
	public void close() {
		update.close();
		restart.close();
		shutdown.close();
	}
	
	@Override
	public String getNodeName() {
		return "FrameworkLifecycle";
	}
	
	@Override
	public String getNodePath() {
		return "./OSGi/_Framework/FrameworkLifecycle";
	}
	
	@Override
	public Node[] getChildNodes() {
		return new Node[] {update, restart, shutdown};
	}
	
	@Override
	public String[] getChildNodeNames() {
		return new String[] {update.getNodeName(), restart.getNodeName(), shutdown.getNodeName()};
	}
	
	@Override
	public MetaNode getMetaNode() {
		return new InteriorMetaNode() {
			@Override
			public boolean can(int operation) {
				if ( operation == MetaNode.CMD_GET ) {
					return true;
				}
				
				return false;
			}

			@Override
			public String getDescription() {
				return "Interior node that contains the values for Lifecycle control of the OSGi Framework.";
			}

			@Override
			public int getMaxOccurrence() {
				return 1;
			}

			@Override
			public int getScope() {
				return MetaNode.PERMANENT;
			}
		};
	}
}
