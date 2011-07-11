package org.osgi.dmt.residential2;

import java.net.*;

import org.osgi.dmt.ddf.*;

/**
 * 
 The Execution Environments that are available on the device, along with their
 * properties and configurable settings.
 */
public interface ExecEnv {


	/**
	 * 
	 Setting this parameter to {{true}} causes this {{object}} to revert back
	 * to the state it was in when the device last issued a "0 BOOTSTRAP" Inform
	 * event. The following requirements dictate what MUST happen for the reset
	 * to be complete: # All Deployment Units that were installed after the last
	 * "0 BOOTSTRAP" Inform event MUST be removed # All persistent storage,
	 * configuration files, and log files that were associated with the removed
	 * Deployment Units MUST be removed # Any Deployment Unit that is still
	 * installed against the Execution Environment MUST be restored to the
	 * version present when the last "0 BOOTSTRAP" Inform event was issued # Any
	 * Deployment Unit that was present when the last "0 BOOTSTRAP" Inform event
	 * was issued, but was subsequently uninstalled and is now not present, MUST
	 * be installed with the version that was present when the last
	 * "0 BOOTSTRAP" Inform event was issued # The Execution Environment MUST be
	 * restored to the version and configuration present when the last
	 * "0 BOOTSTRAP" Inform event was issued # The Execution Environment MUST be
	 * restarted after all other restoration requirements have been met
	 */
	boolean Reset();

	/**
	 * A Name provided by the CPE that adequately distinguishes this {{object}}
	 * from all other {{object}} instances.
	 */
	String Name();

	/**
	 * 	 Indicates the complete type and specification version of this {{object}}.
	 */
	String Type();

	/**
	 * 
	 The run level that this {{object}} will be in upon startup (whether that
	 * is caused by a CPE Boot or the Execution Environment starting). Run
	 * levels dictate which Execution Units will be started. Execution Units
	 * will be started if {{param|CurrentRunLevel}} is greater than or equal to
	 * {{param|#.ExecutionUnit.{i}.RunLevel}} and
	 * {{param|#.ExecutionUnit.{i}.AutoStart}} is {{true}}. If the value of
	 * {{param|CurrentRunLevel}} is -1, then the value of this parameter is
	 * irrelevant when read and setting its value has no impact on the Run Level
	 * of this {{object}}.
	 */
	long InitialRunLevel();

	/**
	 * 
	 Provides a mechanism to remotely manipulate the run level of this
	 * {{object}}, meaning that altering this parameter's value will change the
	 * value of the {{param|CurrentRunLevel}}. Run levels dictate which
	 * Execution Units will be started. Execution Units will be started if
	 * {{param|CurrentRunLevel}} is greater than or equal to
	 * {{param|#.ExecutionUnit.{i}.RunLevel}} and
	 * {{param|#.ExecutionUnit.{i}.AutoStart}} is {{true}}. Setting this value
	 * when {{param|CurrentRunLevel}} is -1 has no impact to the Run Level of
	 * this {{object}}.
	 * 
	 */
	int RequestedRunLevel();

	/**
	 * 
	 The run level that this {{object}} is currently operating in. This value
	 * is altered by changing the {{param|RequestedRunLevel}} parameter. Upon
	 * startup (whether that is caused by a CPE Boot or the Execution
	 * Environment starting) {{param}} will be equal to
	 * {{param|InitialRunLevel}}, unless Run Levels are not supported by this
	 * {{object}} in which case {{param}} will be -1. Run levels dictate which
	 * Execution Units will be started. Execution Units will be started if
	 * {{param}} is greater than or equal to
	 * {{param|#.ExecutionUnit.{i}.RunLevel}} and
	 * {{param|#.ExecutionUnit.{i}.AutoStart}} is {{true}}. If {{param}} is -1
	 * then Run Levels are not supported by this {{object}} and setting
	 * {{param|InitialRunLevel}} or {{param|RequestedRunLevel}} will not impact
	 * the Run Level of this {{object}}.
	 * 
	 */
	int CurrentRunLevel();

	/**
	 * The vendor that produced this {{object}}.
	 */
	String Vendor();

	/**
	 * 	 The Version of this {{object}} as specified by the Vendor that
	 * implemented this {{object}}, not the version of the specification.
	 * 
	 */
	String Version();

	/**
	 * 	 Represents the parent {{object}} of this {{object}}. If this value is
	 * {{empty}} then this is the Primary Execution Environment.
	 */
	String ParentExecEnv();


	MAP<Long,DeploymentUnit> DeploymentUnit(); 
	MAP<Long,ExecutionUnit> ExecutionUnit(); 
}
