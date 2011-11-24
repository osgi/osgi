package org.osgi.impl.service.tr069todmt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
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
  
  private static final String TEMP_TREE_FILE = "tree.dat";

  private TR069ConnectorFactoryImpl factory;
  private MappingTable mappingTable;
  private SortedSet<String> tree;
  
  /**
   * @param factory 
   */
  public PersistenceManager(TR069ConnectorFactoryImpl factory) {
    this.factory = factory;
    mappingTable = new MappingTable();
    tree = Collections.synchronizedSortedSet(new TreeSet<String>());
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
  
  boolean isNodeUri(DmtSession session, String nodeUri) {
    if (tree.contains(nodeUri)) {
      return true;
    }
    return session.isNodeUri(nodeUri);
  }
  
  void createInteriorNode(DmtSession session, String nodeUri, boolean eager) throws DmtException {
    checkSessionLock(session);
    if (eager) {
      session.createInteriorNode(nodeUri);
      if (tree.contains(nodeUri)) {
        tree.remove(nodeUri);
      }
    } else {
      tree.add(nodeUri);
    }
  }

  String getNodeType(DmtSession session, String nodeUri) throws DmtException {
    if (tree.contains(nodeUri)) { 
      return null;
    }
    return session.getNodeType(nodeUri);
  }
  
  void renameNode(DmtSession session, String nodeUri, String newName) throws DmtException {
    checkSessionLock(session);
    session.renameNode(nodeUri, newName);
    int forwardSlashIndex = nodeUri.lastIndexOf(Uri.PATH_SEPARATOR_CHAR);
    
    String parentUri = forwardSlashIndex == -1 ? "" : nodeUri.substring(0, forwardSlashIndex + 1);
    String newUri = parentUri.concat(newName);
    
    mappingTable.rename(nodeUri, newUri);
    
    String[] nodes = tree.toArray(new String[tree.size()]);
    String oldPath = nodeUri.concat(Uri.PATH_SEPARATOR);
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].startsWith(oldPath) || nodes[i].equals(nodeUri)) {
        tree.remove(nodes[i]);
        tree.add(nodes[i].replace(nodeUri, newUri));
      }
    }
    String instanceNumber = parentUri.length() == 0 ? nodeUri : nodeUri.substring(forwardSlashIndex + 1);
    try {
      Long instance = mappingTable.get(nodeUri);
      mappingTable.put(newUri, instance != null ? instance : Long.parseLong(instanceNumber));
    } catch (NumberFormatException e) {
      factory.log(LogService.LOG_DEBUG, "The node name is not an instance number, but an alias", e);
    }
  }
  
  void setNodeValue(DmtSession session, String nodeUri, DmtData value) throws DmtException {
    checkSessionLock(session);
    if (!createNode(session, nodeUri, value)) {
      session.setNodeValue(nodeUri, value);
    }
  }

  private boolean createNode(DmtSession session, String nodeUri, DmtData value) throws DmtException {
    if (session.isNodeUri(nodeUri)) {
      return false;
    }
    session.createLeafNode(nodeUri, value);
    tree.remove(nodeUri);
    return true;
  }
  
  void deleteNode(DmtSession session, String nodeUri) throws DmtException {
    checkSessionLock(session);
    if (session.isNodeUri(nodeUri)) {
      session.deleteNode(nodeUri);
    }
    
    /*remove the whole subtree*/
    String[] nodes = tree.toArray(new String[tree.size()]);
    String prefix = nodeUri.concat(Uri.PATH_SEPARATOR);
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].startsWith(prefix) || nodes[i].equals(nodeUri)) {
        tree.remove(nodes[i]);
      }
    }
    
    mappingTable.remove(nodeUri);
  }
  
  int getInstanceNumber(DmtSession session, String nodeUri) throws DmtException {
    Long mapping = mappingTable.get(nodeUri);
    if (mapping == null) {
      String instanceIDUri = nodeUri + Uri.PATH_SEPARATOR_CHAR + Utils.INSTANCE_ID;
      if (isNodeUri(session, instanceIDUri)) {
        mapping = new Long(session.getNodeValue(instanceIDUri).getLong());
        mappingTable.put(nodeUri, mapping);
      } else {
        return -1;
      }
    }
    return mapping.intValue();
  }
  
  int generateInstanceId(DmtSession session, String nodeUri) throws DmtException {
    /*The Connector must ensure that any id chosen is not actually already in use or has been handed out recently*/
    String[] children = getChildNodeNames(session, nodeUri, false);
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
    String last = children[children.length - 1];
    int res;
    try {
      res = Integer.parseInt(last);
    } catch (NumberFormatException e) {
      return 1;
    }
    
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
  
  String[] getChildNodeNames(DmtSession session, String nodeUri, boolean aliases) throws DmtException {
    String[] nodes = tree.toArray(new String[tree.size()]);
    ArrayList<String> children = new ArrayList<String>();
    String nodeUriPrefix = nodeUri.concat(Uri.PATH_SEPARATOR);
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].startsWith(nodeUriPrefix)) {
        String name = nodes[i].substring(nodeUriPrefix.length());
        if (!name.contains(Uri.PATH_SEPARATOR)) {
          if (aliases) {
            children.add(name);
          } else {
            Long instance = mappingTable.get(nodes[i]);
            //TODO to see if here should be created a mapping instance
            children.add(instance == null ? name : instance.toString());
          }
        }
      }
    }
    //TODO to check if nodes duplication is possible
    if (session.isNodeUri(nodeUri)) {
      children.addAll(Arrays.asList(session.getChildNodeNames(nodeUri)));
    }
    return children.toArray(new String[children.size()]); 
  }
  
  private void checkSessionLock(DmtSession session) throws TR069Exception {
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
      factory.context.getDataFile(TEMP_TREE_FILE)
    ));
    tree = (TreeSet<String>)in.readObject();
    mappingTable = (MappingTable)in.readObject();
    in.close();
  }
  
  
  void close() throws Exception {
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
      factory.context.getDataFile(TEMP_TREE_FILE)
    ));
    out.writeObject(tree);
    out.writeObject(mappingTable);
    out.close();
  }

}