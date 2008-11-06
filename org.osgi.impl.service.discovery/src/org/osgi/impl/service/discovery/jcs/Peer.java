/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.impl.service.discovery.jcs;

import java.io.Serializable;

 /** 
 * This class represents one peer as a combination of host and port.
 * 
 * @version initial 
 *
 * @author Thomas Kiesslich
 *
 */
public class Peer implements Serializable {
    
    /**
     * The <code>serialVersionUID</code> of this class.
     */
	private static final long serialVersionUID = -2860546310979329126L;

	/**
     * The hostname / IP address of the peer.
     */
    private String host;
    
    /**
     * The port of the peer.
     */
    private String port;
    
    /**
     * Constructor with parameters.
     * 
     * @param host  The hostname/IPaddress
     * @param port  The port
     */
    public Peer(final String host, final String port) {
        super();
        this.host = host;
        this.port = port;
    }
    
    /**
     * Parameterless constructor.
     */
    public Peer() {
    }

    /**
     * @return Returns the host.
     */
    public String getHost() {
        return host;
    }
    
    /**
     * @param host The host to set.
     */
    public void setHost(final String host) {
        this.host = host;
    }
    
    /**
     * @return Returns the port.
     */
    public String getPort() {
        return port;
    }
    
    /**
     * @param port The port to set.
     */
    public void setPort(final String port) {
        this.port = port;
    }

    /** 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        return result;
    }

    /** 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Peer other = (Peer) obj;
        if (host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (port == null) {
            if (other.port != null) {
                return false;
            }
        } else if (!port.equals(other.port)) {
            return false;
        }
        return true;
    }
    
    /** 
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        Peer clone = new Peer(new String(this.host), new String(this.port));
        return clone;
    }
    
    /** 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return (this.host + ":" + this.port);
    }
}
