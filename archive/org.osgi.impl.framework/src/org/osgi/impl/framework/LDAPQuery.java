/**
 * Copyright (c) 1999 - 2001 Gatespace AB. All Rights Reserved.
 *
 * Gatespace grants Open Services Gateway Initiative (OSGi) an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.lang.reflect.*;
import java.util.*;

import org.osgi.framework.InvalidSyntaxException;

/**
 * LDAP filter functions
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class LDAPQuery
{
    private static final char WILDCARD = 65535;
    private static final String WILDCARD_STRING =
	new String(new char [] { WILDCARD });

    private static final String NULL      = "Null query";
    private static final String GARBAGE   = "Trailing garbage";
    private static final String MALFORMED = "Malformed query";
    private static final String EMPTY     = "Empty list";
    private static final String SUBEXPR   = "No subexpression";
    private static final String OPERATOR  = "Undefined operator";
    private static final String TRUNCATED = "Truncated expression";
    private static final String EQUALITY  = "Only equality supported";

    private static final int EQ     = 0;
    private static final int LE     = 1;
    private static final int GE     = 2;
    private static final int APPROX = 3;

    private static final Class[]  classArg = new Class[] { String.class };
    private static final Class[] objectArg = new Class[] { Object.class };

    boolean val;
    String tail;
    Dictionary prop;


    public static String canonicalize(String f)
    {
      if(f!=null) {
	StringBuffer sb=new StringBuffer();
	boolean protect=false;

	for(int i=0;i<f.length();i++) {
	  char c=f.charAt(i);
	  if(Character.isWhitespace(c)) {
	    if(protect) sb.append(c);
	  } else {
	    if(c=='\\') {
        if (++i<f.length()) {
          sb.append(c);
          c=f.charAt(i);
        }
      } else if(c=='=') {
        protect=true;
      } else if(c==')') {
        protect=false;
      } else if(c=='(') {
        // An ugly hack, haven't got time to clean this up atm
        sb.append(c);
        i = tryCanonicalizeAttr(f, sb, ++i);
        if(f.charAt(i) == '=') protect = true;
        continue;
      }
	    sb.append(c);
	  }
	}

	f=sb.toString();
      }
      return f;
    }

    public static int tryCanonicalizeAttr(String f, StringBuffer sb, int pos) {
      int i = pos;
      // First, make sure we're reading an attribute
      for(; i < f.length(); ++i) {
        char c = f.charAt(i);
        if(Character.isWhitespace(c)) {
          // Skip leading whitespace
          continue;
        } else if(c == '&' ||
                  c == '|' ||
                  c == '!') {
          // Not reading attribute yet
          sb.append(c);
          return i;
        } else {
          // We're reading an attribute
          break;
        }
      }
      // Now we're reading an attribute
      for(; i < f.length(); ++i) {
        char c = f.charAt(i);
        if( c == '=' ||
            c == '~' ||
            c == '<' ||
            c == '>') {
            // Attribute ended, trim trailing whitespace
            while(sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1)))
              sb.setLength(sb.length() - 1);
            sb.append(c);
            break;
        } else {
            sb.append(c);
        }
      }
      return i;
    }

    public static void check(String q) throws InvalidSyntaxException
    {
	query(q,new Hashtable());
    }


    public static boolean query(String q, Dictionary p)
	throws InvalidSyntaxException
    {
	LDAPQuery lq=new LDAPQuery(q,p);
	lq.doQuery();
	if(lq.tail.length()>0) lq.error(GARBAGE);
	return lq.val;
    }


    LDAPQuery(String q, Dictionary p) throws InvalidSyntaxException
    {
	if(q==null || q.length()==0) error(NULL);
	tail=q;
	prop=p;
    }


    void doQuery() throws InvalidSyntaxException
    {
	if(tail.length()<3 || !prefix("(")) error(MALFORMED);
	
	switch(tail.charAt(0)) {
	case '&': doAnd(); break;
	case '|': doOr(); break;
	case '!': doNot(); break;
	default: doSimple(); break;
	}
	
	if(!prefix(")")) error(MALFORMED);
    }


    private void doAnd() throws InvalidSyntaxException
    {
	tail=tail.substring(1);
	boolean val1=true;
	if(!tail.startsWith("(")) error(EMPTY);
	do {
	    doQuery();
	    if(!val) val1=false;
	} while(tail.startsWith("("));
	val=val1;
    }


    private void doOr() throws InvalidSyntaxException
    {
	tail=tail.substring(1);
	boolean val1=false;
	if(!tail.startsWith("(")) error(EMPTY);
	do {
	    doQuery();
	    if(val) val1=true;
	} while(tail.startsWith("("));
	val=val1;
    }


    private void doNot() throws InvalidSyntaxException
    {
	tail=tail.substring(1);
	if(!tail.startsWith("(")) error(SUBEXPR);
	doQuery();
	val=!val;
    }


    private void doSimple() throws InvalidSyntaxException
    {
	int op=0;
	Object attr=getAttr();
	
	if(prefix("=" )) op=EQ;
	else if(prefix("<=")) op=LE;
	else if(prefix(">=")) op=GE;
	else if(prefix("~=")) op=APPROX;
	else error(OPERATOR);
	
	val=compare(attr,op,getValue());
    }


    private boolean prefix(String pre)
    {
	if(!tail.startsWith(pre)) return false;
	tail=tail.substring(pre.length());
	return true;
    }


    private Object getAttr()
    {
	int len=tail.length();
	int ix=0;
    label:
	for(;ix<len;ix++) {
	    switch(tail.charAt(ix)) {
	    case '(':
	    case ')':
	    case '<':
	    case '>':
	    case '=':
	    case '~':
	    case '*':
	    case '\\':
		break label;
	    }
	}
	String attr=tail.substring(0,ix);
	tail=tail.substring(ix);
	Object res = prop.get(attr);
	if (res == null && !(prop instanceof PropertiesDictionary)) {
	    // No match and we don't have a Dictionary ignore case,
	    // loop through all keys.
	    for (Enumeration e = prop.keys(); e.hasMoreElements();) {
		String key = (String)e.nextElement();
		if (key.equalsIgnoreCase(attr)) {
		    res = prop.get(key);
		    break;
		}
	    }
	}
	return res;
    }


    private String getValue()
    {
	StringBuffer sb=new StringBuffer();
	int len=tail.length();
	int ix=0;
    label:for(;ix<len;ix++) {
	char c=tail.charAt(ix);
	switch(c) {
	case '(':
	case ')':
	    break label;
	case '*':
	    sb.append(WILDCARD);
	    break;
	case '\\':
	    if(ix==len-1) break label;
	    sb.append(tail.charAt(++ix));
	    break;
	default:
	    sb.append(c);
	    break;
	}
    }
	tail=tail.substring(ix);
	return sb.toString();
    }


    private void error(String m) throws InvalidSyntaxException
    {
	throw new InvalidSyntaxException(m,tail);
    }


    private boolean compare(Object obj, int op, String s)
    {
	if(obj==null) return false;
	if(op==EQ && s.equals(WILDCARD_STRING)) return true;
	try {
	    if(obj instanceof String) {
		return compareString((String)obj,op,s);
	    } else if(obj instanceof Character) {
		return compareString(obj.toString(),op,s);
	    } else if(obj instanceof Long) {
		return compareSign(op,Long.valueOf(s).compareTo(obj));
	    } else if(obj instanceof Integer) {
		return compareSign(op,Integer.valueOf(s).compareTo(obj));
	    } else if(obj instanceof Short) {
		return compareSign(op,Short.valueOf(s).compareTo(obj));
	    } else if(obj instanceof Byte) {
		return compareSign(op,Byte.valueOf(s).compareTo(obj));
	    } else if(obj instanceof Double) {
		return compareSign(op,Double.valueOf(s).compareTo(obj));
	    } else if(obj instanceof Float) {
		return compareSign(op,Float.valueOf(s).compareTo(obj));
	    } else if(obj instanceof Boolean) {
		if(op==LE || op==GE) return false;
		return ((Boolean)obj).equals(new Boolean(s));
	    } else if(obj instanceof Comparable) {
		Constructor c = obj.getClass().getConstructor(new Class [] { s.getClass() });
		Comparable cobj = (Comparable)c.newInstance(new Object [] { s });
		return compareSign(op,cobj.compareTo(obj));
	    } else if(obj instanceof Vector) {
		for(Enumeration e=((Vector)obj).elements();e.hasMoreElements();)
		    if(compare(e.nextElement(),op,s)) return true;
	    } else if(obj.getClass().isArray()) {
		int len=Array.getLength(obj);
		for(int i=0;i<len;i++)
		    if(compare(Array.get(obj,i),op,s)) return true;
	    }
	} catch(Exception e) { }
	return false;
    }


    static boolean compareString(String s1, int op, String s2)
    {
	switch(op) {
	case EQ:
	    return patSubstr(s1,s2);
	case APPROX:
	    return fixupString(s2).equals(fixupString(s1));
	default:
	    return compareSign(op,s2.compareTo(s1));
	}
    }


    static boolean compareSign(int op, int cmp)
    {
	switch(op) {
	case LE:            return cmp>=0;
	case GE:            return cmp<=0;
	case EQ:            return cmp==0;
	default: /*APPROX*/ return cmp==0;
	}
    }

    static String fixupString(String s)
    {
	StringBuffer sb=new StringBuffer();
	int len=s.length();
	boolean isStart=true;
	boolean isWhite=false;
	for(int i=0;i<len;i++) {
	    char c=s.charAt(i);
	    if(Character.isWhitespace(c)) {
		isWhite=true;
	    } else {
		if(!isStart && isWhite) sb.append(' ');
		if(Character.isUpperCase(c)) c=Character.toLowerCase(c);
		sb.append(c);
		isStart=false;
		isWhite=false;
	    }
	}
	return sb.toString();
    }

    static boolean patSubstr(String s, String pat)
    {
	if(s==null) return false;
	if(pat.length()==0) return s.length()==0;
	if(pat.charAt(0)==WILDCARD) {
	    pat=pat.substring(1);
	    for(;;) {
		if(patSubstr(s,pat)) return true;
		if(s.length()==0) return false;
		s=s.substring(1);
	    }
	} else {
	    if(s.length()==0 || s.charAt(0)!=pat.charAt(0)) return false;
	    return patSubstr(s.substring(1),pat.substring(1));
	}
    }
}
