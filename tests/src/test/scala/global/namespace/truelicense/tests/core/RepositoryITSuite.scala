/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.core

import global.namespace.truelicense.api.License
import global.namespace.truelicense.api.auth.RepositoryContext
import org.scalatest.Matchers._
import org.scalatest._

/** @author Christian Schlichtherle */
abstract class RepositoryITSuite[Model <: AnyRef] extends WordSpec with ParallelTestExecution {
  this: TestContext =>

  val context: RepositoryContext[Model]

  "A repository" should {
    "sign and verify an object" in {
      val authentication = vendorManager.schema.authentication
      val controller = context `with` codec controller context.model
      val artifact = license
      val clazz = license.getClass
      (authentication.sign(controller, artifact) decode clazz: License) shouldBe artifact
      (authentication verify controller decode clazz: License) shouldBe artifact
    }
  }
}
