
package org.osgi.impl.service.tr069todmt;

import org.osgi.service.dmt.MetaNode;
import org.osgi.service.tr069todmt.ParameterInfo;
import org.osgi.service.tr069todmt.ParameterValue;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 * 
 *
 */
public class ParameterInfoImpl implements ParameterInfo {

	private Node			node;
	private String			parameterName;
	private TR069Connector	connector;

	/**
	 * @param connector
	 * @param parameterName
	 * @param node
	 */
	public ParameterInfoImpl(TR069Connector connector, String parameterName, Node node) {
		this.connector = connector;
		this.parameterName = parameterName;
		this.node = node;
	}

	@Override
	public String getPath() {
		return parameterName;
	}

	@Override
	public boolean isWriteable() {
		if (isParameter()) {
			return node.can(MetaNode.CMD_REPLACE);
		}
		if (node.isMultiInstanceParent()) {
			return node.canAddChild();
		}
		if (node.isMultiInstanceNode()) {
			return node.can(MetaNode.CMD_DELETE);
		}
		return false;
	}

	@Override
	public boolean isParameter() {
		return node.isLeaf();
	}

	@Override
	public ParameterValue getParameterValue() throws TR069Exception {
		return connector.getParameterValue(getPath());
	}

	@Override
	public String toString() {
		return parameterName;
	}
}
