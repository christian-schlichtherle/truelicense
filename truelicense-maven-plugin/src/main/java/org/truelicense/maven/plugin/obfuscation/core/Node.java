/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin.obfuscation.core;

import java.nio.file.Path;

/**
 * @author Christian Schlichtherle
 */
final class Node {

    private final Path base, file;

    Node(final Path base) {
        this.base = this.file = base;
    }

    private Node(final Path base, final Path resolvedMember) {
        this.base = base;
        this.file = resolvedMember;
    }

    Node updateFile(final Path resolvedFile) {
        assert base.toString().isEmpty() && !resolvedFile.isAbsolute() || resolvedFile.startsWith(base);
        return new Node(base, resolvedFile);
    }

    Path file() { return file; }

    String path() { return base.relativize(file).toString(); }
}
