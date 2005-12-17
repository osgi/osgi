/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.internal.core;

import org.eclipse.osgi.util.NLS;

public class ConsoleMsg extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.osgi.framework.internal.core.ConsoleMessages"; //$NON-NLS-1$
	
	public static String CONSOLE_LINES_TO_SCROLL_NEGATIVE_ERROR;
	public static String CONSOLE_NESTED_EXCEPTION;
	public static String CONSOLE_ERROR_READING_RESOURCE;
	public static String CONSOLE_RESOURCE_NOT_IN_BUNDLE;
	public static String CONSOLE_MORE;
	public static String CONSOLE_HELP_CONTROLLING_CONSOLE_HEADING;
	public static String CONSOLE_HELP_MORE;
	public static String CONSOLE_HELP_DISCONNECT;
	public static String CONSOLE_CONFIRM_MORE;
	public static String CONSOLE_CONFIRM_DISCONNECT;
	public static String CONSOLE_CONFIRM;
	public static String CONSOLE_CONFIRM_VALUES;
	public static String CONSOLE_Y;
	public static String CONSOLE_N;
	public static String CONSOLE_PROMPT_DEFAULT;
	public static String CONSOLE_INVALID_INPUT;
	public static String CONSOLE_TOO_MUCH_INVALID_INPUT;
	public static String CONSOLE_MORE_ENTER_LINES;
	
	public static String CONSOLE_HELP_VALID_COMMANDS_HEADER;
	public static String CONSOLE_LISTENING_ON_PORT;
	public static String CONSOLE_PROMPT;
	
	public static String CONSOLE_TELNET_CONNECTION_REFUSED;
	public static String CONSOLE_TELNET_CURRENTLY_USED;
	public static String CONSOLE_TELNET_ONE_CLIENT_ONLY;
	
	public static String CONSOLE_UNINSTALLED_MESSAGE;
	public static String CONSOLE_INSTALLED_MESSAGE;
	public static String CONSOLE_RESOLVED_MESSAGE;
	public static String CONSOLE_STARTING_MESSAGE;
	public static String CONSOLE_STOPPING_MESSAGE;
	public static String CONSOLE_ACTIVE_MESSAGE;
	
	public static String CONSOLE_HELP_CONTROLLING_FRAMEWORK_HEADER;
	public static String CONSOLE_HELP_LAUNCH_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_SHUTDOWN_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_CLOSE_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_EXIT_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_GC_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_INIT_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_KEYVALUE_ARGUMENT_DESCRIPTION;
	public static String CONSOLE_HELP_SETPROP_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_CONTROLLING_BUNDLES_HEADER;
	public static String CONSOLE_HELP_INSTALL_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_UNINSTALL_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_START_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_STOP_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_REFRESH_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_DISPLAYING_STATUS_HEADER;
	public static String CONSOLE_HELP_STATUS_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_SS_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_FILTER_ARGUMENT_DESCRIPTION;
	public static String CONSOLE_HELP_SERVICES_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_PACKAGES_ARGUMENT_DESCRIPTION;
	public static String CONSOLE_HELP_PACKAGES_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_BUNDLES_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_IDLOCATION_ARGUMENT_DESCRIPTION;
	public static String CONSOLE_HELP_BUNDLE_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_HEADERS_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_LOG_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_EXTRAS_HEADER;
	public static String CONSOLE_HELP_COMMAND_ARGUMENT_DESCRIPTION;
	public static String CONSOLE_HELP_EXEC_COMMAND_DESCRIPTION;
	public static String CONSOLE_HELP_FORK_COMMAND_DESCRIPTION;
	public static String STARTLEVEL_HELP_HEADING;
	public static String CONSOLE_HELP_OPTIONAL_IDLOCATION_ARGUMENT_DESCRIPTION;
	public static String STARTLEVEL_HELP_SL;
	public static String STARTLEVEL_ARGUMENT_DESCRIPTION;
	public static String STARTLEVEL_HELP_SETFWSL;
	public static String STARTLEVEL_IDLOCATION_ARGUMENT_DESCRIPTION;
	public static String STARTLEVEL_HELP_SETBSL;
	public static String STARTLEVEL_HELP_SETIBSL;
	public static String CONSOLE_HELP_PROFILE_HEADING;
	public static String CONSOLE_HELP_PROFILELOG_DESCRIPTION;
	public static String CONSOLE_HELP_UPDATE_COMMAND_DESCRIPTION;
	
	public static String CONSOLE_NO_BUNDLE_SPECIFIED_ERROR;
	public static String CONSOLE_NOTHING_TO_INSTALL_ERROR;
	public static String CONSOLE_BUNDLE_ID_MESSAGE;
	public static String CONSOLE_NO_INSTALLED_BUNDLES_ERROR;
	public static String CONSOLE_REGISTERED_SERVICES_MESSAGE;
	public static String CONSOLE_FRAMEWORK_IS_LAUNCHED_MESSAGE;
	public static String CONSOLE_FRAMEWORK_IS_SHUTDOWN_MESSAGE;
	public static String CONSOLE_ID;
	public static String CONSOLE_BUNDLE_LOCATION_MESSAGE;
	public static String CONSOLE_STATE_BUNDLE_FILE_NAME_HEADER;
	public static String CONSOLE_BUNDLES_USING_SERVICE_MESSAGE;
	public static String CONSOLE_NO_REGISTERED_SERVICES_MESSAGE;
	public static String CONSOLE_NO_BUNDLES_USING_SERVICE_MESSAGE;
	public static String CONSOLE_REGISTERED_BY_BUNDLE_MESSAGE;
	public static String CONSOLE_IMPORTS_MESSAGE;
	public static String CONSOLE_STALE_MESSAGE;
	public static String CONSOLE_NO_EXPORTED_PACKAGES_NO_PACKAGE_ADMIN_MESSAGE;
	public static String CONSOLE_NO_EXPORTED_PACKAGES_MESSAGE;
	public static String CONSOLE_REMOVAL_PENDING_MESSAGE;
	public static String CONSOLE_SERVICES_IN_USE_MESSAGE;
	public static String CONSOLE_NO_SERVICES_IN_USE_MESSAGE;
	public static String CONSOLE_ID_MESSAGE;
	public static String CONSOLE_STATUS_MESSAGE;
	public static String CONSOLE_DATA_ROOT_MESSAGE;
	
	public static String CONSOLE_IMPORTED_PACKAGES_MESSAGE;
	public static String CONSOLE_NO_IMPORTED_PACKAGES_MESSAGE;
	public static String CONSOLE_HOST_MESSAGE;
	public static String CONSOLE_EXPORTED_PACKAGES_MESSAGE;
	public static String CONSOLE_EXPORTED_REMOVAL_PENDING_MESSAGE;
	public static String CONSOLE_EXPORTED_MESSAGE;
	public static String CONSOLE_NO_HOST_MESSAGE;
	public static String CONSOLE_FRAGMENT_MESSAGE;
	public static String CONSOLE_NO_FRAGMENT_MESSAGE;
	public static String CONSOLE_NO_NAMED_CLASS_SPACES_MESSAGE;
	public static String CONSOLE_NAMED_CLASS_SPACE_MESSAGE;
	public static String CONSOLE_PROVIDED_MESSAGE;
	public static String CONSOLE_REQUIRED_BUNDLES_MESSAGE;
	public static String CONSOLE_NO_REQUIRED_BUNDLES_MESSAGE;
	public static String CONSOLE_DEBUG_MESSAGE;
	public static String CONSOLE_INFO_MESSAGE;
	public static String CONSOLE_WARNING_MESSAGE;
	public static String CONSOLE_ERROR_MESSAGE;
	public static String CONSOLE_LOGSERVICE_NOT_REGISTERED_MESSAGE;
	public static String CONSOLE_TOTAL_MEMORY_MESSAGE;
	public static String CONSOLE_FREE_MEMORY_BEFORE_GARBAGE_COLLECTION_MESSAGE;
	public static String CONSOLE_FREE_MEMORY_AFTER_GARBAGE_COLLECTION_MESSAGE;
	public static String CONSOLE_MEMORY_GAINED_WITH_GARBAGE_COLLECTION_MESSAGE;
	public static String CONSOLE_FRAMEWORK_LAUNCHED_PLEASE_SHUTDOWN_MESSAGE;
	public static String CONSOLE_INVALID_BUNDLE_SPECIFICATION_ERROR;
	public static String CONSOLE_CAN_NOT_REFRESH_NO_PACKAGE_ADMIN_ERROR;
	public static String CONSOLE_NO_COMMAND_SPECIFIED_ERROR;
	public static String CONSOLE_STARTED_IN_MESSAGE;
	public static String CONSOLE_EXECUTED_RESULT_CODE_MESSAGE;
	public static String CONSOLE_BUNDLE_HEADERS_TITLE;
	public static String CONSOLE_SYSTEM_PROPERTIES_TITLE;
	public static String CONSOLE_NO_PARAMETERS_SPECIFIED_TITLE;
	public static String CONSOLE_SETTING_PROPERTIES_TITLE;
	public static String CONSOLE_STATE_BUNDLE_TITLE;
	public static String CONSOLE_THREADGROUP_TITLE;
	public static String CONSOLE_THREADTYPE_TITLE;
	public static String CONSOLE_REQUIRES_MESSAGE;
	public static String CONSOLE_CAN_NOT_USE_STARTLEVEL_NO_STARTLEVEL_SVC_ERROR;
	public static String CONSOLE_CANNOT_FIND_BUNDLE_ERROR;
	
	
	public static String STARTLEVEL_FRAMEWORK_ACTIVE_STARTLEVEL;
	public static String STARTLEVEL_BUNDLE_STARTLEVEL;
	public static String STARTLEVEL_NO_STARTLEVEL_GIVEN;
	public static String STARTLEVEL_NO_STARTLEVEL_OR_BUNDLE_GIVEN;
	public static String STARTLEVEL_INITIAL_BUNDLE_STARTLEVEL;
	public static String STARTLEVEL_POSITIVE_INTEGER;
	
	
	
	
	static {
		// initialize resource bundles
		NLS.initializeMessages(BUNDLE_NAME, ConsoleMsg.class);
	}
}