/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.crypto;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Transformation;

/**
 * An encryption for the I/O streams provided by {@linkplain Sink sinks} and
 * {@linkplain Source sources}.
 *
 * @author Christian Schlichtherle
 */
public interface Encryption
extends EncryptionParametersProvider, Transformation { }
