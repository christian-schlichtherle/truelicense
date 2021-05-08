/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.core

import global.namespace.truelicense.api.License
import global.namespace.truelicense.api.auth.RepositoryFactory
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpecLike

trait RepositoryITLike extends AnyWordSpecLike {
  this: TestContext =>

  type Model >: Null <: AnyRef

  private lazy val factory = managementContext.repositoryFactory.asInstanceOf[RepositoryFactory[Model]]

  "A repository" should {
    "sign and verify an object" in {
      val authentication = vendorManager.parameters.authentication
      val controller = factory.controller(managementContext.codec, factory.model)
      val artifact = licenseBean
      val clazz = artifact.getClass
      (authentication.sign(controller, artifact) decode clazz: License) shouldBe artifact
      (authentication verify controller decode clazz: License) shouldBe artifact
    }
  }
}
