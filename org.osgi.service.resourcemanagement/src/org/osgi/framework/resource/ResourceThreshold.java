package org.osgi.framework.resource;

/**
 * A ResourceThreshold specifies a threshold for a type of resource. They can be
 * retrieved through their related {@link ResourceMonitor} instance by calling
 * {@link ResourceMonitor#getThresholds()}. Each threshold is composed of :
 * <ul>
 * <li>a value (see {@link #getValue()}).</li>
 * <li>a direction (see {@link #getDirection()}).</li>
 * <li>a state (see {@link #getState()}).</li>
 * </ul>
 * <p>
 * The {@link #notifyCurrentConsumption(Object)} method will be called
 * periodically by the {@link ResourceMonitor} instance. The ResourceThreshold
 * instance then evaluates the new state (either {@link #NORMAL_STATE} or
 * {@link #WARNING_STATE} or {@link #ERROR_STATE}). In the case when the new
 * state is different of the previous state, a {@link ResourceEvent} is
 * generated. The next table summarizes the allowed transition and the type of
 * {@link ResourceEvent} instance to be generated:
 * <table>
 * <tr>
 * <th>Initial state</th>
 * <th>Final state</th>
 * <th>Type of ResourceEvent</th>
 * </tr>
 * <tr>
 * <td>NORMAL</td>
 * <td>WARNING</td>
 * <td>WARNING ResourceEvent type</td>
 * </tr>
 * <tr>
 * <td>WARNING</td>
 * <td>ERROR</td>
 * <td>ERROR ResourceEvent type</td>
 * </tr>
 * <tr>
 * <td>ERROR</td>
 * <td>WARNING</td>
 * <td>WARNING ResourceEvent type</td>
 * </tr>
 * <tr>
 * <td>WARNING</td>
 * <td>NORMAL</td>
 * <td>NORMAL ResourceEvent type</td>
 * </tr>
 * <tr>
 * <td>NORMAL</td>
 * <td>ERROR</td>
 * <td>ERROR ResourceEvent type</td>
 * </tr>
 * <tr>
 * <td>ERROR</td>
 * <td>NORMAL</td>
 * <td>ERROR ResourceEvent type</td>
 * </tr>
 * </table>
 * </p>
 * 
 */
public interface ResourceThreshold {



	/**
	 * A ResourceThreshold instance is in the NORMAL state while the current
	 * consumption of resource is either under or above the warning threshold
	 * depending on the type of threshold (minimum or maximum).
	 */
	public static final String NORMAL_STATE = "NORMAL";

	/**
	 * A ResourceThreshold instance is in the WARNING state when the current
	 * consumption:
	 * <ul>
	 * <li>either exceeds the WARNING threshold value AND is under the ERROR
	 * threshold value (maximum threshold)</li>
	 * <li>or is under the WARNING threshold value AND exceeds the ERROR
	 * threshold value (minimum threshold)</li>
	 * </ul>
	 */
	public static final String WARNING_STATE = "WARNING";

	/**
	 * A ResourceThreshold instance is in the ERROR state when the current
	 * consumption:
	 * <ul>
	 * <li>either exceeds the ERROR threshold value (maximum threshold)</li>
	 * <li>or is under the ERROR threshold value (minimum threshold)</li>
	 * </ul>
	 */
	public static final String ERROR_STATE = "ERROR";

	/**
	 * Returns the current state of the ResourceThreshold.
	 * 
	 * @return either {@link #NORMAL_STATE} or {@link #WARNING_STATE} or
	 *         {@link ERROR_STATE}
	 */
	public String getState();

	/**
	 * Return true if WARNING value is less than ERROR value.
	 * 
	 * @return true if WARNING value is less than ERROR value.
	 */
	public boolean isMaximum();

	/**
	 * Returns the value of the error threshold
	 * 
	 * @return value
	 */
	public Object getErrorThreshold();

	/**
	 * Returns the value of the warning threshold
	 * 
	 * @return value
	 */
	public Object getWarningThreshold();

	/**
	 * Set the Error threshold value.
	 * 
	 * @param value
	 */
	public void setErrorThreshold(Object value);

	/**
	 * Set the Warning threshold value
	 * 
	 * @param value
	 */
	public void setWarningThreshold(Object value);


}
