/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.generation;

import global.namespace.truelicense.build.tasks.generation.GenerateSourcesTask;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.context.Context;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static java.lang.String.join;
import static org.codehaus.plexus.util.FileUtils.getFileNames;

abstract class GenerateSourcesMavenTask extends GenerateSourcesTask {

    private static final VersionTool versionTool = new VersionTool();

    @Override
    public Context context() {
        final Context c = super.context();
        c.put("version", versionTool);
        return c;
    }

    abstract MavenProject project();

    @Override
    public Path projectDirectory() {
        return project().getBasedir().toPath();
    }

    protected List<String> list(Path directory, List<String> includes, List<String> excludes) throws IOException {
        return getFileNames(directory.toFile(), join(", ", includes), join(", ", excludes), false);
    }

    protected void addCompileSourceRoot(String path) {
        project().addCompileSourceRoot(path);
    }

    protected void addTestCompileSourceRoot(String path) {
        project().addTestCompileSourceRoot(path);
    }
}
