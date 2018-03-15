/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import java.util.Locale.ENGLISH

import net.truelicense.api.auth.RepositoryContext
import org.scalatest.Matchers._
import org.scalatest._

/** @author Christian Schlichtherle */
abstract class RepositoryTestSuite[Model <: AnyRef]
extends WordSpec with ParallelTestExecution { this: TestContext[Model] =>

  private val _codec = new SerializationCodec {
    override def contentType: String = super.contentType.toUpperCase(ENGLISH)
    override def contentTransferEncoding: String = super.contentTransferEncoding.toUpperCase(ENGLISH)
  }

  val context: RepositoryContext[Model]

  "A repository" should {
    "sign and verify an object" in {
      val authentication = vendorManager.parameters.authentication
      val controller = context.controller(context.model, _codec)
      val artifact = "Hello world!"
      (authentication.sign(controller, artifact) decode classOf[String]: String) shouldBe artifact
      (authentication verify controller decode classOf[String]: String) shouldBe artifact
    }
  }
}
