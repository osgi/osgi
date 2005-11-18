// TargetProxy.java
// Copyright 1996, 1997 Netscape Communications Corp.  All rights reserved.

package netscape.constructor;

import netscape.util.*;
import netscape.application.*;

/** A TargetProxy is an object that in stored in a Constructor document
  * that represents a real application object. The TargetProxy object can have
  * commands added to it's description while in Constructor. This allows you
  * to define arbitrary commands for the object and connect these commands
  * to specific events in the application. Additionally, like all Constructor
  * objects, they can be named. The purpose of these TargetProxies is to
  * represent some actual application object that responds to these commands.
  * When a .plan file is loaded, a Hashtable containing the real application
  * objects is passed into the Plan constructor. During unarchiving, the
  * TargetProxies are replaced with the real application objects, based on
  * matching the object names. Any objects that would have sent a command to the
  * TargetProxy, instead send the command to the real application
  * object.<BR><BR> There are two standard objects that you are normally
  * interested in, the Application.application() object and the
  * TargetChain.applicationChain() object. These two objects are handled
  * specially by the Plan object. A TargetProxy with the name "__APPLICATION__"
  * will always stand for the application object. A TargetProxy with the name
  * "__TARGETCHAIN__" will always stand for the applicationChain object.
  * These two
  * objects are always replaced when the Plan object loads. You do not have to
  * include them in the TargetProxies hashtable that you pass in on the Plan
  * constructors. The TargetProxy also maintains a type value, indicating if it
  * is the application or the TargetChain proxy. By setting the type to
  * CUSTOM_TYPE you can set the proxyName value to any string, allowing you to
  * replace the proxy with your own custom object.
  * <BR><BR>
  * You normally will not need to create a TargetProxy outside of Constructor.
  * @see Plan
  *
  * @note 1.0b2 Added setAttributesToReplacingTarget for easier subclassing
  */
public class TargetProxy implements Target, Codable {
    Vector              commands;
    TargetProxyManager  manager;
    String              name;
    int                 type;

    Target              _realTarget;

    static final String COMMANDS_VECTOR_KEY = "commands";
    static final String TP_MANAGER_KEY = "manager";
    static final String NAME_KEY = "name";
    static final String TYPE_KEY = "type";

    /** type value indicating Application object.*/
    public final static int    APPLICATION_TYPE = 0;
    /** type value indicating TargetChain object.*/
    public final static int    TARGET_CHAIN_TYPE = 1;
    /** type value indicating a Custom object.*/
    public final static int    CUSTOM_TYPE = 2;

    /* constructors */

    /** Creates a CUSTOM_TYPE TargetProxy */
    public TargetProxy() {
        type = CUSTOM_TYPE;
    }

    /** Creates a CUSTOM_TYPE TargetProxy
      * @private
      */
    public TargetProxy(TargetProxyManager targetProxyManager)    {
        setTargetProxyManager(targetProxyManager);
        type = CUSTOM_TYPE;
    }
    /* methods */

    /** Returns the commands that this TargetProxy is supposed to be able to perform. */
    public Vector commands()    {
        if(commands == null)
            commands = new Vector();
        return commands;
    }

    /** @private */
    public void setCommands(Vector newCommands) {
        commands = newCommands;
    }

    /** @private */
    public TargetProxyManager targetProxyManager()  {
        return manager;
    }

    /** @private */
    public void setTargetProxyManager(TargetProxyManager tpManager) {
        manager = tpManager;
    }

    /** This is the name used to find the real object from the Hashtable
      * passed into the Plan object constructor.
      */
    public String name()    {
        return name;
    }

    /** You can only change the name of CUSTOM_TYPE TargetProxy. */
    public void setName(String value)   {
        if(type == APPLICATION_TYPE)
            name = TargetProxyManager.APPLICATION_TARGET_PROXY_KEY;
        else if(type == TARGET_CHAIN_TYPE)
            name = TargetProxyManager.TARGET_CHAIN_TARGET_PROXY_KEY;
        else
            name = value;
    }

    public int type()    {
        return type;
    }

    /** Sets the type of this object. */
    public void setType(int value)   {
        type = value;
        if(type == APPLICATION_TYPE)
            setName(TargetProxyManager.APPLICATION_TARGET_PROXY_KEY);
        else if(type == TARGET_CHAIN_TYPE)
            setName(TargetProxyManager.TARGET_CHAIN_TARGET_PROXY_KEY);
    }

    /* target */

    /** This object does not actually implement any action here.
      * It will print to System.err a message, if it is ever called.
      */
    public void performCommand(String command, Object anObject) {
        System.err.println(this + " named: " + name
                                +"\n  performCommand( " + command
                             + " ,\n                  " + anObject + " )");
    }


    /* archiving */


    /** Describes the TargetProxy class' coding info.
      * @see Codable#describeClassInfo
      * @private
      */
    public void describeClassInfo(ClassInfo info) {
        info.addClass("netscape.constructor.TargetProxy", 2);
        info.addField(NAME_KEY, Codable.STRING_TYPE);
        info.addField(TP_MANAGER_KEY, Codable.OBJECT_TYPE);
        info.addField(COMMANDS_VECTOR_KEY, Codable.OBJECT_TYPE);
        info.addField(TYPE_KEY, Codable.INT_TYPE);
    }

    /** Encodes the TargetProxy.
      * @see Codable#encode
      * @private
      */
    public void encode(Encoder encoder) throws CodingException {
        encoder.encodeString(NAME_KEY, name);
        encoder.encodeObject(TP_MANAGER_KEY, manager);
        encoder.encodeObject(COMMANDS_VECTOR_KEY, commands);
        encoder.encodeInt(TYPE_KEY, type);
    }

    /** Decodes the TargetProxy.
      * @see Codable#decode
      * @private
      */
    public void decode(Decoder decoder) throws CodingException {

        setName(decoder.decodeString(NAME_KEY));
        setTargetProxyManager((TargetProxyManager)decoder.decodeObject(TP_MANAGER_KEY));

        if(targetProxyManager() != null)    {
            _realTarget = manager.targetNamed(name);
            if(_realTarget != null)  {
                decoder.replaceObject(_realTarget);
                return;
            }
        }

        setCommands((Vector)decoder.decodeObject(COMMANDS_VECTOR_KEY));
        if (decoder.versionForClassName("netscape.constructor.TargetProxy") > 1)
            setType(decoder.decodeInt(TYPE_KEY));
        else
            setType(CUSTOM_TYPE);
    }

    /** Finishes the TargetProxy decoding.  This method does nothing.
      * @see Codable#finishDecoding
      * @private
      */
    public void finishDecoding() throws CodingException {
        if(_realTarget != null)
            setAttributesToReplacingTarget(_realTarget);
        _realTarget = null;
    }

    /** This method is called during the finishDecoding() method.
      * <b>realTarget</b> is the object that has replaced the
      * proxy in the Plan. The current implementation does nothing.
      * This method is only called if the TargetProxy is replaced.
      */
    public void setAttributesToReplacingTarget(Target realTarget) {
    }
}
