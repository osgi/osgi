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

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.test.cases.dmt.tc4.tb1.intf.BaseMetaNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.LeafNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;

public class Shutdown extends LeafNode {
	private BundleContext ctx;
	
	
	public Shutdown(BundleContext context) {
		ctx = context;
	}
	
	
	@Override
	public void open() {
		// Nothing to do
	}
	
	@Override
	public void close() {
		// Nothing to do
	}
	
	@Override
	public String getNodeName() {
		return "Shutdown";
	}
	
	@Override
	public String getNodeType() {
		return "application/vnd.osgi.tr-069;type=boolean";
	}
	
	@Override
	public String getNodePath() {
		return "./OSGi/_Framework/FrameworkLifecycle/Shutdown";
	}
	
	@Override
	public DmtData getNodeValue() {
		// Always false when read
		return new DmtData(false);
	}
	
	@Override
	public void setNodeValue(DmtData value) throws DmtException {
		if (value.getBoolean() == true) {
			Bundle b = ctx.getBundle(0);
			try {
				b.stop();
				nodeChanged();
			} catch (BundleException e) {
				throw new DmtException(getNodePath(), DmtException.COMMAND_FAILED, e.getMessage());
			}
		}
	}

	@Override
	public Node[] getChildNodes() {
		return new Node[0];
	}

	@Override
	public String[] getChildNodeNames() {
		return new String[0];
	}
	
	@Override
	public MetaNode getMetaNode() {
		return new BaseMetaNode() {
			@Override
			public boolean can(int operation) {
				if ( operation == MetaNode.CMD_GET ) {
					return true;
				} else if ( operation == MetaNode.CMD_REPLACE ) {
					return true;
				}
				
				return false;
			}

			@Override
			public DmtData getDefault() {
				return new DmtData(false);
			}

			@Override
			public String getDescription() {
				return "A leaf  node used to shutdown the OSGi Framework. This node is writable to set the shutdown command. If this node value is replaced with 'TRUE',  the Framework sub-tree must shutdown the OSGi Framework.";
			}

			@Override
			public int getFormat() {
				return DmtData.FORMAT_BOOLEAN;
			}

			@Override
			public double getMax() {
				return 1;
			}

			@Override
			public int getMaxOccurrence() {
				return 1;
			}

			@Override
			public String[] getMimeTypes() {
				return new String[] { "x-osgi/tr069.boolean" };
			}

			@Override
			public double getMin() {
				return 0;
			}

			@Override
			public String[] getRawFormatNames() {
				return null;
			}

			@Override
			public int getScope() {
				return MetaNode.PERMANENT;
			}

			@Override
			public boolean isValidName(String name) {
				return true;
			}

			@Override
			public boolean isValidValue(DmtData value) {
				return true;
			}
		};
	}
}
