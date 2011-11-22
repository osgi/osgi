package org.osgi.impl.service.tr069todmt;

import org.osgi.service.dmt.DmtException;

import org.osgi.service.tr069todmt.ParameterValue;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 *
 */
public class ParameterValueImpl implements ParameterValue {
  
  private Node node;
  private String parameterName;
  
  /**
   * @param parameterName 
   * @param node 
   */
  public ParameterValueImpl(String parameterName, Node node) {
    this.parameterName = parameterName;
    this.node = node;
  }

  public String getPath() {
    return parameterName;
  }
  
  public String getValue() {
    try {
      return Utils.getDmtValueAsString(node);
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }
  
  public int getType() {
    try {
      return Utils.getTR069Type(node.getDmtValue(), node.getMimeTypes());
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }
  
  
}
