package org.osgi.impl.service.tr069todmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.osgi.framework.BundleContext;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.Uri;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 *
 */
public class PersistenceManager {

  private DmtSession session;
  private MappingTable mappingTable;
  private ArrayList<String> nodes;
  
  /**
   * @param session
   */
  public PersistenceManager(DmtSession session) {
    this.session = session;
    mappingTable = new MappingTable();
    nodes = new ArrayList<String>();
    loadNodeMappings(session.getRootUri());
  }
  
  private void loadNodeMappings(String nodeUri) {
    try {
      String currentNode = nodeUri;
      String nodeType = session.getNodeType(currentNode);
      String[] children = session.getChildNodeNames(nodeUri);
      if (DmtConstants.DDF_MAP.equals(nodeType) || DmtConstants.DDF_LIST.equals(nodeType)) {
        String instanceIDUri;
        for (int i = 0; i < children.length; i++) {
          currentNode = currentNode + Uri.PATH_SEPARATOR_CHAR + children[i];
          instanceIDUri = currentNode + Uri.PATH_SEPARATOR_CHAR + Utils.INSTANCE_ID;
          if (session.isNodeUri(instanceIDUri)) {
            mappingTable.put(currentNode, new Long(session.getNodeValue(instanceIDUri).getLong()));
          } else {
            loadNodeMappings(currentNode);
          }
        }
      } else {
        for (int i = 0; i < children.length; i++) {
          loadNodeMappings(currentNode + Uri.PATH_SEPARATOR_CHAR + children[i]);
        }
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }
  
  boolean isNodeUri(String nodeUri) {
    int index = nodes.indexOf(nodeUri);
    if (index == -1) {
      return session.isNodeUri(nodeUri);
    }
    return true;
  }
  
  void createInteriorNode(String nodeUri, boolean eager) throws DmtException {
    checkSessionLock();
    if (eager) {
      session.createInteriorNode(nodeUri);
    } else {
      nodes.add(nodeUri);
    }
  }

  void renameNode(String nodeUri, String newName) throws DmtException {
    checkSessionLock();
    session.renameNode(nodeUri, newName);
    int forwardSlashIndex = nodeUri.lastIndexOf(Uri.PATH_SEPARATOR_CHAR);
    mappingTable.rename(nodeUri, forwardSlashIndex == -1 ? newName : nodeUri.substring(0, forwardSlashIndex + 1).concat(newName));
  }
  
  void setNodeValue(String nodeUri, DmtData value) throws DmtException {
    checkSessionLock();
    if (!createNode(nodeUri, value)) {
      session.setNodeValue(nodeUri, value);
    }
  }

  private boolean createNode(String nodeUri, DmtData value) throws DmtException {
    if (session.isNodeUri(nodeUri)) {
      return false;
    }
    session.createLeafNode(nodeUri, value);
    nodes.remove(nodeUri);
    return true;
  }
  
  void deleteNode(String nodeUri) throws DmtException {
    checkSessionLock();
    int index = nodes.indexOf(nodeUri);
    if (index != -1) {
      nodes.remove(index);
    }
    session.deleteNode(nodeUri);
    mappingTable.remove(nodeUri);
  }
  
  int getInstanceNumber(String nodeUri) {
    return mappingTable.get(nodeUri).intValue();
  }
  
  int generateInstanceId(String nodeUri) throws DmtException {
    /*The Connector must ensure that any id chosen is not actually already in use or has been handed out recently*/
    String[] children = session.getChildNodeNames(nodeUri);
    if (children == null || children.length == 0) {
      return 1;
    }
    Comparator<String> comparator = new Comparator<String>() {

      public int compare(String s1, String s2) {
        try {
          int i1 = Integer.parseInt(s1);
          int i2 = Integer.parseInt(s2);
          if (i1 == i2) {
            return 0;
          }
          return i1 > i2 ? 1 : -1;
        } catch (NumberFormatException e) {
          return -s1.compareTo(s2);
        }
      }
    };
    Arrays.sort(children, 0, children.length, comparator);
    int res = Integer.parseInt(children[children.length - 1]);
    if (res == Integer.MAX_VALUE) {
      for (int i = 1, insert; i < Integer.MAX_VALUE; i++) {
        insert = Arrays.binarySearch(children, String.valueOf(i), comparator);
        if (insert < 0) {
          try {
            return Integer.parseInt(children[-insert - 2]) + 1;
          } catch (NumberFormatException e) {
            return 1;
          }
        }
      }
      throw new IllegalArgumentException("The maximum number of " + nodeUri + " children is reached!");
    } else {
      return ++res;
    }
  }
  
  private void checkSessionLock() throws TR069Exception {
    /*
     * If a non-atomic session is used then the TR069 Connector must not attempt to lazily create objects and reject any
     * addObject(String) and deleteObject(String) methods
     */

    if (session.getLockType() != DmtSession.LOCK_TYPE_ATOMIC) {
      throw new TR069Exception("Cannot add/delete objects and set parameter values in a non-atomic session");
    }
  }
  
  void close(BundleContext bc) {
    //TODO save the nodes and mappings
  }

}