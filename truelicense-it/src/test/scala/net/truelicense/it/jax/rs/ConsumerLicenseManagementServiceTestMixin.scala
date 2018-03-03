package net.truelicense.it.jax.rs

import global.namespace.fun.io.bios.BIOS
import net.truelicense.api.License
import net.truelicense.it.core.TestContext
import net.truelicense.jax.rs.ConsumerLicenseManagementService

import scala.language.existentials

trait ConsumerLicenseManagementServiceTestMixin {
  this: TestContext[_] =>

  lazy val managementService: ConsumerLicenseManagementService = new ConsumerLicenseManagementService(consumerManager())

  protected lazy val (cachedLicenseClass, cachedLicenseBean, cachedLicenseKey): (Class[_ <: License], License, Array[Byte]) = {
    val store = BIOS.memoryStore
    val bean = license
    val generator = vendorManager generateKeyFrom bean saveTo store.output
    (bean.getClass, generator.license, store.content)
  }
}
