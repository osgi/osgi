/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.deploymentadmin.perm;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;



class Representation {
    
    public class Node {

    	private static final int ROOT  = 0;
        private static final int LEAF  = 1; 
        private static final int AND   = 2;
        private static final int OR    = 3;
        private static final int NOT   = 4;
        
        // only in interior nodes
        private int                op;
        private Vector             args = new Vector();
        
        // only in leaf nodes
        private SignerChainPattern chainPattern;
        private NamePattern        namePattern;
        private String[]           leafValue;
       
        void setLeaf (String attr, String value) {
            leafValue = new String[] {attr, value};
        }
        
        void setOp(int op) {
            this.op = op;    
        }
        
        void setSignerChainPattern(String str) {
            chainPattern = new SignerChainPattern(str);    
        }
        
        void setNamePattern(String str) {
            namePattern = new NamePattern(str);    
        }
        
        void add(Node child) {
            args.add(child);
        }
        
        boolean match(Hashtable ht) {
            switch (op) {
                case ROOT: {
                    return ((Node) args.get(0)).match(ht);
                }
                case LEAF: {
                    String attr = leafValue[0];
                    if ("name".equals(attr))
                        return namePattern.match((String) ht.get(attr));
                    else if ("signer".equals(attr)) 
                        return chainPattern.match((String) ht.get(attr));
                    else 
                        return false;
                }
                case AND: {
                    boolean ret = true;
                    for (Iterator iter = args.iterator(); iter.hasNext();) {
                        Node node = (Node) iter.next();
                        ret = ret && node.match(ht);
                    }
                    return ret;
                }
                case OR: {
                    boolean ret = false;
                    for (Iterator iter = args.iterator(); iter.hasNext();) {
                        Node node = (Node) iter.next();
                        ret = ret || node.match(ht);
                    }
                    return ret;
                }
                case NOT: {
                    return !((Node) args.get(0)).match(ht);
                }
                default:
                    return false;
            }
        }
    }

    private Node      rootNode;
    private Hashtable attrTable = new Hashtable();

    private char[]    filter;
    private int       index;
    
    public boolean equals(Object o) {
        if (null == o)
            return false;
        if (!(o instanceof Representation))
            return false;
        Representation other = (Representation) o;
        
        return new String(filter).equals(new String(other.filter));
    }
    
    public int hashCode() {
        return new String(filter).hashCode();
    }
    
    Representation(String filter) {
        this.filter = filter.toCharArray();
        rootNode = new Node();
        rootNode.setOp(Node.ROOT);
        parse_filter(rootNode);
        if (index < this.filter.length)
            throwException();
    }
    
    boolean match(Representation rep) {
        return rootNode.match(rep.attrTable);
    }
    
    private void parse_filter(Node node) {
        accept('(');
        Node newNode = new Node();    
        parse_filter_comp(newNode);
        accept(')');
        node.add(newNode);
    }

    private void parse_filter_comp(Node node) {
        switch (filter[index]) {
            case '&' :
                accept('&');
                node.setOp(Node.AND);
                parse_filter_list(node);
                break;
            case '|' :
                accept('|');
                node.setOp(Node.OR);
                parse_filter_list(node);
                break;
            case '!' :
                accept('!');
                node.setOp(Node.NOT);
                parse_filter(node);
                break;
            default :
                node.setOp(Node.LEAF);
                parse_operation(node);
                break;
        }
    }

    private void parse_operation(Node node) {
        String f = new String(filter);
        int indEq = f.indexOf("=", index);
        if (-1 == indEq)
            throwException();
        String attr = f.substring(index, indEq);
        int indPar = f.indexOf(")", indEq);
        if (-1 == indEq)
            throwException();
        String value = f.substring(indEq + 1, indPar);
        if ("name".equals(attr))
            node.setNamePattern(value);
        else if ("signer".equals(attr))
            node.setSignerChainPattern(value);
        else
            throwException();
        node.setLeaf(attr, value);
        
        // it is used in case of concrete permissions 
        // and not in case of wildcarded ones
        attrTable.put(attr, value);
        
        index = indPar;
    }

    private void parse_filter_list(Node node) {
        parse_filter(node);
        if ('(' == filter[index])
            parse_filter_list(node);
    }

    private void accept(char ch) {
        if (ch != filter[index])
            throw new IllegalArgumentException();
        ++index;
    }
    
    private void throwException() {
        throw new IllegalArgumentException("Error at position " + index + " in " +
        		"\"" + new String(filter) + "\"");
    }
}