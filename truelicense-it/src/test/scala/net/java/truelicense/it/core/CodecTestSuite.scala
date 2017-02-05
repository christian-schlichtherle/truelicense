/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.core

import java.io.IOException

import net.java.truelicense.core.codec._
import net.java.truelicense.core.io._
import net.java.truelicense.it.core.CodecTestSuite._
import net.java.truelicense.it.core.io.IdentityTransformation
import org.scalatest.Matchers._
import org.scalatest._
import org.slf4j.LoggerFactory

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
          codec encode (transformation apply store, orig)
          val copy: AnyRef = codec decode (transformation unapply store,
                                           orig.getClass)
          store match {
            case ms: MemoryStore =>
              Option(Codecs charset codec) match {
                case Some(charset) if IdentityTransformation == transformation =>
                  logger debug ("\n{}", new String(ms.data, charset))
                case _ =>
                  logger debug ("Created BLOB with {} bytes size.", ms.data.length)
              }
            case _ =>
          }
          copy should equal (orig)
          copy should not be theSameInstanceAs (orig)
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
