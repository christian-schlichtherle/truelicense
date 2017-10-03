/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import java.util.Date

import net.truelicense.api.{ConsumerLicenseManager, License, LicenseManagementException}
import net.truelicense.it.core.LicenseKeyLifeCycleTestSuite.logger
import net.truelicense.spi.io.MemoryStore
import org.scalatest.Matchers._
import org.scalatest._
import org.slf4j.LoggerFactory

/** @author Christian Schlichtherle */
abstract class LicenseKeyLifeCycleTestSuite
  extends WordSpec
{ this: TestContext[_] =>

  "The license key life cycle" should {
    "work for regular license keys" in {
      val vs = store

      val generated = {
        val vm = vendorManager
        val generated = (vm generateKeyFrom managementContext.license saveTo vs).license
        assertLicense(generated)
        generated
      }

      vs match {
        case ms: MemoryStore =>
          logger debug ("Generated license key with {} bytes size.", ms.data.length)
        case _ =>
      }

      val cs = store
      val cm = consumerManager(cs)
      cs exists () shouldBe false
      assertUninstalled(cm)
      cm install vs
      cm install vs // reinstall
      cm verify ()
      val viewed = cm load ()
      viewed shouldBe generated
      viewed should not be theSameInstanceAs (generated)
      cm uninstall ()
      assertUninstalled(cm)
    }

    "work for FTP license keys" in {
      val cs = store
      val cm = consumerManager(cs)
      cs exists () shouldBe false
      val fcs = store
      val fcm = ftpConsumerManager(cm, fcs)
      fcs exists () shouldBe false
      fcm verify () // generate
      val generated = fcm load ()
      cs exists () shouldBe false
      fcs exists () shouldBe true
      assertLicense(generated,
            generated.getIssued,
            datePlusDays(generated.getIssued, 1))
      fcm verify ()
      val viewed = fcm load ()
      viewed shouldBe generated
      viewed should not be theSameInstanceAs (generated)
      val viewed2 = fcm load ()
      viewed2 shouldBe generated
      viewed2 should not be theSameInstanceAs (generated)
      cs exists () shouldBe false
    }

    "work for chained license keys" in {
      val vs = store

      val cs = store
      val cm = consumerManager(cs)

      val ccs = store
      val ccm = chainedConsumerManager(cm, ccs)

      cs exists () shouldBe false
      ccs exists () shouldBe false

      intercept[LicenseManagementException] { cm load () }
      intercept[LicenseManagementException] { ccm load () }

      ;{
        val generated = {
          val vm = vendorManager
          val generated = (vm generateKeyFrom managementContext.license saveTo vs).license
          assertLicense(generated)
          generated
        }

        ccm install vs // delegates to cm!
        cs exists () shouldBe true
        ccs exists () shouldBe false
        ccm verify ()
        val viewed = ccm load ()
        viewed shouldBe generated
        viewed should not be theSameInstanceAs (generated)
        ccm uninstall () // delegates to cm!
        assertUninstalled(ccm)
      }

      cs exists () shouldBe false
      ccs exists () shouldBe false

      ;{
        val generated = {
          val vm = chainedVendorManager
          val generated = (vm generateKeyFrom managementContext.license saveTo vs).license
          assertLicense(generated)
          generated
        }

        ccm install vs // installs in ccm!
        assertUninstalled(cm)
        cs exists () shouldBe false
        ccs exists () shouldBe true
        ccm verify ()
        val viewed = ccm load ()
        viewed shouldBe generated
        viewed should not be theSameInstanceAs (generated)
        ccm uninstall () // uninstalls from ccm!
        assertUninstalled(ccm)
      }

      cs exists () shouldBe false
      ccs exists () shouldBe false
    }
  }

  private def assertLicense(license: License, notBefore: Date = null, notAfter: Date = null) {
    import license._
    getConsumerAmount shouldBe 1
    getConsumerType shouldBe "User"
    getExtra shouldBe null
    getHolder should not be null
    getInfo shouldBe null
    getIssued should not be null
    getIssuer should not be null
    getNotAfter shouldBe notAfter
    getNotBefore shouldBe notBefore
    getSubject shouldBe managementContext.subject
  }

  private def assertUninstalled(cm: ConsumerLicenseManager) = {
    intercept[LicenseManagementException] { cm load () }
    intercept[LicenseManagementException] { cm verify () }
    intercept[LicenseManagementException] { cm uninstall () }
  }
}

/** @author Christian Schlichtherle */
object LicenseKeyLifeCycleTestSuite {
  private val logger = LoggerFactory getLogger classOf[LicenseKeyLifeCycleTestSuite]
}
