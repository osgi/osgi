/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
   * The ID is a service registration property. <br>
   * 
   * The value of this constant is <code>"org.osgi.control.id"</code>.
   */
  public static final String ID = "org.osgi.control.id";

  /**
   * The <code>TYPE</code> of a control unit specifies its type. There may be
   * many control units of the same type. All control units of one type have a
   * same set of state variable ids and a same set of supported actions
   * specified by the <code>ObjectClassDefinition</code> with the same ID.
   * 
   * The type is a service registration property. <br>
   * 
   * The value of this constant is <code>"org.osgi.control.type"</code>.
   * 
   * @see org.osgi.service.metatype.ObjectClassDefintion#getID()
   */
  public static final String TYPE = "org.osgi.control.type";

  /**
   * The <code>VERSION</code> property, when set, specifies the version of the
   * meta-typing information used for unit description. <br>
   * 
   * The version is a service registration property. <br>
   * 
   * The value of this constant is <code>"org.osgi.control.version"</code>.
   */
  public static final String VERSION = "org.osgi.control.version";

  /**
   * Property specifying the ID of a parent control unit. <br>
   * 
   * The parent ID is a service registration property. <br>
   * 
   * The value of this constant is <code>"org.osgi.control.parent.id"</code>.
   */
  public static final String PARENT_ID = "org.osgi.control.parent.id";

  /**
   * Property specifying the type of the parent control unit. <br>
   * 
   * The parent type is a service registration property. <br>
   * 
   * The value of this constant is <code>"org.osgi.control.parent.type"</code>.
   */
  public static final String PARENT_TYPE = "org.osgi.control.parent.type";

  /**
   * {@link StateVariableListener}s, wishing to receive upon registration the 
   * values of the state variables for which they are listening, should have this
   * property set in their service registration properties (the value of the
   * property doesn't matter). If a listener has registered with such property
   * and a new control unit with variables matching the filter of the listener
   * is registered it will receive the values of these variables. <br>
   * 
   * The value of this constant is <code>"org.osgi.control.event.auto_receive"</code>.
   */
  public static final String EVENT_AUTO_RECEIVE = "org.osgi.control.event.auto_receive";

  /**
   * This service registration property may be used by the
   * {@link org.osgi.service.cu.admin.ControlUnitAdminListener}s,
   * {@link org.osgi.service.cu.StateVariableListener}s and
   * {@link org.osgi.service.cu.admin.HierarchyListener}s to filter the events
   * they are interested in. <br>
   * 
   * The value of this constant is <code>"org.osgi.control.event.filter"</code>.
   */
  public static final String EVENT_FILTER = "org.osgi.control.event.filter";

  /**
   * This service registration property may be used by the
   * {@link org.osgi.service.cu.admin.ControlUnitAdminListener}s,
   * {@link org.osgi.service.cu.StateVariableListener}s and
   * {@link org.osgi.service.cu.admin.HierarchyListener}s to specify that 
   * events will be sent to them synchronously. <br>
   * 
   * The value of the property doesn't matter - if it's present events will be
   * delivered synchronously to the corresponding listener. Otherwise events are
   * delivered asynchronously. <br>
   * 
   * The value of this constant is <code>"org.osgi.control.event.sync"</code>
   */
  public static final String EVENT_SYNC = "org.osgi.control.event.sync";

  /**
   * This property may be used in the event filters of
   * {@link org.osgi.service.cu.StateVariableListener}s to specify the state
   * variables, which the listeners are interested in. <br>
   * 
   * The value of this constant is <code>"org.osgi.control.var.id"</code>.
   */
  public static final String STATE_VARIABLE_ID = "org.osgi.control.var.id";

  /**
   * Control units must have state variable with ID equal to this constant, 
   * which value should be a string array containing the IDs of all other 
   * control unit's state variables.  
   * 
   * <P>
   * The <code>ControlUnitAdmin</code> uses the state variable with this ID 
   * when it has to send initial state variables values to 
   * {@link StateVariableListener}s, which have registered with the 
   * {@link #EVENT_AUTO_RECEIVE} service registration property.<br>
   * 
   * The value of this constant is <code>"org.osgi.control.var.list"</code>.
   */
  public static final String STATE_VARIABLES_LIST = "org.osgi.control.var.list";

}
