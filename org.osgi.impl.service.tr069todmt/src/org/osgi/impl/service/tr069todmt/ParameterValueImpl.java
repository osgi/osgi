
package org.osgi.impl.service.tr069todmt;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.tr069todmt.ParameterValue;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 *
 */
public class ParameterValueImpl implements ParameterValue {

	private Node	node;
	private String	value;
	private String	parameterName;

	/**
	 * @param parameterName
	 * @param node
	 * @throws TR069Exception
	 */
	public ParameterValueImpl(String parameterName, Node node) throws TR069Exception {
		this.parameterName = parameterName;
		this.node = node;
		value = Utils.getDmtValueAsString(node);
	}

	@Override
	public String getPath() {
		return parameterName;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public int getType() {
		try {
			return Utils.getTR069Type(node.getDmtValue(), node.getMimeTypes());
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

}
