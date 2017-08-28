/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import java.io.IOException

import net.truelicense.api.codec.Codec
import net.truelicense.api.io.{Store, Transformation}
import net.truelicense.spi.io.Transformer
import org.scalatest.Matchers._
import org.scalatest._

/** @author Christian Schlichtherle */
abstract class CodecTestSuite extends WordSpec {

  def artifact: AnyRef
  def codec: Codec
  def store: Store
  def transformation: Transformation

  "A codec" when {
    "combined with a transformation" should {
      "support round trip I/O" in {
        val artifact = this.artifact
        val codec = this.codec
        val store = Transformer apply transformation to this.store
        store.exists should equal (false)
        intercept[IOException] { store delete () }
        try {
          codec encoder store encode artifact
          val duplicate: AnyRef = codec decoder store decode artifact.getClass
          duplicate should equal (artifact)
          duplicate should not be theSameInstanceAs (artifact)
          store.exists should equal (true)
        } finally {
          store delete ()
        }
        store.exists should equal (false)
        intercept[IOException] { store delete () }
      }
    }
  }
}
