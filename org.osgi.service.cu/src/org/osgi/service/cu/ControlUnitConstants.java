/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.cu;

/**
 * This interface defines constants used as service registration properties and
 * metadata attribute keys in the scope of Control Unit API.
 * 
 * @version $Revision$
 */
public interface ControlUnitConstants {

  /**
   * The <code>ID</code> property uniquely identifies a control unit instance
   * in the scope of its {@link #TYPE}. Therefore, the pair <code>(TYPE, ID)</code>
   * uniquely identifies a single control unit within the scope of the current
   * OSGi framework.<br>
   * 
   * The id is a service registration property. <br>
   * 
   * The value of this constant is <code>"osg.control.id"</code>.
   */
  public static final String ID = "osg.control.id";

  /**
   * The <code>TYPE</code> of a control unit specifies its type. There may be
   * many control units of the same type. All control units of one type have a
   * same set of state variable ids and a same set of supported actions
   * specified by the <code>ObjectClassDefinition</code> with the same id.
   * 
   * The type is a service registration property. <br>
   * 
   * The value of this constant is <code>"osg.control.type"</code>.
   * 
   * @see org.osgi.service.metatype.ObjectClassDefintion#getID()
   */
  public static final String TYPE = "osg.control.type";

  /**
   * The <code>VERSION</code> property, when set, specifies the version of the
   * meta-typing information used for unit description. <br>
   * 
   * The version is a service registration property. <br>
   * 
   * The value of this constant is <code>"osg.control.version"</code>.
   */
  public static final String VERSION = "osg.control.version";

  /**
   * Property specifying the id of a parent control unit. <br>
   * 
   * The parent id is a service registration property. <br>
   * 
   * The value of this constant is <code>"osg.control.parent.id"</code>.
   */
  public static final String PARENT_ID = "osg.control.parent.id";

  /**
   * Property specifying the type of the parent control unit. <br>
   * 
   * The parent type is a service registration property. <br>
   * 
   * The value of this constant is <code>"osg.control.parent.type"</code>.
   */
  public static final String PARENT_TYPE = "osg.control.parent.type";

  /**
   * {@link StateVariableListener}s, which wishes to receive upon registration the 
   * values of the state variables for which they are listening, should have this
   * property set in their service registration properties (the value of the
   * property doesn't matter). If a listener has registered with such property
   * and a new control unit with variables matching the filter of the listener
   * is registered it will receive the values of these variables. <br>
   * 
   * The value of this constant is <code>"osg.control.event.auto_receive"</code>.
   */
  public static final String EVENT_AUTO_RECEIVE = "osg.control.event.auto_receive";

  /**
   * This service registration property may be used by the
   * {@link org.osgi.service.cu.admin.ControlUnitAdminListener}s,
   * {@link org.osgi.service.cu.StateVariableListener}s and
   * {@link org.osgi.service.cu.admin.HierarchyListener}s to specify the events
   * they are interested in. <br>
   * 
   * The value of this constants is <code>"osg.control.event.filter"</code>.
   */
  public static final String EVENT_FILTER = "osg.control.event.filter";

  /**
   * This service registration property may be used by the
   * {@link org.osgi.service.cu.admin.ControlUnitAdminListener}s,
   * {@link org.osgi.service.cu.StateVariableListener}s and
   * {@link org.osgi.service.cu.admin.HierarchyListener}s to specify they whish
   * to be receive events synchronously. <br>
   * 
   * The value of the property doesn't matter - if it's present events will be
   * delivered synchronously to the corresponding listener. Otherwise events are
   * delivered asynchronously. <br>
   * 
   * The value of this constant is <code>"osg.control.event.sync"</code>
   */
  public static final String EVENT_SYNC = "osg.control.event.sync";

  /**
   * This property may be used in the event filters of
   * {@link org.osgi.service.cu.StateVariableListener}s to specify the state
   * variables, which the listeners are interested in. <br>
   * 
   * The value of this constant is <code>"osg.control.var.id"</code>.
   */
  public static final String STATE_VARIABLE_ID = "osg.control.var.id";

  /**
   * Control units must have state variable with this ID, which value should be
   * a string array containing the IDs of all other control unit's state
   * variables. <br>
   * 
   * The value of this constant is <code>"osg.control.var.list_sv"</code>.
   */
  public static final String STATE_VARIABLES_LIST = "osg.control.var.list_sv";

}
