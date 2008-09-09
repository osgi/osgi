/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.osgi.test.cases.deploymentadmin.mo.tb1.CommandExecution;

import java.util.ArrayList;
import javax.security.auth.x500.X500Principal;

/**
 * This class contains a method to match a distinguished name (DN) chain against
 * and DN chain pattern.
 * <p>
 * The format of DNs are given in RFC 2253. We represent a signature chain for
 * an X.509 certificate as a semicolon separated list of DNs. This is what we
 * refer to as the DN chain. Each DN is made up of relative distinguished names
 * (RDN) which in turn are made up of key value pairs. For example:
 * 
 * <pre>
 * 
 *  
 *   cn=ben+ou=research,o=ACME,c=us;ou=Super CA,c=CA
 *   
 *  
 * </pre>
 * 
 * is made up of two DNs: "<code>cn=ben+ou=research,o=ACME,c=us</code>" and "
 * <code>ou=Super CA,c=CA</code>". The first DN is made of of three RDNs: "
 * <code>cn=ben+ou=research</code>" and "<code>o=ACME</code>" and "
 * <code>c=us</code>". The first RDN has two name value pairs: "
 * <code>cn=ben</code>" and "<code>ou=research</code>".
 * <p>
 * A chain pattern makes use of wildcards ('*') to match against DNs, DN
 * prefixes, and value. If a DN in a DN chain is made up of a wildcard ("*"),
 * that wildcard will match zero or more DNs in the chain. If the first RDN of a
 * DN is the wildcard, that DN will match any other DN with the same suffix (the
 * DN with the wildcard RDN removed). If a value of a name/value pair is a
 * wildcard, the value will match any value for that name.
 */
public class DNChainMatching {
	/**
	 * Check the name/value pairs of the rdn against the pattern.
	 * 
	 * @param rdn ArrayList of name value pairs for a given RDN.
	 * @param rdnPattern ArrayList of name value pattern pairs.
	 * @return true if the list of name value pairs match the pattern.
	 */
	private static boolean rdnmatch(ArrayList rdn, ArrayList rdnPattern) {
		if (rdn.size() != rdnPattern.size())
			return false;
		for (int i = 0; i < rdn.size(); i++) {
			String rdnNameValue = (String) rdn.get(i);
			String patNameValue = (String) rdnPattern.get(i);
			int rdnNameEnd = rdnNameValue.indexOf('=');
			int patNameEnd = patNameValue.indexOf('=');
			if (rdnNameEnd != patNameEnd || !rdnNameValue.regionMatches(0, patNameValue, 0, rdnNameEnd)) {
				return false;
			}
			String patValue = patNameValue.substring(patNameEnd);
			String rdnValue = rdnNameValue.substring(rdnNameEnd);
			if (!rdnValue.equals(patValue) && !patValue.equals("=*") && !patValue.equals("=#16012a")) { //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
		}
		return true;
	}

	private static boolean dnmatch(ArrayList dn, ArrayList dnPattern) {
		int dnStart = 0;
		int patStart = 0;
		int patLen = dnPattern.size();
		if (patLen == 0) {
			return false;
		}
		if (dnPattern.get(0).equals("*")) { //$NON-NLS-1$
			patStart = 1;
			patLen--;
		}
		if (dn.size() < patLen) {
			return false;
		} else if (dn.size() > patLen) {
			if (!dnPattern.get(0).equals("*")) { //$NON-NLS-1$
				// If the number of rdns do not match we must have a prefix map
				return false;
			}
			// The rdnPattern and rdn must have the same number of elements
			dnStart = dn.size() - patLen;
		}
		for (int i = 0; i < patLen; i++) {
			if (!rdnmatch((ArrayList) dn.get(i + dnStart), (ArrayList) dnPattern.get(i + patStart))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Parses a distinguished name chain and returns an ArrayList where each
	 * element represents a distinguished name (DN) in the chain of DNs. Each
	 * element will be either a String, if the element represents a wildcard
	 * ("*"), or an ArrayList representing an RDN. Each element in the RDN
	 * ArrayList will be a String, if the element represents a wildcard ("*"),
	 * or an ArrayList of Strings, each String representing a name/value pair in
	 * the RDN.
	 * 
	 * @param dnChain
	 * @return a list of DNs.
	 * @throws IllegalArgumentException
	 */
	private static ArrayList parseDNchain(String dnChain) throws IllegalArgumentException {
		ArrayList parsed = new ArrayList();
		int startIndex = 0;
		startIndex = skipSpaces(dnChain, startIndex);
		while (startIndex < dnChain.length()) {
			int endIndex = startIndex;
			boolean inQuote = false;
			out: while (endIndex < dnChain.length()) {
				char c = dnChain.charAt(endIndex);
				switch (c) {
					case '"' :
						inQuote = !inQuote;
						break;
					case '\\' :
						endIndex++; // skip the escaped char
						break;
					case ';' :
						if (!inQuote)
							break out;
				}
				endIndex++;
			}
			if (endIndex > dnChain.length()) {
				throw new IllegalArgumentException("unterminated escape"); //$NON-NLS-1$
			}
			parsed.add(dnChain.substring(startIndex, endIndex));
			startIndex = endIndex + 1;
			startIndex = skipSpaces(dnChain, startIndex);
		}
		// Now we parse is a list of strings, lets make ArrayList of rdn out of
		// them
		for (int i = 0; i < parsed.size(); i++) {
			String dn = (String) parsed.get(i);
			if (dn.equals("*")) //$NON-NLS-1$
				continue;
			ArrayList rdns = new ArrayList();
			if (dn.charAt(0) == '*') {
				if (dn.charAt(1) != ',')
					throw new IllegalArgumentException("invalid wildcard prefix"); //$NON-NLS-1$
				rdns.add("*"); //$NON-NLS-1$
				dn = new X500Principal(dn.substring(2)).getName(X500Principal.CANONICAL);
			} else {
				dn = new X500Principal(dn).getName(X500Principal.CANONICAL);
			}
			// Now dn is a nice CANONICAL DN
			parseDN(dn, rdns);
			parsed.set(i, rdns);
		}
		if (parsed.size() == 0) {
			throw new IllegalArgumentException("empty DN chain"); //$NON-NLS-1$
		}
		return parsed;
	}

	/**
	 * Increment startIndex until the end of dnChain is hit or until it is the
	 * index of a non-space character.
	 */
	private static int skipSpaces(String dnChain, int startIndex) {
		while (startIndex < dnChain.length() && dnChain.charAt(startIndex) == ' ')
			startIndex++;
		return startIndex;
	}

	/**
	 * Takes a distinguished name in canonical form and fills in the rdnArray
	 * with the extracted RDNs.
	 * 
	 * @param dn the distinguished name in canonical form.
	 * @param rdnArray the array to fill in with RDNs extracted from the dn
	 * @throws IllegalArgumentException if a formatting error is found.
	 */
	private static void parseDN(String dn, ArrayList rdnArray) throws IllegalArgumentException {
		int startIndex = 0;
		char c = '\0';
		ArrayList nameValues = new ArrayList();
		while (startIndex < dn.length()) {
			int endIndex;
			for (endIndex = startIndex; endIndex < dn.length(); endIndex++) {
				c = dn.charAt(endIndex);
				if (c == ',' || c == '+')
					break;
				if (c == '\\') {
					endIndex++; // skip the escaped char
				}
			}
			if (endIndex > dn.length())
				throw new IllegalArgumentException("unterminated escape " + dn); //$NON-NLS-1$
			nameValues.add(dn.substring(startIndex, endIndex));
			if (c != '+') {
				rdnArray.add(nameValues);
				if (endIndex != dn.length())
					nameValues = new ArrayList();
				else
					nameValues = null;
			}
			startIndex = endIndex + 1;
		}
		if (nameValues != null) {
			throw new IllegalArgumentException("improperly terminated DN " + dn); //$NON-NLS-1$
		}
	}

	/**
	 * This method will return an 'index' which points to a non-wild-card DN or
	 * the end-of-arraylist.
	 */
	private static int skipWildCards(ArrayList dnChainPattern, int dnChainPatternIndex) throws IllegalArgumentException {
		int i;
		for (i = dnChainPatternIndex; i < dnChainPattern.size(); i++) {
			Object dnPattern = dnChainPattern.get(i);
			if (dnPattern instanceof String) {
				if (!dnPattern.equals("*")) { //$NON-NLS-1$
					throw new IllegalArgumentException("expected wild-card in DN pattern"); //$NON-NLS-1$
				}
				// otherwise continue skipping over wild cards
			} else if (dnPattern instanceof ArrayList) {
				// if its an arraylist then we have our 'non-wild-card' DN
				break;
			} else {
				// unknown member of the DNChainPattern
				throw new IllegalArgumentException("expected String or Arraylist in DN Pattern"); //$NON-NLS-1$
			}
		}
		// i either points to end-of-arraylist, or to the first non-wild-card
		// pattern
		// after dnChainPatternIndex
		return i;
	}

	/**
	 * recursively attempt to match the DNChain, and the DNChainPattern where
	 * DNChain is of the format: "DN;DN;DN;" and DNChainPattern is of the
	 * format: "DNPattern;*;DNPattern" (or combinations of this)
	 */
	private static boolean dnChainMatch(ArrayList dnChain, int dnChainIndex, ArrayList dnChainPattern, int dnChainPatternIndex) throws IllegalArgumentException {
		if (dnChainIndex >= dnChain.size()) {
			return false;
		}
		if (dnChainPatternIndex >= dnChainPattern.size()) {
			return false;
		}
		// check to see what the pattern starts with
		Object dnPattern = dnChainPattern.get(dnChainPatternIndex);
		if (dnPattern instanceof String) {
			if (!dnPattern.equals("*")) { //$NON-NLS-1$
				throw new IllegalArgumentException("expected wild-card in DN pattern"); //$NON-NLS-1$
			}
			// here we are processing a wild card as the first DN
			// skip all wild-card DN's
			dnChainPatternIndex = skipWildCards(dnChainPattern, dnChainPatternIndex);
			if (dnChainPatternIndex >= dnChainPattern.size()) {
				// the entire DNChainPattern was wild-cards, so we have a match
				return true;
			}
			//
			// we will now recursively call to see if the rest of the
			// DNChainPattern
			// matches increasingly smaller portions of the rest of the DNChain
			//
			for (int i = dnChainIndex; i < dnChain.size(); i++) {
				if (dnChainMatch(dnChain, i, dnChainPattern, dnChainPatternIndex)) {
					return true;
				}
			}
			// if we are here, then we didn't find a match.. fall through to
			// failure
		} else if (dnPattern instanceof ArrayList) {
			// here we have to do a deeper check for each DN in the pattern
			// until we hit a wild card
			do {
				if (!dnmatch((ArrayList) dnChain.get(dnChainIndex), (ArrayList) dnPattern)) {
					return false;
				}
				// go to the next set of DN's in both chains
				dnChainIndex++;
				dnChainPatternIndex++;
				// if we finished the pattern then it all matched
				if ((dnChainIndex >= dnChain.size()) && (dnChainPatternIndex >= dnChainPattern.size())) {
					return true;
				}
				// if the DN Chain is finished, but the pattern isn't finished
				// then if the rest of the pattern is not wildcard then we are
				// done
				if (dnChainIndex >= dnChain.size()) {
					dnChainPatternIndex = skipWildCards(dnChainPattern, dnChainPatternIndex);
					// return TRUE iff the pattern index moved past the
					// array-size
					// (implying that the rest of the pattern is all wild-cards)
					return (dnChainPatternIndex >= dnChainPattern.size());
				}
				// if the pattern finished, but the chain continues then we have
				// a mis-match
				if (dnChainPatternIndex >= dnChainPattern.size()) {
					return false;
				}
				// get the next DN Pattern
				dnPattern = dnChainPattern.get(dnChainPatternIndex);
				if (dnPattern instanceof String) {
					if (!dnPattern.equals("*")) { //$NON-NLS-1$
						throw new IllegalArgumentException("expected wild-card in DN pattern"); //$NON-NLS-1$
					}
					// if the next DN is a 'wild-card', then we will recurse
					return dnChainMatch(dnChain, dnChainIndex, dnChainPattern, dnChainPatternIndex);
				} else if (!(dnPattern instanceof ArrayList)) {
					throw new IllegalArgumentException("expected String or Arraylist in DN Pattern"); //$NON-NLS-1$
				}
				// if we are here, then we will just continue to the match the
				// next set of DN's from the DNChain, and the DNChainPattern
				// since both are array-lists
			} while (true);
			// should never reach here?
		} else {
			throw new IllegalArgumentException("expected String or Arraylist in DN Pattern"); //$NON-NLS-1$
		}
		// if we get here, the the default return is 'mis-match'
		return false;
	}

	/**
	 * Matches a distinguished name chain against a pattern of a distinguished
	 * name chain.
	 * 
	 * @param dnChain
	 * @param pattern the pattern of distinguished name (DN) chains to match
	 *        against the dnChain. Wildcards "*" can be used in three cases:
	 *        <ol>
	 *        <li>As a DN. In this case, the DN will consist of just the "*".
	 *        It will match zero or more DNs. For example, "cn=me,c=US;*;cn=you"
	 *        will match "cn=me,c=US";cn=you" and
	 *        "cn=me,c=US;cn=her,c=CA;cn=you".
	 *        <li>As a DN prefix. In this case, the DN must start with "*,".
	 *        The wild card will match zero or more RDNs at the start of a DN.
	 *        For example, "*,cn=me,c=US;cn=you" will match "cn=me,c=US";cn=you"
	 *        and "ou=my org unit,o=my org,cn=me,c=US;cn=you"</li>
	 *        <li>As a value. In this case the value of a name value pair in an
	 *        RDN will be a "*". The wildcard will match any value for the given
	 *        name. For example, "cn=*,c=US;cn=you" will match
	 *        "cn=me,c=US";cn=you" and "cn=her,c=US;cn=you", but it will not
	 *        match "ou=my org unit,c=US;cn=you". If the wildcard does not occur
	 *        by itself in the value, it will not be used as a wildcard. In
	 *        other words, "cn=m*,c=US;cn=you" represents the common name of
	 *        "m*" not any common name starting with "m".</li>
	 *        </ol>
	 * @return true if dnChain matches the pattern.
	 * @throws IllegalArgumentException
	 */
	public static boolean match(String dnChain, String pattern) {
		ArrayList parsedDNChain;
		ArrayList parsedDNPattern;
		try {
			parsedDNChain = parseDNchain(dnChain);
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage() + ": " + dnChain); //$NON-NLS-1$
			return false;
		}
		try {
			parsedDNPattern = parseDNchain(pattern);
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage() + ": " + pattern); //$NON-NLS-1$
			return false;
		}
		try {
			return dnChainMatch(parsedDNChain, 0, parsedDNPattern, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
