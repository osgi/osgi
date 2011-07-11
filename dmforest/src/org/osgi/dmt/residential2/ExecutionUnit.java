package org.osgi.dmt.residential2;

import java.net.*;

import org.osgi.dmt.ddf.*;

/**
 * 
 This table serves as the Execution Unit inventory and contains both status
 * information about each Execution Unit as well as configurable parameters for
 * each Execution Unit. Each {{object|#.DeploymentUnit}} that is installed can
 * have zero or more Execution Units. Once a Deployment Unit is installed it
 * populates this table with its contained Execution Units. When the Deployment
 * Unit (that caused this {{object}} to come into existence) is updated, this
 * instance MAY be removed and new instances MAY come into existence. While the
 * Deployment Unit (that caused this {{object}} to come into existence) is being
 * updated, all {{object}} instances associated with the Deployment Unit will be
 * stopped until the update is complete at which time they will be restored to
 * the state that they were in before the update started. When the Deployment
 * Unit (that caused this {{object}} to come into existence) is uninstalled,
 * this instance is removed. Each {{object}} MAY also contain a set of vendor
 * specific parameters displaying status and maintaining configuration that
 * reside under the {{object|Extensions}} object.
 */
public interface ExecutionUnit {

	/**
	 * Execution Unit Identifier chosen by the {{object|#.ExecEnv}} during
	 * installation of the associated {{object|#.DeploymentUnit}}. The format of
	 * this value is Execution Environment specific, but it MUST be unique
	 * across {{object|#.ExecEnv}} instances. Thus, it is recommended that this
	 * be a combination of the {{param|#.ExecEnv.{i}.Name}} and an Execution
	 * Environment local unique value.
	 */
	String EUID();

	/**
	 * 	 A non-volatile handle used to reference this instance. {{param}} provides
	 * a mechanism for an ACS to label this instance for future reference. An
	 * initial unique value MUST be assigned when the CPE creates an instance of
	 * this {{object}}.
	 */
	String Alias();

	/**
	 * 	 The name of this {{object}} as it pertains to its associated
	 * {{object|#.DeploymentUnit}}, which SHOULD be unique across all {{object}}
	 * instances contained within its associated {{object|#.DeploymentUnit}}.
	 * 
	 */
	String Name();

	/**
	 * 	 The name of this {{object}} as provided by the {{object|#.ExecEnv}},
	 * which SHOULD be unique across all {{object}} instances contained within a
	 * specific {{object|#.ExecEnv}}.
	 */
	String ExecEnvLabel();

	/**
	 * 	 Indicates the status of this {{object}}.
	 * 
	 * 
	 * 
	 * 
	 * Syntax:string
	 */
	String Status();

	/**
	 * 
	 Indicates the state transition that the ACS is requesting for this
	 * {{object}}. {{enum}} If this {{object}} is associated with an Execution
	 * Environment that is disabled and an attempt is made to alter this value,
	 * then a CWMP Fault MUST be generated.
	 * 
	 * 
	 * 
	 * 
	 * Syntax:string
	 */
	String RequestedState();

	/**
	 * 
	 If while running or transitioning between states this {{object}}
	 * identifies a fault this parameter embodies the problem. The value of
	 * {{enum|NoFault}} MUST be used when everything is working as intended.
	 * {{enum}} For fault codes not included in this list, the vendor MAY
	 * include vendor-specific values, which MUST use the format defined in
	 * {{bibref|TR-106a4|Section 3.3}}.
	 * 
	 * 
	 * 
	 * 
	 * Syntax:string
	 */
	String ExecutionFaultCode();

	/**
	 * 
	 If while running or transitioning between states this {{object}}
	 * identifies a fault this parameter provides a more detailed explanation of
	 * the problem. If {{param|ExecutionFaultCode}} has the value of
	 * {{enum|NoFault|ExecutionFaultCode}} then the value of this parameter MUST
	 * {{empty}} and ignored by the ACS.
	 * 
	 * 
	 * 
	 * 
	 * Syntax:string [
	 * 
	 * :
	 * 
	 * ]
	 */
	String ExecutionFaultMessage();

	/**
	 * 
	 If {{true}} and the {{param|RunLevel}} verification is also met, then
	 * this {{object}} will be automatically started by the device after its
	 * {{object|#.ExecEnv}} is either rebooted or restarted. If {{false}} this
	 * {{object}} will not be started after its {{object|#.ExecEnv}} is either
	 * rebooted or restarted until it is explicitly commanded to do so by either
	 * the ACS or another Execution Unit.
	 * 
	 * 
	 * 
	 * 
	 * Syntax:boolean
	 */
	boolean AutoStart();

	/**
	 * 
	 Determines when this {{object}} will be started. If {{param|AutoStart}}
	 * is {{true}} and {{param|#.ExecEnv.{i}.CurrentRunLevel}} is greater than
	 * or equal to {{param}}, then this {{object}} will be started. If the value
	 * of {{param|#.ExecEnv.{i}.CurrentRunLevel}} is -1, then the associated
	 * {{object|#.ExecEnv.}} doesn't support Run Levels, thus the value of this
	 * parameter is irrelevant when read and setting its value has no impact to
	 * the Run Level of this {{object}}.
	 * 
	 * 
	 * 
	 * 
	 * Syntax:unsignedInt [
	 * 
	 * :
	 * 
	 * ]
	 */
	long RunLevel();

	/**
	 * 
	 Vendor of this {{object}}.
	 * 
	 * 
	 * 
	 * Syntax:string [
	 * 
	 * :
	 * 
	 * ]
	 */
	String Vendor();

	/**
	 * 
	 Version of the {{object}}. The format of this value is Execution
	 * Environment specific.
	 * 
	 * 
	 * 
	 * Syntax:string [
	 * 
	 * :
	 * 
	 * ]
	 */
	String Version();

	/**
	 * 
	 Textual description of this {{object}}. The format of this value is
	 * Execution Environment specific.
	 * 
	 * 
	 * 
	 * Syntax:string [
	 * 
	 * :
	 * 
	 * ]
	 */
	String Description();

	/**
	 * 
	 The amount of disk space measured in {{units}} currently being used by
	 * this {{object}}. A value of -1 MUST be used for {{object}} instances
	 * where this parameter is not applicable.
	 * 
	 * 
	 * 
	 * Syntax:int [
	 * 
	 * :
	 * 
	 * ]
	 */
	int DiskSpaceInUse();

	/**
	 * 
	 The amount of physical RAM measured in {{units}} currently being used by
	 * this {{object}}. A value of -1 MUST be used for {{object}} instances
	 * where this parameter is not applicable.
	 * 
	 * 
	 * 
	 * Syntax:int [
	 * 
	 * :
	 * 
	 * ]
	 */
	int MemoryInUse();

	/**
	 * 
	 Represents the instances of multi-instanced objects that are directly
	 * controlled by, and have come into existence because of, this {{object}}.
	 * See {{bibref|TR-157a3|Appendix II.3.2}} for more description and some
	 * examples. NOTE: All other objects and parameters (i.e. not
	 * multi-instanced objects) that this {{object}} has caused to come into
	 * existence can be discovered via the
	 * {{object|.DeviceInfo.SupportedDataModel.{i}.}} table.
	 * 
	 * 
	 * 
	 * 
	 * Syntax:liststring
	 */
	LIST<URI> References();

	/**
	 * 
	 Represents the system processes that are active in the system because of
	 * this {{object}}. If {{param|Status}} is not {{enum|Active|Status}} it is
	 * expected that this list will be {{empty}}. Some {{object}} instances
	 * MIGHT NOT have any system processes irrespective of the value of
	 * {{param|Status}}.
	 * 
	 * 
	 * 
	 * 
	 * Syntax:liststring
	 */
	LIST<String> AssociatedProcessList();

	/**
	 * 
	 Represents the vendor log files that have come into existence because of
	 * this {{object}}. When the {{object|#.DeploymentUnit}} (that caused this
	 * {{object}} to come into existence) is uninstalled the vendor log files
	 * referenced here SHOULD be removed from the CPE. Not all {{object}}
	 * instances will actually have a corresponding vendor log file, in which
	 * case the value of this parameter will be {{empty}}.
	 */
	LIST<String> VendorLogList();

	/**
	 * 
	 Represents the vendor config files that have come into existence because
	 * of this {{object}}. When the {{object|#.DeploymentUnit}} (that caused
	 * this {{object}} to come into existence) is uninstalled the vendor config
	 * files referenced here SHOULD be removed from the CPE. Not all {{object}}
	 * instances will actually have a corresponding vendor config file, in which
	 * case the value of this parameter will be {{empty}}.
	 * 
	 * 
	 * 
	 * 
	 * Syntax:liststring
	 */
	LIST<String> VendorConfigList();

	/**
	 * 
	 Represents the CWMP-DT schema instances that have been introduced to this
	 * device because of the existence of this {{object}}.
	 * 
	 * 
	 * 
	 * Syntax:liststring
	 */
	LIST<String> SupportedDataModelList();

	/**
	 * 
	 Represents the {{object|#.ExecEnv}} that this {{object}} is associated
	 * with.
	 * 
	 * 
	 * 
	 * Syntax:string
	 */
	LIST<URI> ExecutionEnvRef();

	/**
	 * 
	 */
	Extension Extensions(); 
}
