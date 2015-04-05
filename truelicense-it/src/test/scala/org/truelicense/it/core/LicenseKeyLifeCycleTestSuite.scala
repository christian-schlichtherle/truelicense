/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

import java.util.Date

import org.truelicense.core._
import org.truelicense.core.io._
import org.truelicense.it.core.LicenseKeyLifeCycleTestSuite.logger
import org.scalatest.Matchers._
import org.scalatest._
import org.slf4j.LoggerFactory

/** @author Christian Schlichtherle */
abstract class LicenseKeyLifeCycleTestSuite
extends WordSpec { this: TestContext =>

  def check(license: License, notBefore: Date = null, notAfter: Date = null) {
    import license._
    getConsumerAmount should be (1)
    getConsumerType should be ("User")
    getExtra should be (null)
    getHolder should not be null
    getInfo should be (null)
    getIssued should not be null
    getIssuer should not be null
    getNotAfter should be (notAfter)
    getNotBefore should be (notBefore)
    getSubject should be (managementContext.subject)
  }

  "The license key life cycle" should {
    "work for regular license keys" in {
      val vs = store

      val created = {
        val vm = vendorManager
        val created = vm create (managementContext.license, vs)
        check(created)
        created
      }

      vs match {
        case ms: MemoryStore =>
          logger debug ("Created license key with {} bytes size.", ms.data.length)
        case _ =>
      }

      val cs = store
      val cm = consumerManager(cs)
      cs exists () should equal (false)
      intercept[LicenseManagementException] { cm view () }
      intercept[LicenseManagementException] { cm verify () }
      intercept[LicenseManagementException] { cm uninstall () }
      val installed = cm install vs
      installed should equal (created)
      installed should not be theSameInstanceAs (created)
      ;{
        val reinstalled = cm install vs
        reinstalled should equal (installed)
        reinstalled should not be theSameInstanceAs (installed)
      }
      cm verify ()
      val viewed = cm view ()
      viewed should equal (created)
      viewed should not be theSameInstanceAs (created)
      viewed should equal (installed)
      viewed should not be theSameInstanceAs (installed)
      cm uninstall ()
      intercept[LicenseManagementException] { cm view () }
      intercept[LicenseManagementException] { cm verify () }
      intercept[LicenseManagementException] { cm uninstall () }
    }

    "work for FTP license keys" in {
      val cs = store
      val cm = consumerManager(cs)
      cs exists () should equal (false)
      val fcs = store
      val fcm = ftpConsumerManager(cm, fcs)
      fcs exists () should equal (false)
      fcm verify () // generate
      val generated = fcm view ()
      cs exists () should equal (false)
      fcs exists () should equal (true)
      check(generated,
            generated.getIssued,
            datePlusDays(generated.getIssued, 1))
      fcm verify ()
      val viewed = fcm view ()
      viewed should equal (generated)
      viewed should not be theSameInstanceAs (generated)
      val viewed2 = fcm view ()
      viewed2 should equal (generated)
      viewed2 should not be theSameInstanceAs (generated)
      cs exists () should equal (false)
    }

    "work for chained license keys" in {
      val vs = store

      val cs = store
      val cm = consumerManager(cs)

      val ccs = store
      val ccm = chainedConsumerManager(cm, ccs)

      cs exists () should equal (false)
      ccs exists () should equal (false)

      intercept[LicenseManagementException] { cm view () }
      intercept[LicenseManagementException] { ccm view () }

      ;{
        val created = {
          val vm = vendorManager
          val created = vm create (managementContext.license, vs)
          check(created)
          created
        }

        val installed = ccm install vs // delegates to cm!
        installed should equal (created)
        installed should not be theSameInstanceAs (created)
        cs exists () should equal (true)
        ccs exists () should equal (false)
        ccm verify ()
        val viewed = ccm view ()
        viewed should equal (created)
        viewed should not be theSameInstanceAs (created)
        ccm uninstall () // delegates to cm!
      }

      cs exists () should equal (false)
      ccs exists () should equal (false)

      ;{
        val created = {
          val vm = chainedVendorManager
          val created = vm create (managementContext.license, vs)
          check(created)
          created
        }

        val installed = ccm install vs // installs in ccm!
        installed should equal (created)
        installed should not be theSameInstanceAs (created)
        cs exists () should equal (false)
        ccs exists () should equal (true)
        ccm verify ()
        val viewed = ccm view ()
        viewed should equal (created)
        viewed should not be theSameInstanceAs (created)
        ccm uninstall () // uninstalls from ccm!
      }

      cs exists () should equal (false)
      ccs exists () should equal (false)
    }
  }
}

/** @author Christian Schlichtherle */
object LicenseKeyLifeCycleTestSuite {
  private val logger = LoggerFactory getLogger classOf[LicenseKeyLifeCycleTestSuite]
}
