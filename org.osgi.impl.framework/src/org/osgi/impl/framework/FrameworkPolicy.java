/**
 * Copyright (c) 2001 Gatespace AB. All Rights Reserved.
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

import java.net.*;
import java.security.*;
import java.util.*;

/**
 * Implementation of a Permission Policy for Framework.
 *
 * @see java.security.Policy
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */

class FrameworkPolicy extends Policy {

    private Policy oldPolicy;

    private PermissionAdminImpl permissionAdmin;


    FrameworkPolicy(PermissionAdminImpl pa, Policy old) {
      permissionAdmin = pa;
      oldPolicy = old;
    }

    //
    // Policy methods
    //

    public PermissionCollection getPermissions(CodeSource cs) {
      PermissionCollection pc = null;
      try {
        if( cs != null &&
            cs.getLocation() != null &&
            cs.getLocation().getProtocol() != null &&
            cs.getLocation().getProtocol().equals(BundleURLStreamHandler.PROTOCOL)) {
            
            Long id = new Long(cs.getLocation().getHost());
            pc = permissionAdmin.getPermissionCollection(id); 
        } else {
          pc = oldPolicy.getPermissions(cs);
        }
      } catch(Exception ignored) {
        pc = null;
      }
      // Never return null, since javadoc says we must
      // return a new mutable PermissionCollection
      if(pc == null) {
        pc = new Permissions();
      }
      return pc;
    }


    public void refresh() {
      oldPolicy.refresh();
    }
}
