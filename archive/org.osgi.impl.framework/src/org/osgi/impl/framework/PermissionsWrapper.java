/**
 * Copyright (c) 2001 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.permissionadmin.*;


/**
 * Wrapps Permissions so that we can update it dynamicly.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */


class PermissionsWrapper extends PermissionCollection {

  private BundleImpl bundle;
  private PermissionAdminImpl pa;
  private PermissionCollection permissions;
  private boolean readOnly = false;

  PermissionsWrapper(PermissionAdminImpl p, BundleImpl b) {
    pa = p;
    bundle = b;
    permissions = makePermissionCollection(b);;
  }


  public void add(Permission permission) {
    getPerms().add(permission);
  }

    
  public Enumeration elements() {
    return getPerms().elements();
  }


  public boolean implies(Permission permission) {
    return getPerms().implies(permission);
  }


  public boolean isReadOnly() {
    return readOnly;
  }


  public void setReadOnly() {
    if (!readOnly) {
      readOnly = true;
      getPerms().setReadOnly();
    }
  }

  synchronized void invalidate() {
    permissions = null;
  }

  synchronized private PermissionCollection getPerms() {
    PermissionCollection p = permissions;
    if (p == null) {
      p = makePermissionCollection(bundle);
      if (readOnly) {
	p.setReadOnly();
      }
      permissions = p;
    }
    return p;
  }


  /**
   * Create the permissionCollection assigned to the bundle with the specified id.
   * The collection contains the configured permissions for the bundle location
   * plus implict granted permissions (FilePermission for the data area and
   * java runtime permissions).
   *
   * @param bundle The bundle whose permissions are to be created.
   *
   * @return The permissions assigned to the bundle with the specified
   * location, or the default permissions if that bundle has not been assigned
   * any permissions.
   */
  private PermissionCollection makePermissionCollection(BundleImpl bundle) {
    PermissionCollection pc;
    File root = bundle.getDataRoot();
    PermissionInfo[] pi = pa.getPermissions(bundle.location);
    if (pi != null) {
      pc = makePermissionCollection(pi, root);
    } else {
      pi = pa.getDefaultPermissions();
      if (pi == null) {
        pi = pa.getImplicitDefaultPermissions();
      }
      pc = makePermissionCollection(pi, null);
    }

    pc.add(new FilePermission(root.getPath(),
                              "read,write"));
    pc.add(new FilePermission((new File(root, "-")).getPath(),
                              "read,write,execute,delete"));
    pc.add(new PackagePermission("java.*", "import"));

    if (pa.runtimePermissions != null) {
      for (Enumeration e = pa.runtimePermissions.elements(); e.hasMoreElements();) {
        pc.add((Permission) e.nextElement());
      }
    }
    return pc;
  }

  /**
   * Build a permissionCollection based on a set PermissionInfo objects. All the
   * permissions that are available in the CLASSPATH are constructed and all
   * the bundle based permissions are constructed as UnresolvedPermissions.
   *
   * @param pi Array of PermissionInfo to enter into the PermissionCollection.
   *
   * @return The permissions corresponding to the PermissionInfo objects.
   */
  private PermissionCollection makePermissionCollection(PermissionInfo[] pi, File bundleDataDir) {
    Permissions p = new Permissions();
    for (int i = pi.length - 1; i >= 0; i--) {
      String a = pi[i].getActions();
      String n = pi[i].getName();
      String t = pi[i].getType();
      try {
        Class pc = Class.forName(t);
        if(pc == FilePermission.class) {
          File f = new File(n);
          if(f.isAbsolute()) {
            p.add(new FilePermission(n, a));
          } else if(bundleDataDir == null) {
            // We have a FilePermission for a relative path in
            // the default permissions. These should be ignored.
          } else {
            File absolute = new File(bundleDataDir, n);
            p.add(new FilePermission(absolute.getPath(), a));
          }
        } else {
          Constructor c = pc.getConstructor(new Class [] { String.class, String.class });
          p.add((Permission)c.newInstance(new Object[] { n, a }));
        }
      } catch (ClassNotFoundException e) {
        p.add(new UnresolvedPermission(t, n, a, null));
      } catch (NoSuchMethodException ignore) {
      } catch (InstantiationException ignore) {
      } catch (IllegalAccessException ignore) {
      } catch (InvocationTargetException ignore) {
      }
    }
    return p;
  }
}
