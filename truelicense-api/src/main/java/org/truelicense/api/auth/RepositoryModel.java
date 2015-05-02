/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

/**
 * A marker interface for storing authenticated objects (artifacts).
 * A repository model is used by a
 * {@linkplain Repository repository controller} and must be serializable by
 * a {@linkplain org.truelicense.api.codec.Codec codec}.
 * A repository model is created by a
 * {@linkplain RepositoryContext repository context}.
 * <p>
 * This marker interface provides type safety for deserialization (some codecs
 * may require this).
 * It does not specify any properties because that's an implementation detail
 * of the respective {@linkplain Repository repository controller}.
 *
 * @author Christian Schlichtherle
 */
public interface RepositoryModel { }
