package global.namespace.truelicense.build.tasks.generation;

import global.namespace.neuron.di.java.Neuron;

import java.nio.file.Path;
import java.util.List;

import static global.namespace.neuron.di.java.CachingStrategy.NOT_THREAD_SAFE;
import static java.lang.String.join;

@SuppressWarnings("WeakerAccess")
@Neuron(cachingStrategy = NOT_THREAD_SAFE)
public abstract class PathSet {

    public abstract Path directory();

    public abstract List<String> excludes();

    public abstract List<String> includes();

    public String toString() {
        return  "{ \"directory\": " + directory() +
                ", \"includes\": [" + join(", ", includes()) + "]" +
                ", \"excludes\": [" + join(", ", excludes()) + "]" +
                "}";
    }
}
