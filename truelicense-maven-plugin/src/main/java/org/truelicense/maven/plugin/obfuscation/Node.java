/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin.obfuscation;

import java.nio.file.Path;

/**
 * @author Christian Schlichtherle
 */
final class Node {

    private final String path;
    private final Path file;

    Node(final String path, final Path file) {
        this.path = path;
        this.file = file;
    }

    Node(Node node, String member) {
        path = resolve(node.path(), member, node.file().getFileSystem().getSeparator());
        file = node.file().resolve(member);
    }

    private static String resolve(String path, String member, String separator) {
        return path.isEmpty() ? member : path + separator + member;
    }

    String path() { return path; }

    Path file() { return file; }
}
