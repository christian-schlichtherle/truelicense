/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.spi.io;

import net.truelicense.api.io.Store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.OptionalLong;

import static java.util.Objects.requireNonNull;

/**
 * A path store.
 *
 * @author Christian Schlichtherle
 */
final class PathStore implements Store {

    private final Path path;

    PathStore(final Path path) { this.path = requireNonNull(path); }

    @Override
    public InputStream input() throws IOException {
        return Files.newInputStream(path);
    }

    @Override
    public OutputStream output() throws IOException {
        return Files.newOutputStream(path);
    }

    @Override
    public void delete() throws IOException {
        Files.delete(path);
    }

    @Override
    public OptionalLong size() throws IOException {
        try {
            return OptionalLong.of(Files.size(path));
        } catch (NoSuchFileException ignored) {
            return OptionalLong.empty();
        }
    }

    @Override
    public boolean exists() { return Files.exists(path); }
}
