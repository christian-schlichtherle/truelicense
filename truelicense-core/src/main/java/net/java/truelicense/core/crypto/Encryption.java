/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.crypto;

import net.java.truelicense.core.io.*;

/**
 * An encryption for the I/O streams provided by {@linkplain Sink sinks} and
 * {@linkplain Source sources}.
 *
 * @author Christian Schlichtherle
 */
public interface Encryption
extends EncryptionParametersProvider, Transformation { }
