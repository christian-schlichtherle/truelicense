/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core.io

import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.truelicense.api.License
import org.truelicense.it.core.TestContext
import org.truelicense.spi.io.TransformationBuilder

/** @author Christian Schlichtherle */
abstract class TransformationTestSuite extends WordSpec { this: TestContext[_] =>

  "The Transformations class" when {
    "given a store" should {
      "apply the compression and encryption transformations in order" in {
        val originalStore = store
        val transformedStore = TransformationBuilder apply compression then encryption to originalStore
        val codec = this.codec
        val original = license
        codec encoder transformedStore encode original
        val duplicate: License = codec decoder transformedStore decode classOf[License]
        duplicate should not be theSameInstanceAs (original)
        duplicate should equal (original)
      }
    }
  }
}
