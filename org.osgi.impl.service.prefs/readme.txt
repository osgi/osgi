--------------------------------------------------------------------------

    $Id$

    Reference Implementation of the Preferences Service

--------------------------------------------------------------------------



    This bundle implements the service

            org.osgi.service.prefs.PreferencesService

    It stores preferences data in its persistent storage area (see
    org.osgi.framework.BundleContext.getDataFile()), using one file for
    each preferences tree.  Note that every bundle using preferences can
    have one preferences tree for system preferences, and one for each user
    of the bundle.  Flushing any preferences node causes the entire tree
    containing that node to be flushed to persistent storage.  The sync
    method just calls flush(), since it is assumed that this bundle has
    exclusive access to its persistent storage area.



--------------------------------------------------------------------------
Copyright: Copyright 1996-2001 Sun Microsystems, Inc. All Rights Reserved.
