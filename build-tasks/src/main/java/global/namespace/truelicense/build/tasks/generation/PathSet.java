package global.namespace.truelicense.build.tasks.generation;

import java.nio.file.Path;
import java.util.List;

import static java.lang.String.join;

@SuppressWarnings("WeakerAccess")
public final class PathSet {

    private final Path directory;
    private final List<String> includes;
    private final List<String> excludes;

    public PathSet(final Path directory, final List<String> includes, final List<String> excludes) {
        this.directory = directory;
        this.includes = includes;
        this.excludes = excludes;
    }

    public Path directory() {
        return directory;
    }

    public List<String> excludes() {
        return excludes;
    }

    public List<String> includes() {
        return includes;
    }

    public String toString() {
        return  "{ \"directory\": " + directory +
                ", \"includes\": [" + join(", ", includes) + "]" +
                ", \"excludes\": [" + join(", ", excludes) + "]" +
                "}";
    }
}
