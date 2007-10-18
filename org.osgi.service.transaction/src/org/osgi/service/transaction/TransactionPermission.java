/*
 * $Date: 2007-08-10 04:17:58 +0300 $
 *
 * Copyright (c) OSGi Alliance (2000, 2007). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.service.transaction;

import java.io.IOException;
import java.security.BasicPermission;
import java.security.Permission;

/**
 * The TransactionPermission defines the security roles required to retrieve or start a transaction.
 *
 * @version $Revision: 1.0 $
 */
public final class TransactionPermission extends BasicPermission {

  private static final long serialVersionUID = 7055996148204934740L;

/**
   * The action string <code>get</code> (Value is "get").
   */
  public final static String GET = "get";

  /**
   * The action string <code>begin</code> (Value is "begin").
   */
  public final static String BEGIN = "begin";

  private final static int ACTION_GET = 0x00000001;
  private final static int ACTION_BEGIN = 0x00000002;
  private final static int ACTION_ALL = ACTION_GET | ACTION_BEGIN;
  private final static int ACTION_NONE = 0;

  /**
   * The actions mask.
   */
  private transient int action_mask = ACTION_NONE;

  /**
   * The actions in canonical form.
   */
  private String actions = null;

  /**
   * Creates a new Transaction permission
   * 
   * @param name is ignored, you can set anything here
   * @param actions the actions - either {@link #GET} or {@link #BEGIN}
   */
  public TransactionPermission(String name, String actions) {
    super("TransactionPermission");
    int mask = getMask(actions);
    init(mask);
  }

  /**
   * @see java.security.BasicPermission#implies(java.security.Permission)
   */
  public boolean implies(Permission p) {
    if (p instanceof TransactionPermission) {
      TransactionPermission target = (TransactionPermission) p;
      return ((action_mask & target.action_mask) == target.action_mask);
    }

    return false;
  }

  /**
   * Returns the canonical string representation of the actions. Always returns
   * present actions in the following order: <code>get</code>,
   * <code>begin</code>.
   * 
   * @return The canonical string representation of the actions.
   * @see java.security.BasicPermission#getActions()
   */
  public String getActions() {
    if (actions == null) {
      StringBuffer sb = new StringBuffer();
      boolean comma = false;

      if ((action_mask & ACTION_GET) == ACTION_GET) {
        sb.append(GET);
        comma = true;
      }

      if ((action_mask & ACTION_BEGIN) == ACTION_BEGIN) {
        if (comma) sb.append(',');
        sb.append(BEGIN);
      }

      actions = sb.toString();
    }

    return (actions);
  }

  /**
   * @see java.security.BasicPermission#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof TransactionPermission)) {
      return false;
    }
    TransactionPermission p = (TransactionPermission) obj;
    return action_mask == p.action_mask;
  }

  /**
   * @see java.security.BasicPermission#hashCode()
   */
  public int hashCode() {
    return getActions().hashCode();
  }

  // Called by constructors and when deserialized.
  private void init(int mask) {
    if ((mask == ACTION_NONE) || ((mask & ACTION_ALL) != mask)) {
      throw new IllegalArgumentException("invalid action string");
    }
    action_mask = mask;
  }

  /**
   * Parse action string into action mask.
   * 
   * @param actions Action string.
   * @return action mask.
   */
  private static int getMask(String actions) {
    boolean seencomma = false;

    int mask = ACTION_NONE;

    if (actions == null) {
      return mask;
    }

    char[] a = actions.toCharArray();

    int i = a.length - 1;
    if (i < 0) return mask;

    while (i != -1) {
      char c;

      // skip whitespace
      while ((i != -1)
             && ((c = a[i]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t'))
        i-- ;

      // check for the known strings
      int matchlen;

      if (i >= 2 && (a[i - 2] == 'g' || a[i - 2] == 'G')
          && (a[i - 1] == 'e' || a[i - 1] == 'E') && (a[i] == 't' || a[i] == 'T')) {
        matchlen = 3;
        mask |= ACTION_GET;

      } else if (i >= 4 && (a[i - 4] == 'b' || a[i - 4] == 'B')
                 && (a[i - 3] == 'e' || a[i - 3] == 'E')
                 && (a[i - 2] == 'g' || a[i - 2] == 'G')
                 && (a[i - 1] == 'i' || a[i - 1] == 'I') && (a[i] == 'n' || a[i] == 'N')) {
        matchlen = 5;
        mask |= ACTION_BEGIN;

      } else {
        // parse error
        throw new IllegalArgumentException("invalid permission: " + actions);
      }

      // make sure we didn't just match the tail of a word
      // like "ackbarfregister". Also, skip to the comma.
      seencomma = false;
      while (i >= matchlen && !seencomma) {
        switch (a[i - matchlen]) {
          case ',':
            seencomma = true;
            /* FALLTHROUGH */
          case ' ':
          case '\r':
          case '\n':
          case '\f':
          case '\t':
            break;
          default:
            throw new IllegalArgumentException("invalid permission: " + actions);
        }
        i-- ;
      }

      // point I at the location of the comma minus one (or -1).
      i -= matchlen;
    }

    if (seencomma) {
      throw new IllegalArgumentException("invalid permission: " + actions);
    }

    return mask;
  }

  /**
   * WriteObject is called to save the state of this permission to a stream. The
   * actions are serialized, and the superclass takes care of the name.
   */
  private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException {
    // Write out the actions. The superclass takes care of the name
    // call getActions to make sure actions field is initialized
    if (actions == null) getActions();
    s.defaultWriteObject();
  }

  /**
   * readObject is called to restore the state of this permission from a stream.
   */
  private void readObject(java.io.ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    // Read in the action, then initialize the rest
    s.defaultReadObject();
    init(getMask(actions));
  }

}
