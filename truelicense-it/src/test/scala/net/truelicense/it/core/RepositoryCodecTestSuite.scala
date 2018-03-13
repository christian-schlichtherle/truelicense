/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

/** @author Christian Schlichtherle */
abstract class RepositoryCodecTestSuite[Model <: AnyRef] extends CodecTestSuite { self: TestContext[Model] =>

  final override def artifact: AnyRef = {
    val license = self.license
    val codec = managementContext.codec
    val parameters = vendorManager.parameters
    val authentication = parameters.authentication
    val context = repositoryContext
    val model = context.model
    val controller = context.controller(model, codec)
    authentication.sign(controller, license)
    model
  }
}
