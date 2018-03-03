/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import java.io.IOException

import net.truelicense.api.codec.Codec
import global.namespace.fun.io.api.{Store, Transformation}
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
        val store = this.store map transformation
        store.exists shouldBe false
        intercept[IOException] { store delete () }
        try {
          codec connect store encode artifact
          val duplicate: AnyRef = codec connect store decode artifact.getClass
          duplicate shouldBe artifact
          duplicate should not be theSameInstanceAs (artifact)
          store.exists shouldBe true
        } finally {
          store delete ()
        }
        store.exists shouldBe false
        intercept[IOException] { store delete () }
      }
    }
  }
}
