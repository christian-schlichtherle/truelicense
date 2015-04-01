package net.java.truelicense.maven.plugin;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import javax.annotation.concurrent.Immutable;

/**
 * @author Christian Schlichtherle
 */
@Immutable
public final class VersionTool {

    public ArtifactVersion parse(String version) {
        return new DefaultArtifactVersion(version);
    }
}
