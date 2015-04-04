/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.maven.plugin.obfuscation;

import de.schlichtherle.truezip.file.TFile;
import java.io.File;

/**
 * @author Christian Schlichtherle
 */
final class Node {

    private final String path;
    private final File file;

    Node(final String path, final File file) {
        this.path = path;
        this.file = file;
    }

    Node(Node node, String member) {
        path = resolve(node.path(), member);
        file = new TFile(node.file(), member);
    }

    private static String resolve(String path, String member) {
        return path.isEmpty() ? member : path + File.separatorChar + member;
    }

    String path() { return path; }

    File file() { return file; }
}
