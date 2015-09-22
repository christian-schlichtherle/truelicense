/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

import java.util.Date

import org.scalatest.Matchers._
import org.scalatest._
import org.slf4j.LoggerFactory
import org.truelicense.api._
import org.truelicense.it.core.LicenseKeyLifeCycleTestSuite.logger
import org.truelicense.spi.io.MemoryStore

/** @author Christian Schlichtherle */
abstract class LicenseKeyLifeCycleTestSuite
  extends WordSpec
{ this: TestContext[_] =>

  def check(license: License, notBefore: Date = null, notAfter: Date = null) {
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

  "The license key life cycle" should {
    "work for regular license keys" in {
      val vs = store

      val generated = {
        val vm = vendorManager
        val generated = (vm generator managementContext.license writeTo vs).license
        check(generated)
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
      intercept[LicenseManagementException] { cm view () }
      intercept[LicenseManagementException] { cm verify () }
      intercept[LicenseManagementException] { cm uninstall () }
      cm install vs
      cm install vs // reinstall
      cm verify ()
      val viewed = cm view ()
      viewed shouldBe generated
      viewed should not be theSameInstanceAs (generated)
      cm uninstall ()
      intercept[LicenseManagementException] { cm view () }
      intercept[LicenseManagementException] { cm verify () }
      intercept[LicenseManagementException] { cm uninstall () }
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
      check(generated,
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
          val generated = (vm generator managementContext.license writeTo vs).license
          check(generated)
          generated
        }

        ccm install vs // delegates to cm!
        cs exists () shouldBe true
        ccs exists () shouldBe false
        ccm verify ()
        val viewed = ccm view ()
        viewed shouldBe generated
        viewed should not be theSameInstanceAs (generated)
        ccm uninstall () // delegates to cm!
        intercept[LicenseManagementException] { ccm view () }
        intercept[LicenseManagementException] { ccm verify () }
        intercept[LicenseManagementException] { ccm uninstall () }
      }

      cs exists () shouldBe false
      ccs exists () shouldBe false

      ;{
        val generated = {
          val vm = chainedVendorManager
          val generated = (vm generator managementContext.license writeTo vs).license
          check(generated)
          generated
        }

        ccm install vs // installs in ccm!
        intercept[LicenseManagementException] { cm view () }
        intercept[LicenseManagementException] { cm verify () }
        intercept[LicenseManagementException] { cm uninstall () }
        cs exists () shouldBe false
        ccs exists () shouldBe true
        ccm verify ()
        val viewed = ccm view ()
        viewed shouldBe generated
        viewed should not be theSameInstanceAs (generated)
        ccm uninstall () // uninstalls from ccm!
        intercept[LicenseManagementException] { ccm view () }
        intercept[LicenseManagementException] { ccm verify () }
        intercept[LicenseManagementException] { ccm uninstall () }
      }

      cs exists () shouldBe false
      ccs exists () shouldBe false
    }
  }
}

/** @author Christian Schlichtherle */
object LicenseKeyLifeCycleTestSuite {
  private val logger = LoggerFactory getLogger classOf[LicenseKeyLifeCycleTestSuite]
}
