/*
 * Copyright (C) 2014 Schlichtherle IT Services & CirrusPoint Solutions, Inc.
 * All rights reserved. Use is subject to license terms.
 */
package org.truelicense.core.io;

import org.truelicense.api.io.Store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * A path store.
 *
 * @author Christian Schlichtherle
 */
public final class PathStore implements Store {

    private final Path path;

    public PathStore(final Path path) { this.path = requireNonNull(path); }

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
    public boolean exists() { return Files.exists(path); }
}
