/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.core

import java.util.Date

import net.java.truelicense.core._
import net.java.truelicense.core.io._
import net.java.truelicense.it.core.LicenseKeyLifeCycleTestSuite.logger
import org.scalatest.Matchers._
import org.scalatest._
import org.slf4j.LoggerFactory

/** @author Christian Schlichtherle */
abstract class LicenseKeyLifeCycleTestSuite
extends WordSpec { this: TestContext =>

  "The license key life cycle" should {
    "work for regular license keys" in {
      val vs = store

      val generated = {
        val vm = vendorManager
        val generated = vm create (managementContext.license, vs)
        assertLicense(generated)
        generated
      }

      vs match {
        case ms: MemoryStore =>
          logger debug ("Created license key with {} bytes size.", ms.data.length)
        case _ =>
      }

      val cs = store
      val cm = consumerManager(cs)
      cs exists () shouldBe false
      assertUninstalled(cm)
      val installed = cm install vs
      installed shouldBe generated
      installed should not be theSameInstanceAs (generated)
      ;{
        val reinstalled = cm install vs
        reinstalled shouldBe installed
        reinstalled should not be theSameInstanceAs (installed)
      }
      cm verify ()
      val viewed = cm view ()
      viewed shouldBe generated
      viewed should not be theSameInstanceAs (generated)
      viewed shouldBe installed
      viewed should not be theSameInstanceAs (installed)
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
      val generated = fcm view ()
      cs exists () shouldBe false
      fcs exists () shouldBe true
      assertLicense(generated,
            generated.getIssued,
            datePlusDays(generated.getIssued, 1))
      fcm verify ()
      val viewed = fcm view ()
      viewed shouldBe generated
      viewed should not be theSameInstanceAs (generated)
      val viewed2 = fcm view ()
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

      intercept[LicenseManagementException] { cm view () }
      intercept[LicenseManagementException] { ccm view () }

      ;{
        val generated = {
          val vm = vendorManager
          val generated = vm create (managementContext.license, vs)
          assertLicense(generated)
          generated
        }

        val installed = ccm install vs // delegates to cm!
        installed shouldBe generated
        installed should not be theSameInstanceAs (generated)
        cs exists () shouldBe true
        ccs exists () shouldBe false
        ccm verify ()
        val viewed = ccm view ()
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
          val generated = vm create (managementContext.license, vs)
          assertLicense(generated)
          generated
        }

        val installed = ccm install vs // installs in ccm!
        assertUninstalled(cm)
        installed shouldBe generated
        installed should not be theSameInstanceAs (generated)
        cs exists () shouldBe false
        ccs exists () shouldBe true
        ccm verify ()
        val viewed = ccm view ()
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

  private def assertUninstalled(cm: LicenseConsumerManager) {
    intercept[LicenseManagementException] { cm view () }
    intercept[LicenseManagementException] { cm verify () }
    intercept[LicenseManagementException] { cm uninstall () }
  }
}

/** @author Christian Schlichtherle */
object LicenseKeyLifeCycleTestSuite {
  private val logger = LoggerFactory getLogger classOf[LicenseKeyLifeCycleTestSuite]
}
