/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

import java.io.IOException

import org.scalatest.Matchers._
import org.scalatest._
import org.truelicense.spi.io.TransformationBuilder

/** @author Christian Schlichtherle */
abstract class CodecTestSuite extends WordSpec { this: TestContext[_] =>

  def artifact: AnyRef = license

  "A codec" when {
    "combined with a transformation" should {
      "support round trip I/O" in {
        val codec = this.codec
        val artifact = this.artifact
        val store = TransformationBuilder apply transformation to this.store
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
