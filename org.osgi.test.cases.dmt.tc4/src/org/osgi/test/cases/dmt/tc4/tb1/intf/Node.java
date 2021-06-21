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
package org.osgi.test.cases.dmt.tc4.tb1.intf;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;

public abstract class Node {
	private int version;
	private Date timestamp;
	private String title;
	
	protected static final long SERVICE_TIMER = 2000;
	
	
	public Node() {
		version = 0;
		timestamp = new Date();
		title = "";
	}
	
	
	public int getVersion() {
		return version;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void nodeChanged() {
		version++;
		timestamp = new Date();
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String data) {
		title = data;
	}

	
	public abstract void open();
	
	public abstract void close();
	
	public abstract boolean isLeaf();
	
	public abstract int getNodeSize();
	
	public abstract String getNodeName();

	public abstract String getNodePath();
	
	public abstract String getNodeType();
	
	public abstract DmtData getNodeValue();
	
	public abstract void setNodeValue(DmtData value) throws DmtException;
	
	public abstract Node[] getChildNodes();
	
	public abstract String[] getChildNodeNames();
	
	public abstract MetaNode getMetaNode();
}
