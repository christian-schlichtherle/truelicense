/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

import java.io.IOException

import org.scalatest.Matchers._
import org.scalatest._
import org.slf4j.LoggerFactory
import org.truelicense.it.core.CodecTestSuite._
import org.truelicense.it.core.io.IdentityTransformation
import org.truelicense.spi.codec.Codecs
import org.truelicense.spi.io.MemoryStore

/** @author Christian Schlichtherle */
abstract class CodecTestSuite extends WordSpec { this: TestContext =>

  def artifact: AnyRef = license

  "A codec" when {
    "combined with a transformation" should {
      "support round trip I/O" in {
        val orig = this.artifact
        val codec = this.codec
        val store = this.store
        store.exists should equal (false)
        intercept[IOException] { store delete () }
        try {
          codec to (transformation apply store) encode orig
          val duplicate: AnyRef = codec from (transformation unapply store) decode orig.getClass
          store match {
            case ms: MemoryStore =>
              import collection.JavaConverters._
              (Codecs charset codec).asScala match {
                case Seq(charset) if IdentityTransformation == transformation =>
                  logger debug ("\n{}", new String(ms.data, charset))
                case _ =>
                  logger debug ("Created BLOB with {} bytes size.", ms.data.length)
              }
            case _ =>
          }
          duplicate should equal (orig)
          duplicate should not be theSameInstanceAs (orig)
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

/** @author Christian Schlichtherle */
object CodecTestSuite {
  private val logger = LoggerFactory getLogger classOf[CodecTestSuite]
}
