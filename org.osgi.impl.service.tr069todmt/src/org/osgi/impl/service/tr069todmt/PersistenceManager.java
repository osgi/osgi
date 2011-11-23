package org.osgi.impl.service.tr069todmt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.Uri;
import org.osgi.service.log.LogService;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 *
 */
public class PersistenceManager {
  
  private static final String TEMP_TREE_FILE_PREFIX = "tree";

  private DmtSession session;
  private TR069ConnectorFactoryImpl factory;
  private MappingTable mappingTable;
  //TODO should it be global?!?
  private TreeSet<String> tree;
  
  /**
   * @param factory 
   * @param session
   */
  public PersistenceManager(TR069ConnectorFactoryImpl factory, DmtSession session) {
    this.factory = factory;
    this.session = session;
    mappingTable = new MappingTable();
    tree = new TreeSet<String>();
    try {
      load();
    } catch (Exception e) {
      factory.log(LogService.LOG_WARNING, "PersistenceManager cannot load mappings", e);
    }
  }
  
//  private void loadNodeMappings(String nodeUri) {
//    try {
//      String currentNode = nodeUri;
//      String nodeType = session.getNodeType(currentNode);
//      String[] children = session.getChildNodeNames(nodeUri);
//      if (DmtConstants.DDF_MAP.equals(nodeType) || DmtConstants.DDF_LIST.equals(nodeType)) {
//        String instanceIDUri;
//        for (int i = 0; i < children.length; i++) {
//          currentNode = currentNode + Uri.PATH_SEPARATOR_CHAR + children[i];
//          instanceIDUri = currentNode + Uri.PATH_SEPARATOR_CHAR + Utils.INSTANCE_ID;
//          if (session.isNodeUri(instanceIDUri)) {
//            mappingTable.put(currentNode, new Long(session.getNodeValue(instanceIDUri).getLong()));
//          } else {
//            loadNodeMappings(currentNode);
//          }
//        }
//      } else {
//        for (int i = 0; i < children.length; i++) {
//          loadNodeMappings(currentNode + Uri.PATH_SEPARATOR_CHAR + children[i]);
//        }
//      }
//    } catch (DmtException e) {
//      throw new TR069Exception(e);
//    }
//  }
  
  boolean isNodeUri(String nodeUri) {
    if (tree.contains(nodeUri)) {
      return true;
    }
    return session.isNodeUri(nodeUri);
  }
  
  void createInteriorNode(String nodeUri, String instanceNumber, boolean eager) throws DmtException {
    checkSessionLock();
    if (eager) {
      session.createInteriorNode(nodeUri);
      if (tree.contains(nodeUri)) {
        tree.remove(nodeUri);
      }
    } else {
      tree.add(nodeUri);
    }
    if (instanceNumber != null) {
      mappingTable.put(nodeUri, Long.parseLong(instanceNumber));
    }
  }

  String getNodeType(String nodeUri) throws DmtException {
    if (tree.contains(nodeUri)) { 
      return null;
    }
    return session.getNodeType(nodeUri);
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
    tree.remove(nodeUri);
    return true;
  }
  
  void deleteNode(String nodeUri) throws DmtException {
    checkSessionLock();
    if (session.isNodeUri(nodeUri)) {
      session.deleteNode(nodeUri);
    }
    if (tree.contains(nodeUri)) {
      tree.remove(nodeUri);
    }
    mappingTable.remove(nodeUri);
  }
  
  int getInstanceNumber(String nodeUri) {
    return mappingTable.get(nodeUri).intValue();
  }
  
  int generateInstanceId(String nodeUri) throws DmtException {
    /*The Connector must ensure that any id chosen is not actually already in use or has been handed out recently*/
    String[] children = getChildNodeNames(nodeUri);
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
  
  String[] getChildNodeNames(String nodeUri) throws DmtException {
    String[] nodes = tree.toArray(new String[tree.size()]);
    ArrayList<String> children = new ArrayList<String>();
    String nodeUriPrefix = nodeUri.concat(Uri.PATH_SEPARATOR);
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].startsWith(nodeUriPrefix)) {
        String name = nodes[i].substring(nodeUriPrefix.length());
        if (!name.contains(Uri.PATH_SEPARATOR)) {
          children.add(name);
        }
      }
    }
    //TODO to check if nodes duplication is possible
    if (session.isNodeUri(nodeUri)) {
      children.addAll(Arrays.asList(session.getChildNodeNames(nodeUri)));
    }
    return children.toArray(new String[children.size()]); 
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
  
  private void load() throws Exception {
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(
      factory.context.getDataFile(TEMP_TREE_FILE_PREFIX + session.getRootUri().replaceAll(Uri.PATH_SEPARATOR, Utils.DOT))
    ));
    tree = (TreeSet<String>)in.readObject();
    mappingTable = (MappingTable)in.readObject();
    in.close();
  }
  
  
  void close() throws Exception {
    //TODO save the tree and mappings
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
      factory.context.getDataFile(TEMP_TREE_FILE_PREFIX + session.getRootUri().replaceAll(Uri.PATH_SEPARATOR, Utils.DOT))
    ));
    out.writeObject(tree);
    out.writeObject(mappingTable);
    out.close();
  }

}