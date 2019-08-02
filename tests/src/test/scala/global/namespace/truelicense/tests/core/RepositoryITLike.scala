/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.core

import global.namespace.truelicense.api.License
import global.namespace.truelicense.api.auth.RepositoryFactory
import org.scalatest.Matchers._
import org.scalatest._

trait RepositoryITLike extends WordSpecLike {
  this: TestContext =>

  type Model >: Null <: AnyRef

  val factory: RepositoryFactory[Model]

  "A repository" should {
    "sign and verify an object" in {
      val authentication = vendorManager.schema.authentication
      val controller = factory.controller(codec, factory.model)
      val artifact = license
      val clazz = license.getClass
      (authentication.sign(controller, artifact) decode clazz: License) shouldBe artifact
      (authentication verify controller decode clazz: License) shouldBe artifact
    }
  }
}
