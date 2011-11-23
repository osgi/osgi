package org.osgi.impl.service.tr069todmt;

import java.util.Arrays;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;

import java.text.ParseException;

import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.log.LogService;

import org.osgi.service.tr069todmt.ParameterInfo;
import org.osgi.service.tr069todmt.ParameterValue;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069Exception;

import org.osgi.impl.service.tr069todmt.encode.Base64;
import org.osgi.impl.service.tr069todmt.encode.HexBinary;


/**
 * 
 *
 */
public class TR069ConnectorImpl implements TR069Connector {
  
  private DmtSession session;
  private TR069ConnectorFactoryImpl factory;
  private PersistenceManager persistenceManager;
  
  /**
   * @param session
   * @param factory 
   */
  public TR069ConnectorImpl(DmtSession session, TR069ConnectorFactoryImpl factory) {
    this.session = session;
    this.factory = factory;
    persistenceManager = new PersistenceManager(factory, session);
  }

  public void setParameterValue(String parameterPath, String value, int type) throws TR069Exception {
    try {
      String nodeUri = toURI(parameterPath, true);
      if (nodeUri.endsWith(Utils.ALIAS)) {
          persistenceManager.renameNode(nodeUri.substring(0,  nodeUri.lastIndexOf(Uri.PATH_SEPARATOR_CHAR)), Uri.encode(value));
      } else {
        DmtData dmtValue = convertToDmtData(nodeUri, value, type, session.getMetaNode(nodeUri));
        if (dmtValue != null) {
          persistenceManager.setNodeValue(nodeUri, dmtValue);
        }
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }
  
  private DmtData convertToDmtData(String nodeUri, String value, int tr069Type, MetaNode metanode) {
    DmtData result = null;
    if (metanode == null) {
      switch(tr069Type) {
        case TR069_BOOLEAN: {
          result = convert(value, DmtData.FORMAT_BOOLEAN);
          if (result == null) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_BOOLEAN", TR069Exception.INVALID_PARAMETER_VALUE);
          }
          return result;
        }
        
        case TR069_INT:
        case TR069_UNSIGNED_INT: {
          result = convert(value, new int[] {DmtData.FORMAT_INTEGER, DmtData.FORMAT_LONG, DmtData.FORMAT_FLOAT});
          if (result == null) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_INTEGER, FORMAT_LONG and FORMAT_FLOAT", TR069Exception.INVALID_PARAMETER_VALUE);
          }
          return result;
        }
        
        case TR069_LONG:
        case TR069_UNSIGNED_LONG: {
          result = convert(value, new int[] {DmtData.FORMAT_LONG, DmtData.FORMAT_FLOAT, DmtData.FORMAT_INTEGER});
          if (result == null) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_LONG, FORMAT_FLOAT and FORMAT_INTEGER", TR069Exception.INVALID_PARAMETER_VALUE);
          }
          return result;
        }
        
        case TR069_HEXBINARY:
        case TR069_BASE64: {
          result = convert(value, new int[] {DmtData.FORMAT_BASE64, DmtData.FORMAT_BINARY, DmtData.FORMAT_RAW_BINARY});
          if (result == null) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_BASE64, FORMAT_BINARY, FORMAT_RAW_BINARY", TR069Exception.INVALID_PARAMETER_VALUE);
          }
          return result;
        }
        
        case TR069_STRING: {
          /* FORMAT_STRING, FORMAT_BOOLEAN, FORMAT_FLOAT, FORMAT_INTEGER, FORMAT_LONG, FORMAT_XML, LIST */
          result = convert(value, new int[] {DmtData.FORMAT_STRING, DmtData.FORMAT_BOOLEAN, DmtData.FORMAT_FLOAT, DmtData.FORMAT_INTEGER, DmtData.FORMAT_LONG, DmtData.FORMAT_XML});
          if (result == null && value.contains(Utils.COMMA)) {
            String[] nodes = value.split(Utils.COMMA);
            for (int i = 0; i < nodes.length; i++) {
              toURI(nodeUri + Uri.PATH_SEPARATOR_CHAR + Uri.encode(nodes[i]), true);
            }
            return null;
          }
          if (result == null) {
            throw new TR069Exception(
              "Error converting " + value + " in FORMAT_STRING, FORMAT_BOOLEAN, FORMAT_FLOAT, FORMAT_INTEGER, FORMAT_LONG, FORMAT_XML, LIST", 
              TR069Exception.INVALID_PARAMETER_VALUE
            );
          }
          return result;
        }
          
        case TR069_DATETIME: {
          result = convert(value, new int[] {DmtData.FORMAT_DATE_TIME, DmtData.FORMAT_DATE, DmtData.FORMAT_TIME});
          if (result == null) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_DATE_TIME, FORMAT_DATE and FORMAT_TIME", TR069Exception.INVALID_PARAMETER_VALUE);
          }
          return result;
        }
        default: throw new TR069Exception("Unknown TR069 type: " + tr069Type, TR069Exception.INVALID_PARAMETER_TYPE);
      }
    } else {
      result = convert(value, metanode.getFormat());
      if (result == null) {
        return convertToDmtData(nodeUri, value, tr069Type, null);
      }
      return result;
    }
  }
  
  private DmtData convert(String value, int[] formats) {
    DmtData result = null;
    for (int i = 0; i < formats.length; i++) {
      result = convert(value, formats[i]);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  private DmtData convert(String value, int format) {
    switch (format) {
      case DmtData.FORMAT_BASE64: {
        if (Utils.BASE64_PATTERN.matcher(value).matches()) {
          try {
            return new DmtData(Base64.decode(value.getBytes()), DmtData.FORMAT_BASE64);
          } catch (Exception e) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_BASE64", TR069Exception.INVALID_PARAMETER_VALUE);
          }
        }
        return null;
      }
      
      case DmtData.FORMAT_BINARY: {
        if (Utils.HEXBINARY_PATTERN.matcher(value).matches()) {
          return new DmtData(new HexBinary(value).binaryValue(), DmtData.FORMAT_BINARY);
        }
        return null;
      }
      
      case DmtData.FORMAT_RAW_BINARY: {
        return new DmtData(value.getBytes(), DmtData.FORMAT_RAW_BINARY);
      }
      
      case DmtData.FORMAT_BOOLEAN: {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1")) {
          return new DmtData(true);
        }
        if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0")) {
          return new DmtData(false);
        }
        return null;
      }

      case DmtData.FORMAT_INTEGER: {
        try {
          return new DmtData(Integer.parseInt(value));
        } catch (IllegalArgumentException e) {
          factory.log(LogService.LOG_WARNING, e.toString(), null);
          return null;
        }
      }

      case DmtData.FORMAT_LONG: {
        try {
          return new DmtData(Long.parseLong(value));
        } catch (IllegalArgumentException e) {
          factory.log(LogService.LOG_WARNING, e.toString(), null);
          return null;
        }
      }

      case DmtData.FORMAT_FLOAT: {
        try {
          return new DmtData(Float.parseFloat(value));
        } catch (IllegalArgumentException e) {
          factory.log(LogService.LOG_WARNING, e.toString(), null);
          return null;
        }
      }

      case DmtData.FORMAT_RAW_STRING:
      case DmtData.FORMAT_STRING:
      case DmtData.FORMAT_XML: {
        return new DmtData(value, format);
      }

      case DmtData.FORMAT_DATE:
      case DmtData.FORMAT_TIME: {
        Date date = null;
        try {
          date = (format == DmtData.FORMAT_DATE ? Utils.DMT_DATE_FORMAT : Utils.DMT_LOCAL_TIME_FORMAT).parse(value);
        } catch (ParseException e) {
          factory.log(LogService.LOG_WARNING, e.toString(), null);
          if (format == DmtData.FORMAT_TIME) {
            //try UTC time
            try {
              date = Utils.DMT_UTC_TIME_FORMAT.parse(value);
            } catch (ParseException e1) {
              factory.log(LogService.LOG_WARNING, e.toString(), null);
            }
          }
        }
        return date != null ? new DmtData(value, format) : null;
      }
      case DmtData.FORMAT_DATE_TIME: {
        if (Utils.TR069_UNKNOWN_TIME.equals(value)) {
          return new DmtData(new Date(0));
        }
        try {
          return new DmtData(Utils.TR069_DATE_ENCODING_FORMAT.parse(value));
        } catch (ParseException e) {
          factory.log(LogService.LOG_WARNING, e.toString(), null);
        }
        return null;
      }
    }
    throw new TR069Exception("Unknown format type " + format + " for " + value, TR069Exception.INVALID_PARAMETER_TYPE, null);
  }
  
  public ParameterValue getParameterValue(final String parameterPath) throws TR069Exception {
    if (parameterPath.endsWith(Utils.NUMBER_OF_ENTRIES)) {
      return new ParameterValue() {
        
        public String getValue() {
          try {
            String[] children = persistenceManager.getChildNodeNames(toURI(parameterPath.substring(0, parameterPath.indexOf(Utils.NUMBER_OF_ENTRIES)), true));
            if (children == null) {
              return "0";
            }
            return String.valueOf(children.length);
          } catch (DmtException e) {
            throw new TR069Exception(e);
          }
        }
        
        public int getType() {
          return TR069_UNSIGNED_INT;
        }
        
        public String getPath() {
          return parameterPath;
        }
      };
    } else if (parameterPath.endsWith(Utils.ALIAS)) {
      return new ParameterValue() {
        
        public String getValue() {
          String parentPath = toURI(parameterPath.substring(0, parameterPath.indexOf(Utils.ALIAS)), true);
          return parentPath.substring(parentPath.lastIndexOf(Uri.PATH_SEPARATOR) + 1);
        }
        
        public int getType() {
          return TR069_STRING;
        }
        
        public String getPath() {
          //TODO parameterPath or parent path should be returned here?!?
          return parameterPath;
        }
      };
    } else {
      return new ParameterValueImpl(parameterPath, new Node(toURI(parameterPath, true), session));
    }
  }

  public Collection<ParameterInfo> getParameterNames(String objectOrTablePath, boolean nextLevel) throws TR069Exception {
    ArrayList<ParameterInfo> result = new ArrayList<ParameterInfo>();
    addChildren(toURI(objectOrTablePath, true), result, nextLevel);
    return result;
  }
  
  private void addChildren(String parentUri, ArrayList<ParameterInfo> names, boolean nextLevel) {
    try {
      /* Any MAP and LIST node must include a ParameterInfo for the corresponding NumberOfEntries parameter */
      /* If the parent node is a MAP, then the synthetic Alias parameter must be included */
      if (DmtConstants.DDF_MAP.equals(persistenceManager.getNodeType(parentUri)) || DmtConstants.DDF_LIST.equals(persistenceManager.getNodeType(parentUri))) {
        String parentName = parentUri.substring(parentUri.lastIndexOf(Uri.PATH_SEPARATOR_CHAR) + 1);
        if (DmtConstants.DDF_MAP.equals(persistenceManager.getNodeType(parentUri))) {
          names.add(new ParameterInfoImpl(this, toPath(parentUri), new Node(parentUri + Uri.PATH_SEPARATOR + Utils.ALIAS, session)));
        }
        names.add(new ParameterInfoImpl(this, toPath(parentUri), new Node(parentUri + Uri.PATH_SEPARATOR + parentName + Utils.NUMBER_OF_ENTRIES, session)));
      }
      
      String[] children = persistenceManager.getChildNodeNames(parentUri);
      if (children == null || children.length == 0) {
        return;
      }
      for (int i = 0; i < children.length; i++) {
        /* If the child nodes have an InstanceId node then the returned names must include the InstanceId values instead of the node names */
        String nodeUri = parentUri + Uri.PATH_SEPARATOR + children[i];
        String instanceIDUri = nodeUri + Uri.PATH_SEPARATOR_CHAR + Utils.INSTANCE_ID;
        if (persistenceManager.isNodeUri(instanceIDUri)) {
          children[i] = Utils.getDmtValueAsString(new Node(instanceIDUri, session));
        }
        names.add(new ParameterInfoImpl(this, toPath(nodeUri), new Node(nodeUri, session)));
        if (!nextLevel) {
          addChildren(nodeUri, names, nextLevel);
        }
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }

  public String addObject(String path) throws TR069Exception {
    String nodeName = path.substring(path.lastIndexOf('.') + 1);
    if (Utils.ALIAS_PATTERN.matcher(nodeName).matches()) {
      /* If the path ends in an alias, then the node name must be the alias */
      return String.valueOf(persistenceManager.getInstanceNumber(
        toURI(path.substring(0, path.length() - nodeName.length() - 1) + Uri.PATH_SEPARATOR + nodeName.substring(1, nodeName.length() - 1), true)
      ));
    }
    try {
      String parentUri = toURI(path, true);
      String instanceNumber = String.valueOf(persistenceManager.generateInstanceId(parentUri));
      createNode(parentUri.concat(Uri.PATH_SEPARATOR).concat(instanceNumber), instanceNumber);
      return instanceNumber;
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }

  public void deleteObject(String objectPath) throws TR069Exception {
    try {
      String nodeUri = toURI(objectPath, false);
      if (new Node(nodeUri, session).isMultiInstanceNode()) {
        persistenceManager.deleteNode(nodeUri);
      }
    } catch (TR069Exception e) {
      if (e.getFaultCode() == TR069Exception.INVALID_PARAMETER_NAME) {
        /* A missing node must be ignored (toURI method throws INVALID_PARAMETER_NAME when create is false)*/
      } else {
        throw e;
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }

  public String toPath(String uri) throws TR069Exception {
    StringBuffer path = new StringBuffer();
    toPath(uri, path);
    return path.toString();
  }
  
  private void toPath(String uri, StringBuffer path) {
    if (uri == null || uri.length() == 0 || Utils.DOT.equals(uri)) {
      if (path.length() > 0) {
        path.deleteCharAt(0);
      }
      return;
    }
    int forwardSlashLastIndex = uri.lastIndexOf(Uri.PATH_SEPARATOR);
    if (forwardSlashLastIndex == -1) {
      uri = escape(uri);
      path.insert(0, uri).insert(0, Utils.DOT);
      return;
    }
    try {
      String parentUri = uri.substring(0, forwardSlashLastIndex);
      String segment = uri.substring(forwardSlashLastIndex + 1);
      if (Utils.ALIAS.equals(segment) || Utils.INSTANCE_ID.equals(segment)) {
        toPath(parentUri, path);
      } else {
        String nodeType = persistenceManager.getNodeType(parentUri);
        if (DmtConstants.DDF_MAP.equals(nodeType) || DmtConstants.DDF_LIST.equals(nodeType)) {
          int instanceNumber = persistenceManager.getInstanceNumber(uri);
          if (instanceNumber != -1) {
            path.insert(0, instanceNumber).insert(0, Utils.DOT);
            toPath(parentUri, path);
          } else {
            throw new TR069Exception("Dmt node " + uri + " does not exist!", TR069Exception.INVALID_PARAMETER_NAME);
          }
        } else {
          path.insert(0, segment).insert(0, Utils.DOT);
          toPath(parentUri, path);
        }
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }

  public String toURI(String path, boolean create) throws TR069Exception {
    StringBuffer uri = new StringBuffer(session.getRootUri());
    toUri(path, create, uri);
    return uri.toString();
  }
  
  private void toUri(String path, boolean create, StringBuffer uri) {
    if (path == null || path.length() == 0) {
      return;
    }
    try {
      String currentNode;
      int dotIndex = path.indexOf(Utils.DOT);
      if (dotIndex == -1) {
        path = unescape(path);
        uri.append(Uri.PATH_SEPARATOR_CHAR).append(path);
        currentNode = uri.toString();
        if (!persistenceManager.isNodeUri(currentNode)) {
          if (create) {
            createNode(currentNode, null);
          } else {
            throw new TR069Exception("Node " + currentNode + "does not exist!", TR069Exception.INVALID_PARAMETER_NAME);
          }
        }
        return;
      }
      String segment = path.substring(0, dotIndex);
      if (Utils.ALIAS_PATTERN.matcher(segment).matches()) {
        segment = segment.substring(1, segment.length() - 1);
      }
      segment = unescape(segment);
      String remainder = path.substring(dotIndex + 1);
      currentNode = uri.toString();
      /*check parent node*/
      if (!persistenceManager.isNodeUri(currentNode)) {
        if (create) {
          createNode(currentNode, null);
        } else {
          throw new TR069Exception("Node " + currentNode + "does not exist!", TR069Exception.INVALID_PARAMETER_NAME);
        }
      }
      String nodeType = persistenceManager.getNodeType(currentNode);
      if ((DmtConstants.DDF_MAP.equals(nodeType) || DmtConstants.DDF_LIST.equals(nodeType)) && Utils.INSTANCE_ID_PATTERN.matcher(segment).matches()) {
        String[] children = persistenceManager.getChildNodeNames(currentNode);
        long instanceID = Long.parseLong(segment);
        String instanceIDUri;
        for (int i = 0; i < children.length; i++) {
          currentNode = currentNode + Uri.PATH_SEPARATOR_CHAR + children[i];
          instanceIDUri = currentNode + Uri.PATH_SEPARATOR_CHAR + Utils.INSTANCE_ID;
          if (persistenceManager.isNodeUri(instanceIDUri)) {
            if (session.getNodeValue(instanceIDUri).getLong() == instanceID) {
              uri.append(Uri.PATH_SEPARATOR_CHAR).append(children[i]);
              toUri(remainder, create, uri);
              return;
            }
          }
        }
      }
      uri.append(Uri.PATH_SEPARATOR_CHAR).append(segment);
      currentNode = uri.toString();
      if (!persistenceManager.isNodeUri(currentNode)) {
        if (create) {
          createNode(currentNode, null);
          toUri(remainder, create, uri);
        } else {
          throw new TR069Exception("Node " + currentNode + "does not exist!", TR069Exception.INVALID_PARAMETER_NAME);
        }
      } else {
        toUri(remainder, create, uri);
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }

  private void createNode(String nodeUri, String instanceNumber) throws DmtException {
    if (persistenceManager.isNodeUri(nodeUri)) {
      return;
    }
    MetaNode metanode = session.getMetaNode(nodeUri);
    String[] mimeTypes = metanode == null ? null : metanode.getMimeTypes();
    persistenceManager.createInteriorNode(
      nodeUri, instanceNumber, mimeTypes == null ? false : Arrays.asList(mimeTypes).contains(TR069Connector.TR069_MIME_EAGER)
    );
  }
  
  private String unescape(String s) {
    StringBuffer sb = new StringBuffer(s);
    Matcher matcher = Utils.THORN_ESCAPE.matcher(sb);
    int rover = 0;
    while (matcher.find(rover)) {
      int unicode = Integer.parseInt(matcher.group(1), 16);
      sb.delete(matcher.start(), matcher.end());
      sb.insert(matcher.start(), (char) unicode);
      rover = matcher.start() + 1;
    }
    return sb.toString();
  }
  
  /*
    NAME ::= (Letter | ’_’ )( Letter | Digit | ’-’ | ’_’ | CombiningChar | Extender)*
  
    TR-106: Parameter names MUST be treated as case sensitive. The name of each node in the hierarchy MUST start with a letter or underscore, 
    and subsequent characters MUST be letters, digits, underscores or hyphens.
    
    CombiningChar, Extender and first non-letter and non-underscore symbols MUST be escaped
   */
  private String escape(String s) {
    StringBuffer sb = new StringBuffer(s);
    char c;
    for (int i = 0; i < sb.length();) {
      c = sb.charAt(i);
      if (i == 0 && !(Character.isLetter((int)c) || (c == Utils.UNDERSCORE_CODE))) {
        i = thornEscape(sb, i, c);
      } else if (Character.isWhitespace((int)c) || Utils.CHARS_TO_ESCAPE_PATTERN.matcher(String.valueOf(c)).matches()) {
          i = thornEscape(sb, i, c);
      } else {
        i++;
      }
    }
    return sb.toString();
  }
 
  private int thornEscape(StringBuffer sb, int index, char c) {
    sb.insert(index++, Utils.THORN);
    sb.insert(index, String.format("%4H", c).replace(' ', '0'));
    sb.deleteCharAt(index+=4);
    return index;
  }

  public void close() {
    /* Closing the connector must not close the corresponding DmtSession */
    try {
      persistenceManager.close();
    } catch (Exception e) {
      factory.log(LogService.LOG_ERROR, null, e);
    }
  }
  
}
