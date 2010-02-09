/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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
package org.osgi.test.cases.webcontainer.util.validate;

import org.osgi.framework.Version;

/**
 * @version $Rev$ $Date$
 * 
 */
public class VersionRange {

    private String version;
    private Version minimumVersion;
    private Version maximumVersion;
    private boolean minimumExclusive;
    private boolean maximumExclusive;

    /**
     * 
     * @param version
     *            version for the VersionRange
     */
    public VersionRange(String version) {
        this.version = version;
        processVersionAttribute(this.version);
    }

    /**
     * 
     * @param version
     *            version for the VersionRange
     * @param exactVersion
     *            whether this is an exact version
     */
    public VersionRange(String version, boolean exactVersion) {
        ;
        this.version = version;
        if (exactVersion) {
            processExactVersionAttribute(this.version);
        } else {
            processVersionAttribute(this.version);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.aries.application.impl.VersionRange#toString()
     */
    @Override
    public String toString() {
        return this.version;
    }

    // simply compare VersionRange.version won't work as version can be [2.0, 10.0) 
    // and [2.0,10.0.0) and they should return true here
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null)
            return false;

        if (!(other instanceof VersionRange)) {
            return false;
        }

        VersionRange otherVersion = (VersionRange) other;
        if (minimumExclusive != otherVersion.minimumExclusive) {
            return false;
        }
        if (!minimumVersion.equals(otherVersion.minimumVersion)) {
            return false;
        }
        if (maximumExclusive != otherVersion.maximumExclusive) {
            return false;
        }
        if (maximumVersion == null) {
            return (otherVersion.maximumVersion == null);
        } else {
            if (otherVersion.maximumVersion == null) {
                return false;
            } else {
                return maximumVersion.equals(otherVersion.maximumVersion);
            }
        }

    }

    public Version getExactVersion() {
        Version v = null;
        if (isExactVersion()) {
            v = getMinimumVersion();
        }
        return v;
    }

    public Version getMaximumVersion() {
        return maximumVersion;
    }

    public Version getMinimumVersion() {
        return minimumVersion;
    }

    // process version attributes that contain , in it
    private void processVersionAttribute(String version) {
        int value = version.indexOf(",");
        if (value > 0) {
            // ok version contains range
            // need to check if starts with ( [ and ends with )]
            if (((version.startsWith("[") || version.startsWith("(")) && (version
                    .endsWith(")") || version.endsWith("]")))) {
                minimumVersion = Version.parseVersion(version.substring(1, value)
                        .trim());
                maximumVersion = Version.parseVersion(version.substring(value + 1,
                        version.length() - 1).trim());
                if (version.startsWith("[")) {
                    minimumExclusive = true;
                } else {
                    minimumExclusive = false;
                }
    
                if (version.endsWith("]")) {
                    maximumExclusive = false;
                } else {
                    maximumExclusive = true;
                }
            } else {
                throw new IllegalArgumentException("unable to parse version: "
                        + version + " as it is invalid");
            }
        } else {
            // in this case maximumVersion = null
            minimumVersion = Version.parseVersion(version.trim());
            minimumExclusive = false;
        }
    }

    // process version attributes that contain , in it
    private void processExactVersionAttribute(String version) {
        minimumVersion = Version.parseVersion(version);
        maximumVersion = minimumVersion;
        minimumExclusive = false;
        maximumExclusive = false;
    }

    public boolean isExactVersion() {
        return minimumVersion.equals(maximumVersion)
                && minimumExclusive == maximumExclusive && !!!minimumExclusive;
    }

}
