/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.internal.core;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.util.Tokenizer;

/**
 * This class maps aliases.
 */
public class AliasMapper {
	private static Hashtable processorAliasTable;
	private static Hashtable osnameAliasTable;

	/**
	 * Constructor.
	 *
	 */
	public AliasMapper() {
	}

	/**
	 * Return the master alias for the processor.
	 *
	 * @param processor Input name
	 * @return aliased name (if any)
	 */
	public String aliasProcessor(String processor) {
		processor = processor.toLowerCase();
		if (processorAliasTable == null) {
			InputStream in = getClass().getResourceAsStream(Constants.OSGI_PROCESSOR_ALIASES);
			if (in != null) {
				try {
					processorAliasTable = initAliases(in);
				} finally {
					try {
						in.close();
					} catch (IOException ee) {
					}
				}
			}
		}
		if (processorAliasTable != null) {
			String alias = (String) processorAliasTable.get(processor);
			if (alias != null) {
				processor = alias;
			}
		}
		return (processor);
	}

	/**
	 * Return the master alias for the osname.
	 *
	 * @param osname Input name
	 * @return aliased name (if any)
	 */
	public Object aliasOSName(String osname) {
		osname = osname.toLowerCase();
		if (osnameAliasTable == null) {
			InputStream in = getClass().getResourceAsStream(Constants.OSGI_OSNAME_ALIASES);
			if (in != null) {
				try {
					osnameAliasTable = initAliases(in);
				} finally {
					try {
						in.close();
					} catch (IOException ee) {
					}
				}
			}
		}
		if (osnameAliasTable != null) {
			Object aliasObject = osnameAliasTable.get(osname);
			//String alias = (String) osnameAliasTable.get(osname);
			if (aliasObject != null)
				if (aliasObject instanceof String) {
					osname = (String) aliasObject;
				} else {
					return (Vector) aliasObject;
				}
		}
		return (osname);
	}

	/**
	 * Read alias data and populate a Hashtable.
	 *
	 * @param in InputStream from which to read alias data.
	 * @return Hashtable of aliases.
	 */
	protected static Hashtable initAliases(InputStream in) {
		Hashtable aliases = new Hashtable(37);
		try {
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(in, "UTF8")); //$NON-NLS-1$
			} catch (UnsupportedEncodingException e) {
				br = new BufferedReader(new InputStreamReader(in));
			}
			while (true) {
				String line = br.readLine();
				if (line == null) /* EOF */{
					break; /* done */
				}
				Tokenizer tokenizer = new Tokenizer(line);
				String master = tokenizer.getString("# \t"); //$NON-NLS-1$
				if (master != null) {
					aliases.put(master.toLowerCase(), master);
					parseloop: while (true) {
						String alias = tokenizer.getString("# \t"); //$NON-NLS-1$
						if (alias == null) {
							break parseloop;
						}
						String lowerCaseAlias = alias.toLowerCase();
						Object storedMaster = aliases.get(lowerCaseAlias);
						if (storedMaster == null) {
							aliases.put(lowerCaseAlias, master);
						} else if (storedMaster instanceof String) {
							Vector newMaster = new Vector();
							newMaster.add(storedMaster);
							newMaster.add(master);
							aliases.put(lowerCaseAlias, newMaster);
						} else {
							((Vector) storedMaster).add(master);
							aliases.put(lowerCaseAlias, storedMaster);
						}
					}
				}
			}
		} catch (IOException e) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.printStackTrace(e);
			}
		}
		return (aliases);
	}
}
