package org.osgi.impl.service.deploymentadmin.plugin;

/*
 * Generic alert result codes
 */
public final class PluginConstants {
    
    // alert codes
    static final int RESULT_SUCCESSFUL                           = 200;
    static final int RESULT_BUNDLE_START_WARNING                 = 250;
    
    static final int RESULT_USER_CANCELLED                       = 401;
    static final int RESULT_CORRUPTED_DEPLOYMENT_PACKAGE         = 402;
    static final int RESULT_PACKAGE_MISMATCH                     = 403;
    static final int RESULT_NOT_ACCEPTABLE_CONTENT               = 404;
    static final int RESULT_AUTHORIZATION_FAILURE                = 405;
    static final int RESULT_REQUEST_TIMED_OUT                    = 406;
    static final int RESULT_UNDEFINED_ERROR                      = 407;
    static final int RESULT_MALFORMED_URL                        = 408;
    static final int RESULT_DWNL_SERVER_NOT_AVAILABLE            = 409;
    static final int RESULT_DWNLD_DESCR_ERROR                    = 410;
    
    static final int RESULT_DEPL_ORDER_ERROR                     = 450;
    static final int RESULT_DEPL_MISSING_HEADER                  = 451;
    static final int RESULT_DEPL_BAD_HEADER                      = 452;
    static final int RESULT_DEPL_MISSING_FIXPACK_TARGET          = 453;
    static final int RESULT_DEPL_MISSING_BUNDLE                  = 454;
    static final int RESULT_DEPL_MISSING_RESOURCE                = 455;
    static final int RESULT_DEPL_SIGNING_ERROR                   = 456;
    static final int RESULT_DEPL_BUNDLE_NAME_ERROR               = 457;
    static final int RESULT_DEPL_FOREIGN_CUSTOMIZER              = 458;
    static final int RESULT_DEPL_NO_SUCH_RESOURCE                = 459;
    static final int RESULT_DEPL_BUNDLE_SHARING_VIOLATION        = 460;
    static final int RESULT_DEPL_CODE_RESOURCE_SHARING_VIOLATION = 461;
    static final int RESULT_DEPL_COMMIT_ERROR                    = 462;
    static final int RESULT_DEPL_UNDEFINED                       = 463;
    static final int RESULT_DEPL_RES_PROC_MISSING                = 464;
    static final int RESULT_DEPL_TIMEOUT                         = 465;

    // MIME types
    public static final String MIME_DP     = "application/vnd.osgi.dp";
    public static final String MIME_BUNDLE = "application/java-archive";

}
