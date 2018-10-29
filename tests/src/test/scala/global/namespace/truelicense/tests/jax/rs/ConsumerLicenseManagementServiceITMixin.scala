/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.jax.rs

import global.namespace.fun.io.bios.BIOS.memory
import global.namespace.truelicense.api.License
import global.namespace.truelicense.tests.core.TestContext
import global.namespace.truelicense.jax.rs.ConsumerLicenseManagementService

import scala.language.existentials

trait ConsumerLicenseManagementServiceITMixin {
  this: TestContext =>

  lazy val managementService: ConsumerLicenseManagementService = new ConsumerLicenseManagementService(consumerManager())

  protected lazy val (cachedLicenseClass, cachedLicenseBean, cachedLicenseKey): (Class[_ <: License], License, Array[Byte]) = {
    val store = memory
    val license = this.license
    val generator = vendorManager generateKeyFrom license saveTo store
    (license.getClass, generator.license, store.content)
  }
}
