/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.core

import java.util.Date

import global.namespace.fun.io.api.Store
import global.namespace.fun.io.bios.BIOS.memory
import global.namespace.truelicense.api._
import global.namespace.truelicense.api.passwd.PasswordProtection
import global.namespace.truelicense.core.passwd.ObfuscatedPasswordProtection
import global.namespace.truelicense.obfuscate.ObfuscatedString
import global.namespace.truelicense.tests.core.TestContext._
import javax.security.auth.x500.X500Principal
import org.scalatest.Matchers._
import org.slf4j.LoggerFactory

trait TestContext {
  self =>

  def managementContextBuilder: LicenseManagementContextBuilder

  protected def extra: AnyRef

  final def consumerManager(store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .encryption
      .protection(test1234)
      .up
      .authentication
      .alias("mykey")
      .loadFromResource(consumerKeystoreName)
      .storeProtection(test1234)
      .up
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  protected def consumerKeystoreName: String

  final def chainedConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .authentication
      .alias("mykey")
      .loadFromResource(chainedConsumerKeystoreName)
      .storeProtection(test1234)
      .up
      .parent(parent)
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  protected def chainedConsumerKeystoreName: String

  final def ftpManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager = {
    val cm = managementContext.consumer
      .ftpDays(1)
      .authentication
      .alias("mykey")
      .loadFromResource(ftpConsumerKeystoreName)
      .storeProtection(test1234)
      .up
      .parent(parent)
      .storeIn(store)
      .build
    require(cm.context eq managementContext)
    cm
  }

  protected def ftpConsumerKeystoreName: String

  final lazy val vendorManager: VendorLicenseManager = {
    val vm = managementContext.vendor
      .encryption
      .protection(test1234)
      .up
      .authentication
      .alias("mykey")
      .loadFromResource(vendorKeystoreName)
      .storeProtection(test1234)
      .up
      .build
    require(vm.context eq managementContext)
    vm
  }

  protected def vendorKeystoreName: String

  final lazy val chainedVendorManager: VendorLicenseManager = {
    val vm = managementContext.vendor
      .encryption
      .protection(test1234)
      .up
      .authentication
      .alias("mykey")
      .loadFromResource(chainedVendorKeystoreName)
      .storeProtection(test1234)
      .up
      .build
    require(vm.context eq managementContext)
    vm
  }

  protected def chainedVendorKeystoreName: String

  class State {

    final lazy val consumerManager: ConsumerLicenseManager = self.consumerManager(consumerStore)

    final lazy val consumerStore: Store = memory

    final lazy val chainedConsumerManager: ConsumerLicenseManager = {
      self.chainedConsumerManager(consumerManager, chainedConsumerStore)
    }

    final lazy val chainedConsumerStore: Store = memory

    final val ftpManager: ConsumerLicenseManager = self.ftpManager(consumerManager, ftpStore)

    final lazy val ftpStore = memory

    private lazy val vendorStore = {
      val s = memory
      vendorManager generateKeyFrom licenseBean saveTo s
      s
    }

    final def licenseKey: Array[Byte] = vendorStore.content
  }

  final def assertLicenseBean(license: License): Unit = {
    import license._
    getConsumerAmount shouldBe 1
    getConsumerType shouldBe "User"
    getExtra shouldBe extra
    getHolder shouldBe me
    getInfo shouldBe info
    getIssued shouldBe timestamp
    getIssuer shouldBe me
    getNotAfter shouldBe null
    getNotBefore shouldBe timestamp
    getSubject shouldBe managementContext.subject
  }

  final def licenseBean: License = {
    val l = managementContext.licenseFactory.license
    import l._
    setConsumerAmount(1)
    setConsumerType("User")
    setSubject(managementContext.subject)
    setHolder(me)
    setIssuer(me)
    setIssued(timestamp)
    setNotBefore(timestamp)
    setInfo(info)
    setExtra(extra)
    l
  }

  final lazy val managementContext: LicenseManagementContext = {
    managementContextBuilder
      .subject("subject")
      .validation(logger.debug("Validating license bean: {}", _))
      .build
  }
}

private object TestContext {

  private val info = "Hello, world!"

  private val logger = LoggerFactory getLogger classOf[TestContext]

  private val me = new X500Principal("CN=Christian Schlichtherle")

  private val test1234: PasswordProtection =
    new ObfuscatedPasswordProtection(new ObfuscatedString(Array[Long](0x545a955d0e30826cL, 0x3453ccaa499e6baeL))) /* => "test1234" */

  private val timestamp = new Date
}
