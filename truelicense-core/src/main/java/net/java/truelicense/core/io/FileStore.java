/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core.io;

import java.io.*;
import static net.java.truelicense.core.util.Objects.*;

/**
 * A file store.
 *
 * @author Christian Schlichtherle
 */
public final class FileStore implements Store {

    private final File file;

    public FileStore(final File file) { this.file = requireNonNull(file); }

    @Override public InputStream input() throws IOException {
        return new FileInputStream(file);
    }

    @Override public OutputStream output() throws IOException {
        return new FileOutputStream(file);
    }

    @Override public void delete() throws IOException {
        if (!file.delete())
            throw new FileNotFoundException(file + " (cannot delete)");
    }

    @Override public boolean exists() { return file.exists(); }
}
