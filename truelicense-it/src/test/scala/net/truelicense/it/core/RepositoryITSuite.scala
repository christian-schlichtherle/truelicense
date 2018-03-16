/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import net.truelicense.api.auth.RepositoryContext
import org.scalatest.Matchers._
import org.scalatest._

/** @author Christian Schlichtherle */
abstract class RepositoryITSuite[Model <: AnyRef] extends WordSpec with ParallelTestExecution { this: TestContext =>

  val context: RepositoryContext[Model]

  "A repository" should {
    "sign and verify an object" in {
      val authentication = vendorManager.parameters.authentication
      val controller = context.controller(context.model, codec)
      val artifact = license
      val clazz = license.getClass
      authentication.sign(controller, artifact) decode clazz shouldBe artifact
      authentication verify controller decode clazz shouldBe artifact
    }
  }
}
