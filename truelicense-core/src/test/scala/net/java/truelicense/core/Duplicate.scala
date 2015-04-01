/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core

import net.java.truelicense.core.io.MemoryStore
import net.java.truelicense.core.codec.SerializationCodec

object Duplicate {

  private[this] val serializationCodec = new SerializationCodec

  def viaSerialization[A <: AnyRef](obj: A): A = {
    val store = new MemoryStore
    serializationCodec encode (store, obj)
    serializationCodec decode (store, obj.getClass)
  }
}
