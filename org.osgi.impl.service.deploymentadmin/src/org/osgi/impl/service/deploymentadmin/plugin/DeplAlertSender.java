package org.osgi.impl.service.deploymentadmin.plugin;

import java.security.AccessControlException;

import org.osgi.impl.service.deploymentadmin.DeploymentAdminImpl;
import org.osgi.impl.service.deploymentadmin.perm.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.dmt.DmtAlertItem;
import org.osgi.service.dmt.DmtData;

public class DeplAlertSender {

    static void sendAlert(Exception exception, String principal, 
            String correlator, String nodeUri, DeploymentAdminImpl da) 
    {
        if (null == principal)
            return;

        try {
            if (null == exception)
                da.getDmtAdmin().sendAlert(principal, 1226, correlator, new DmtAlertItem[] {
                      new DmtAlertItem(nodeUri, "org.osgi.deployment.downloadandinstallandactivate",
                      null, new DmtData(AlertCodes.RESULT_SUCCESSFUL))});
            else {
                if (exception instanceof DeploymentException) {
                    DeploymentException de = (DeploymentException) exception;
                    int res = getResultCodeToExceptionCode(de.getCode());
                    da.getDmtAdmin().sendAlert(principal, 1226, correlator, new DmtAlertItem[] {
                        new DmtAlertItem(nodeUri, "org.osgi.deployment.downloadandinstallandactivate",
                        null, new DmtData(res))});
                } else if (exception instanceof AccessControlException) {
                    AccessControlException ae = (AccessControlException) exception;
                    if (ae.getPermission() instanceof DeploymentAdminPermission)
                        da.getDmtAdmin().sendAlert(principal, 1226, correlator, new DmtAlertItem[] {
                            new DmtAlertItem(nodeUri, "org.osgi.deployment.downloadandinstallandactivate",
                            null, new DmtData(AlertCodes.RESULT_AUTHORIZATION_FAILURE))});
                }
            }
        } catch (Exception e) {
            // TODO log
            e.printStackTrace();
        }
    }

    private static int getResultCodeToExceptionCode(int code) {
        switch (code) {
            case DeploymentException.CODE_BUNDLE_START :
                return AlertCodes.RESULT_BUNDLE_START_WARNING;
            case DeploymentException.CODE_TIMEOUT :
                return AlertCodes.RESULT_DEPL_TIMEOUT;
            case DeploymentException.CODE_ORDER_ERROR :
                return AlertCodes.RESULT_DEPL_ORDER_ERROR;
            case DeploymentException.CODE_MISSING_HEADER:
                return AlertCodes.RESULT_DEPL_MISSING_HEADER;
            case DeploymentException.CODE_BAD_HEADER :
                return AlertCodes.RESULT_DEPL_BAD_HEADER;
            case DeploymentException.CODE_MISSING_FIXPACK_TARGET :
                return AlertCodes.RESULT_DEPL_MISSING_FIXPACK_TARGET;
            case DeploymentException.CODE_MISSING_BUNDLE :
                return AlertCodes.RESULT_DEPL_MISSING_BUNDLE;
            case DeploymentException.CODE_MISSING_RESOURCE :
                return AlertCodes.RESULT_DEPL_MISSING_RESOURCE;
            case DeploymentException.CODE_SIGNING_ERROR :
                return AlertCodes.RESULT_DEPL_SIGNING_ERROR;
            case DeploymentException.CODE_BUNDLE_NAME_ERROR :
                return AlertCodes.RESULT_DEPL_BUNDLE_NAME_ERROR;
            case DeploymentException.CODE_FOREIGN_CUSTOMIZER :
                return AlertCodes.RESULT_DEPL_FOREIGN_CUSTOMIZER;
            case DeploymentException.CODE_NO_SUCH_RESOURCE :
                return AlertCodes.RESULT_DEPL_NO_SUCH_RESOURCE;
            case DeploymentException.CODE_BUNDLE_SHARING_VIOLATION :
                return AlertCodes.RESULT_DEPL_BUNDLE_SHARING_VIOLATION;
            case DeploymentException.CODE_RESOURCE_SHARING_VIOLATION :
                return AlertCodes.RESULT_DEPL_CODE_RESOURCE_SHARING_VIOLATION;
            case DeploymentException.CODE_OTHER_ERROR :
                return AlertCodes.RESULT_UNDEFINED_ERROR;
            //case DeploymentException.COMMIT_ERROR :
            //    return AlertCodes.RESULT_DEPL_COMMIT_ERROR;
            case DeploymentException.CODE_PROCESSOR_NOT_FOUND :
                return AlertCodes.RESULT_DEPL_RES_PROC_MISSING;
            default :
                return AlertCodes.RESULT_DEPL_UNDEFINED;
        }
      }
    
    
}
