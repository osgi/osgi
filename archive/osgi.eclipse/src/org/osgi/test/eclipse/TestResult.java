package org.osgi.test.eclipse;

import java.util.*;

public class TestResult {
	Map		map;
	
	public TestResult(String msg) {
		try {
			map = parse(msg);
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	Hashtable parse(String message) {
		Hashtable	ht = new Hashtable();
		StringBuffer	sb = new StringBuffer();
		for ( int i=0; i<message.length(); i++ ) {
			char c = message.charAt(i);
			if ( c == '[') {
				int n = message.indexOf(']',i+1);
				if ( n > i+2) {
					String s = message.substring(i+1,n-1);
					int j = s.indexOf('=');
					if ( j > 0 ) {
						String name = s.substring(0,j);
						String value = s.substring(j+1);
						ht.put(name,value);
						i = n;
					} else
						sb.append(c);
				}
				else
					sb.append(c);
			} else
				sb.append(c);
		}
		ht.put("@", message);
		ht.put("_", sb.toString());
		return ht;
	}
}