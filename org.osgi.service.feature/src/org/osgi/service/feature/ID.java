/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.service.feature;

import java.util.Objects;

import org.osgi.annotation.versioning.ProviderType;

/**
 * ID used to denote an artifact. This could be a feature model, a bundle which is part of the feature model
 * or some other artifact. <p>
 *
 * Artifact IDs follow the Maven convention of having:
 * <ul>
 * <li>A group ID
 * <li>An artifact ID
 * <li>A version
 * <li>A type identifier (optional)
 * <li>A classifier (optional)
 * </ul>
 * @ThreadSafe
 */
@ProviderType
public class ID {
    private final String groupId;
    private final String artifactId;
    private final String version; // The Artifact Version may not follow OSGi version rules
    private final String type;
    private final String classifier;

    /**
	 * Construct an Artifact ID from a Maven ID. Maven IDs have the following
	 * syntax:
	 * <p>
	 * {@code group-id ':' artifact-id [ ':' [type] [ ':' classifier ] ] ':' version}
	 * 
	 * @param mavenID
	 * @return The ID
	 */
    public static ID fromMavenID(String mavenID) {
        String[] parts = mavenID.split(":");

        if (parts.length < 3 || parts.length > 5)
            throw new IllegalArgumentException("Not a valid maven ID" + mavenID);

        String gid = parts[0];
        String aid = parts[1];
        String ver = parts[2];
        String t = parts.length > 3 ? parts[3] : null;
        String c = parts.length > 4 ? parts[4] : null;

        return new ID(gid, aid, ver, t, c);
    }

    /**
     * Construct an Artifact ID
     * @param groupId The group ID.
     * @param artifactId The artifact ID.
     * @param version The version.
     */
    public ID(String groupId, String artifactId, String version) {
        this(groupId, artifactId, version, null, null);
    }

    /**
     * Construct an Artifact ID
     * @param groupId The group ID.
     * @param artifactId The artifact ID.
     * @param version The version.
     * @param type The type identifier.
     * @param classifier The classifier.
     */
    public ID(String groupId, String artifactId, String version, String type, String classifier) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.classifier = classifier;
    }

    /**
     * Get the group ID.
     * @return The group ID.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Get the artifact ID.
     * @return The artifact ID.
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Get the version.
     * @return The version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the type identifier.
     * @return The type identifier.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the classifier.
     * @return The classifier.
     */
    public String getClassifier() {
        return classifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactId, classifier, groupId, type, version);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ID))
            return false;
        ID other = (ID) obj;
        return Objects.equals(artifactId, other.artifactId) && Objects.equals(classifier, other.classifier)
                && Objects.equals(groupId, other.groupId) && Objects.equals(type, other.type)
                && Objects.equals(version, other.version);
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }
}
