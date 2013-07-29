
package org.osgi.service.resource;


/**
 * A ResourceThreshold specifies a threshold for a type of resource. Each
 * threshold is composed of :
 * <ul>
 * <li>a type which is either upper or lower threshold (see {@link #isUpper()})</li>
 * <li>a WARNING threshold value (see {@link #getWarningThreshold()}).</li>
 * <li>an ERROR threshold value (see {@link #getErrorThreshold()}).</li>
 * <li>a state (see {@link #getState()}).</li>
 * </ul>
 * <p>
 * The ResourceThreshold instance evaluates the new state (either
 * {@link #NORMAL_STATE} or {@link #WARNING_STATE} or {@link #ERROR_STATE}). In
 * the case when the new state is different of the previous state, a
 * {@link ResourceEvent} is generated. The next table summarizes the allowed
 * transition and the type of {@link ResourceEvent} instance to be generated:
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
 * <td>NORMAL ResourceEvent type</td>
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
	public static final int	NORMAL_STATE	= 0;

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
	public static final int	WARNING_STATE	= 1;

	/**
	 * A ResourceThreshold instance is in the ERROR state when the current
	 * consumption:
	 * <ul>
	 * <li>either exceeds the ERROR threshold value (maximum threshold)</li>
	 * <li>or is under the ERROR threshold value (minimum threshold)</li>
	 * </ul>
	 */
	public static final int	ERROR_STATE		= 2;

	/**
	 * Returns the current state of the ResourceThreshold.
	 * 
	 * @return either {@link #NORMAL_STATE} or {@link #WARNING_STATE} or
	 *         {@link #ERROR_STATE}
	 */
	public int getState();

	/**
	 * Return true if the current ResourceThreshold instance defines an upper
	 * threshold.
	 * 
	 * @return true if the current ResourceThreshold instance is an upper
	 *         threshold.
	 */
	public boolean isUpper();

	/**
	 * Returns the value of the error threshold
	 * 
	 * @return value
	 */
	public Comparable getErrorThreshold();

	/**
	 * Returns the value of the warning threshold
	 * 
	 * @return value
	 */
	public Comparable getWarningThreshold();

	/**
	 * Set the Error threshold value.
	 * 
	 * @param value
	 */
	public void setErrorThreshold(Comparable value);

	/**
	 * Set the Warning threshold value
	 * 
	 * @param value
	 */
	public void setWarningThreshold(Comparable value);


}
