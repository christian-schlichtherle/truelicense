/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.core

import java.util.Calendar.{DATE, getInstance}
import java.util.Date

import global.namespace.fun.io.bios.BIOS.memory
import global.namespace.truelicense.api.{ConsumerLicenseManager, License, LicenseManagementException}
import global.namespace.truelicense.tests.core.LicenseKeyLifeCycleITLike.logger
import org.scalatest.Matchers._
import org.scalatest._
import org.slf4j.LoggerFactory

trait LicenseKeyLifeCycleITLike extends WordSpecLike {
  this: TestContext =>

  "The license key life cycle" should {
    "cover regular license keys" in new State {
      {
        val tempStore = memory

        val generated = {
          val generated = (vendorManager generateKeyFrom licenseBean saveTo tempStore).license
          assertLicenseBean(generated)
          generated
        }

        logger debug("Generated license key with {} bytes size.", tempStore.size().getAsLong)

        consumerStore.exists shouldBe false
        assertUninstalled(consumerManager)
        consumerManager install tempStore
        consumerManager install tempStore // reinstall
        consumerManager.verify()

        val viewed = consumerManager.load()
        viewed shouldBe generated
        viewed should not be theSameInstanceAs(generated)
        consumerManager.uninstall()
        assertUninstalled(consumerManager)
      }
    }

    "cover FTP license keys" in new State {
      {
        consumerStore.exists shouldBe false
        ftpStore.exists shouldBe false
        ftpManager.verify() // generate

        val generated = ftpManager.load()
        consumerStore.exists shouldBe false
        ftpStore.exists shouldBe true
        assertFtpLicenseBean(generated)
        ftpManager.verify()

        val viewed = ftpManager.load()
        viewed shouldBe generated
        viewed should not be theSameInstanceAs(generated)
        intercept[LicenseManagementException](ftpManager.uninstall())
        ftpStore.exists shouldBe true

        val viewed2 = ftpManager.load()
        viewed2 shouldBe generated
        viewed2 should not be theSameInstanceAs(generated)
        consumerStore.exists shouldBe false
      }
    }

    "cover chained license keys" in new State {
      {
        val tempStore = memory

        consumerStore.exists shouldBe false
        chainedConsumerStore.exists shouldBe false

        intercept[LicenseManagementException](consumerManager.load())
        intercept[LicenseManagementException](chainedConsumerManager.load())

        {
          val generated = {
            val generated = (vendorManager generateKeyFrom licenseBean saveTo tempStore).license
            assertLicenseBean(generated)
            generated
          }

          chainedConsumerManager install tempStore // delegates to cm!
          consumerStore.exists shouldBe true
          chainedConsumerStore.exists shouldBe false
          chainedConsumerManager.verify()

          val viewed = chainedConsumerManager.load()
          viewed shouldBe generated
          viewed should not be theSameInstanceAs(generated)
          chainedConsumerManager.uninstall() // delegates to cm!
          assertUninstalled(chainedConsumerManager)
        }

        consumerStore.exists shouldBe false
        chainedConsumerStore.exists shouldBe false

        {
          val generated = {
            val generated = (chainedVendorManager generateKeyFrom licenseBean saveTo tempStore).license
            assertLicenseBean(generated)
            generated
          }

          chainedConsumerManager install tempStore // saves to ccs!
          assertUninstalled(consumerManager)
          consumerStore.exists shouldBe false
          chainedConsumerStore.exists shouldBe true
          chainedConsumerManager.verify()

          val viewed = chainedConsumerManager.load()
          viewed shouldBe generated
          viewed should not be theSameInstanceAs(generated)
          chainedConsumerManager.uninstall() // deletes from ccs!
          assertUninstalled(chainedConsumerManager)
        }

        consumerStore.exists shouldBe false
        chainedConsumerStore.exists shouldBe false
      }
    }
  }

  private def assertFtpLicenseBean(license: License): Unit = {
    import license._
    getConsumerAmount shouldBe 1
    getConsumerType shouldBe "User"
    getExtra shouldBe null
    getHolder should not be null
    getInfo shouldBe null
    getIssued should not be null
    getIssuer should not be null
    getNotAfter shouldBe datePlusDays(getIssued, 1)
    getNotBefore shouldBe getIssued
    getSubject shouldBe managementContext.subject
  }

  private def datePlusDays(date: Date, days: Int): Date = {
    val cal = getInstance
    import cal._
    setTime(date)
    assert(isLenient)
    add(DATE, days)
    getTime
  }

  private def assertUninstalled(cm: ConsumerLicenseManager): Unit = {
    intercept[LicenseManagementException](cm.load())
    intercept[LicenseManagementException](cm.verify())
    intercept[LicenseManagementException](cm.uninstall())
  }
}

object LicenseKeyLifeCycleITLike {

  private val logger = LoggerFactory getLogger classOf[LicenseKeyLifeCycleITLike]
}
