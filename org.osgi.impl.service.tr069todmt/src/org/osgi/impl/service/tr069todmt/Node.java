package org.osgi.impl.service.tr069todmt;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 *
 */
public class Node {
  
  private static final String FAKE_NODE_NAME = "Fake";
  
  private String uri;
  private DmtSession session;
  private MetaNode metanode;
  
  /**
   * @param uri 
   * @param session 
   */
  public Node(String uri, DmtSession session) {
    this.uri = uri;
    this.session = session;
    try {
      metanode = session.getMetaNode(uri);
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }
  
  DmtData getDmtValue() throws DmtException {
    return session.getNodeValue(uri);
  }
  
  String[] getMimeTypes() {
    return getMimeTypes(metanode);
  }
   
  private String[] getMimeTypes(MetaNode meta) {
    if (meta == null) {
      return null;
    }
    return meta.getMimeTypes();
  }
  
  String[] getChildrenNames() throws DmtException {
    return session.getChildNodeNames(uri); 
  }
  
  DmtData getLeafValue(String leafName) throws DmtException {
    String leafUri = makeUri(uri, leafName);
    MetaNode leafMeta = session.getMetaNode(leafUri);
    if ((leafMeta == null && session.isLeafNode(leafUri)) || leafMeta.isLeaf()) {
      return session.getNodeValue(leafUri);
    }
    throw new IllegalArgumentException("Node: " + leafUri + " is not a leaf node!"); 
  }
  
  Node getChildNode(String childName) {
    return new Node(makeUri(uri, childName), session);
  }
  
  private String makeUri(String parentUri, String nodeName) {
    return parentUri +  Uri.PATH_SEPARATOR + Uri.encode(nodeName);
  }
  
  boolean isLeaf() {
    try {
      return metanode == null ? session.isLeafNode(uri) : metanode.isLeaf();
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }
  
  boolean isMultiInstanceParent() {
    return isMultiInstanceParent(uri);
  }
  
  //TODO to check if only nodes with type DmtConstants.DDF_LIST and DmtConstants.DDF_MAP are multi instance parents
  private boolean isMultiInstanceParent(String nodeUri) {
    try {
      String nodeType = session.getNodeType(nodeUri);
      return DmtConstants.DDF_LIST.equals(nodeType) || DmtConstants.DDF_MAP.equals(nodeType);
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }
  
  //TODO to see if only these nodes are multi instances
  boolean isMultiInstanceNode() {
    if (metanode == null) {
      /* Check if the parent node is multi instance parent*/
      return isMultiInstanceParent(uri.substring(uri.lastIndexOf(Uri.PATH_SEPARATOR_CHAR) + 1));
    }
    return metanode.getMaxOccurrence() > 0;
  }
  
  boolean canAddChild() {
    try {
      String[] children = getChildrenNames();
      if (children == null || children.length == 0) {
        /* try to get a fake node meta */
        MetaNode fakeNodeMeta = session.getMetaNode(makeUri(uri, FAKE_NODE_NAME));
        if (fakeNodeMeta == null) {
          return true;
        }
        return fakeNodeMeta.can(MetaNode.CMD_ADD);
      } else {
        MetaNode childMeta;
        for (int i = 0; i < children.length; i++) {
          childMeta = session.getMetaNode(makeUri(uri, children[i]));
          if (childMeta != null && childMeta.can(MetaNode.CMD_ADD)) {
            return true;
          }
        }
        return false;
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }
  
  boolean can(int operation) {
    if (metanode == null) {
      /*If no meta-data is provided for a node, all operations are valid*/
      return true;
    }
    return metanode.can(operation);
  }
}
