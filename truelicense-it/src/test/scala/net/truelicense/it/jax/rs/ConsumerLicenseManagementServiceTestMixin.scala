package net.truelicense.it.jax.rs

import global.namespace.fun.io.bios.BIOS.memoryStore
import net.truelicense.api.License
import net.truelicense.it.core.TestContext
import net.truelicense.jax.rs.ConsumerLicenseManagementService

import scala.language.existentials

trait ConsumerLicenseManagementServiceTestMixin { this: TestContext =>

  lazy val managementService: ConsumerLicenseManagementService = new ConsumerLicenseManagementService(consumerManager())

  protected lazy val (cachedLicenseClass, cachedLicenseBean, cachedLicenseKey): (Class[_ <: License], License, Array[Byte]) = {
    val store = memoryStore
    val bean = license
    val generator = vendorManager generateKeyFrom bean saveTo store
    (bean.getClass, generator.license, store.content)
  }
}
