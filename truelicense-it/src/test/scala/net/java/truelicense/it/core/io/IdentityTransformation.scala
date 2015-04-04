/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.core.io

import net.java.truelicense.core.io.{Sink, Source, Transformation}

/** @author Christian Schlichtherle */
object IdentityTransformation extends Transformation {
  override def apply(sink: Sink) = sink
  override def unapply(source: Source) = source
}
