/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

/**
 * An immutable Velocity tool which provides access to Maven artifact meta data.
 *
 * @author Christian Schlichtherle
 */
public final class VersionTool {

    public ArtifactVersion parse(String version) {
        return new DefaultArtifactVersion(version);
    }
}
