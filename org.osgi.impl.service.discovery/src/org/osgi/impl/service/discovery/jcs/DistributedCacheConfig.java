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

/**
 * The class provides the configuration properties of the distributed cache.
 * 
 * @author Thomas Kiesslich
 */
public final class DistributedCacheConfig {
    
    /**
     * Private constructor.
     */
    private DistributedCacheConfig() {       
    }
  
    private static final long MS_PER_SECOND = 1000;

    /**
     * The time in seconds of the publish period of a single resource.
     */
    private static final long DEFAULT_PERIOD_PER_RESOURCE_SEC = 60;
    private static long periodPerResourceSec = DEFAULT_PERIOD_PER_RESOURCE_SEC;
    
    /**
     * The additional time reserve of the lease time in seconds.
     */
    private static final long DEFAULT_LEASE_RESERVE_TIME_SEC = 30;
    private static long leaseReserveTimeSec = DEFAULT_LEASE_RESERVE_TIME_SEC;
    
    /**
     * The number of time segments for publishing during the period. 
     */
    private static final int DEFAULT_NUMBER_OF_SEGMENTS = 12;
    private static int numberOfSegments = DEFAULT_NUMBER_OF_SEGMENTS;
       
    /**
     * The interval in seconds of the shrinker of JCS.
     * The shrinker of JCS works like a garbage collector,
     * it searches entries in the cache with expired lease time
     * and removes them.
     */
    public static final int DEFAULT_SHRINKER_INTERVAL_SECONDS = 3600;
    private static int shrinkerIntervalSeconds = DEFAULT_SHRINKER_INTERVAL_SECONDS;
    
    /**
     * regions prefixes.
     */
    public static final String REGION_PREFIX_SERVICES = "services_";
          
    /**
     * Configuration properties.
     */
    public static final String PREFIX_REGION_KEY = "jcs.region.";
    public static final String REGION_VAL = "LTCP";
    
    public static final String JCS_DEFAULT_KEY = "jcs.default";
    public static final String JCS_DEFAULT_VAL = "LTCP";
    
    public static final String JCS_DEFAULT_CACHEATTR_KEY =
        "jcs.default.cacheattributes";
    public static final String JCS_DEFAULT_CACHEATTR_VAL =
        "org.apache.jcs.engine.CompositeCacheAttributes";   
    
    public static final String JCS_DEFAULT_CACHEATTR_MAX_OBJECTS_KEY =
        "jcs.default.cacheattributes.MaxObjects";
    public static final String JCS_DEFAULT_CACHEATTR_MAX_OBJECTS_VAL =
        "100000";  
    
    public static final String JCS_DEFAULT_CACHEATTR_MEMORY_CACHE_NAME_KEY =
        "jcs.default.cacheattributes.MemoryCacheName";
    public static final String JCS_DEFAULT_CACHEATTR_MEMORY_CACHE_NAME_VAL =
        "org.apache.jcs.engine.memory.lru.LRUMemoryCache";  
    
    public static final String JCS_DEFAULT_CACHEATTR_USE_MEMORY_SHRINKER_KEY =
        "jcs.default.cacheattributes.UseMemoryShrinker";
    public static final String JCS_DEFAULT_CACHEATTR_USE_MEMORY_SHRINKER_VAL =
        "true";  
    
    public static final String JCS_DEFAULT_ELEMENTATTR_KEY =
        "jcs.default.elementattributes";
    public static final String JCS_DEFAULT_ELEMENTATTR_VAL =
        "org.apache.jcs.engine.ElementAttributes";   
    
    public static final String JCS_DEFAULT_ELEMENTATTR_IS_ETERNAL_KEY =
        "jcs.default.elementattributes.IsEternal";
    public static final String JCS_DEFAULT_ELEMENTATTR_IS_ETERNAL_VAL =
        "true";   

    public static final String JCS_DEFAULT_ELEMENTATTR_MAX_LIFE_SECONDS_KEY =
        "jcs.default.elementattributes.MaxLifeSeconds";

    public static final String JCS_DEFAULT_CACHEATTR_SHRINKER_INTERVAL_SECONDS_KEY =
        "jcs.default.cacheattributes.ShrinkerIntervalSeconds";   
    
    // TCP Lateral Auxiliary
    public static final String JCS_AUXILIARY_LTCP_KEY =
        "jcs.auxiliary.LTCP";    
    public static final String JCS_AUXILIARY_LTCP_ATTR_KEY =
        "jcs.auxiliary.LTCP.attributes";
    public static final String JCS_AUXILIARY_LTCP_ATTR_TCP_LISTENER_PORT_KEY =
        "jcs.auxiliary.LTCP.attributes.TcpListenerPort";    
    public static final String JCS_AUXILIARY_LTCP_ATTR_UDP_DISCOVERY_ENABLED_KEY =
        "jcs.auxiliary.LTCP.attributes.UdpDiscoveryEnabled";
    
    // multicast
    public static final String JCS_AUXILIARY_LTCP_ATTR_UDP_DISCOVERY_ADDR_KEY =
        "jcs.auxiliary.LTCP.attributes.UdpDiscoveryAddr";
    public static final String JCS_AUXILIARY_LTCP_VAL =
        "org.apache.jcs.auxiliary.lateral.socket.tcp.LateralTCPCacheFactory";   
    
    public static final String JCS_AUXILIARY_LTCP_ATTR_UDP_DISCOVERY_PORT_KEY =
        "jcs.auxiliary.LTCP.attributes.UdpDiscoveryPort";
    public static final String JCS_AUXILIARY_LTCP_ATTR_VAL =
        "org.apache.jcs.auxiliary.lateral.socket.tcp.TCPLateralCacheAttributes";
    
    // TCP
    public static final String JCS_AUXILIARY_LTCP_ATTR_TCP_SERVERS_KEY =
        "jcs.auxiliary.LTCP.attributes.TcpServers";

    
    // calculated parameters
    /**
     * Gets the lease time in seconds for the cache.
     * 
     * @return  The lease time in seconds for the cache.
     */
    public static long getLeaseTimeSeconds() {
        return periodPerResourceSec + leaseReserveTimeSec;
    }
    
    /**
     * Gets the time in milliseconds for the scheduler.
     * 
     * @return  The time in milliseconds for the scheduler.
     */
    public static long getSchedulerTimeMs() {
        return MS_PER_SECOND * periodPerResourceSec / numberOfSegments;
    }

    
    // changeable parameters
    /**
     * Gets the time in seconds of the publish period of a single resource.
     * 
     * @return Returns the periodPerResourceSec.
     */
    public static long getPeriodPerResourceSeconds() {
        return periodPerResourceSec;
    }

    /**
     * Sets the time in seconds of the publish period of a single resource.
     * 
     * @param periodPerResourceSeconds  The periodPerResourceSec to set.
     */
    public static void setPeriodPerResourceSeconds(final long periodPerResourceSeconds) {
        
        if (periodPerResourceSeconds < 1) {
            throw new RuntimeException(
                    "DistributedCacheConfig.setPeriodPerResourceSec(), invalid periodPerResourceSec: " 
                    + periodPerResourceSeconds);
        }
        DistributedCacheConfig.periodPerResourceSec = periodPerResourceSeconds;
    }

    /**
     * Gets the additional time reserve of the lease time in seconds.
     * 
     * @return Returns the leaseReserveTimeSec.
     */
    public static long getLeaseReserveTimeSeconds() {
        return leaseReserveTimeSec;
    }

    /**
     * Sets the additional time reserve of the lease time in seconds.
     * 
     * @param leaseReserveTimeSeconds  The leaseReserveTimeSec to set.
     */
    public static void setLeaseReserveTimeSeconds(final long leaseReserveTimeSeconds) {

        if (leaseReserveTimeSeconds < 1) {
            throw new RuntimeException(
                    "DistributedCacheConfig.setLeaseReserveTimeSec(), invalid leaseReserveTimeSec: " 
                    + leaseReserveTimeSeconds);
        }
        DistributedCacheConfig.leaseReserveTimeSec = leaseReserveTimeSeconds;
    }

    /**
     * Gets the number of time segments for publishing during the period.
     * 
     * @return Returns the numberOfSegments.
     */
    public static int getNumberOfSegments() {
        return numberOfSegments;
    }

    /**
     * Sets the number of time segments for publishing during the period.
     * 
     * @param numberOfSegments The numberOfSegments to set.
     */
    public static void setNumberOfSegments(final int numberOfSegments) {
        if (numberOfSegments <= 0) {
            throw new RuntimeException(
                    "DistributedCacheConfig.setNumberOfSegments(), invalid numberOfSegments: " + numberOfSegments);
        }
        DistributedCacheConfig.numberOfSegments = numberOfSegments;
    }

    /**
     * Gets the interval in seconds of the shrinker of JCS.
     * The shrinker of JCS works like a garbage collector,
     * it searches entries in the cache with expired lease time
     * and removes them.
     * 
     * @return Returns the shrinkerIntervalSeconds.
     */
    public static int getShrinkerIntervalSeconds() {
        return DistributedCacheConfig.shrinkerIntervalSeconds;
    }

    /**
     * Sets the interval in seconds of the shrinker of JCS.
     * The shrinker of JCS works like a garbage collector,
     * it searches entries in the cache with expired lease time
     * and removes them.
     * 
     * @param shrinkerIntervalSeconds The shrinkerIntervalSeconds to set.
     */
    public static void setShrinkerIntervalSeconds(final int shrinkerIntervalSeconds) {
        if (shrinkerIntervalSeconds < 1) {
            throw new RuntimeException(
                    "DistributedCacheConfig.setShrinkerIntervalSeconds(), invalid shrinkerIntervalSeconds: " 
                    + shrinkerIntervalSeconds);
        }
        DistributedCacheConfig.shrinkerIntervalSeconds = shrinkerIntervalSeconds;
    }
    
}
