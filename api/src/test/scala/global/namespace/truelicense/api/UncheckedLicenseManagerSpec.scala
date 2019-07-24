/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api

import global.namespace.fun.io.api.Store
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatestplus.mockito.MockitoSugar.mock

class UncheckedLicenseManagerSpec extends WordSpec {

  "The unchecked vendor license manager returned by UncheckedLicenseManager.from(VendorLicenseManager)" should {
    val checkedManager = mock[VendorLicenseManager]

    when(checkedManager.unchecked).thenCallRealMethod

    val uncheckedManager = checkedManager.unchecked

    "return the checked vendor license manager" in {
      uncheckedManager.checked shouldBe theSameInstanceAs(checkedManager)
    }

    "throw only unchecked exceptions" when {
      "generating license keys" when {
        val checkedGenerator = mock[LicenseKeyGenerator]
        when(checkedManager generateKeyFrom any[License]) thenReturn checkedGenerator

        val uncheckedGenerator = uncheckedManager generateKeyFrom mock[License]

        "calling license()" in {
          when(checkedGenerator.license) thenThrow checkedException
          interceptUncheckedException {
            uncheckedGenerator.license
          }
        }

        "calling saveTo(Sink)" in {
          when(checkedGenerator saveTo any[Store]) thenThrow checkedException
          interceptUncheckedException {
            uncheckedGenerator saveTo mock[Store]
          }
        }
      }
    }
  }

  "The unchecked consumer license manager returned by UncheckedLicenseManager.from(ConsumerLicenseManager)" should {
    val checkedManager = mock[ConsumerLicenseManager]

    when(checkedManager.unchecked).thenCallRealMethod

    val uncheckedManager = checkedManager.unchecked

    "return the checked consumer license manager" in {
      uncheckedManager.checked shouldBe theSameInstanceAs(checkedManager)
    }

    "throw only unchecked exceptions" when {
      "installing a license key" in {
        when(checkedManager install any[Store]) thenThrow checkedException
        interceptUncheckedException {
          uncheckedManager install mock[Store]
        }
      }

      "loading a license key" in {
        when(checkedManager.load()) thenThrow checkedException
        interceptUncheckedException {
          uncheckedManager.load()
        }
      }

      "verifying a license key" in {
        when(checkedManager.verify()) thenThrow checkedException
        interceptUncheckedException {
          uncheckedManager.verify()
        }
      }

      "uninstalling a license key" in {
        when(checkedManager.uninstall()) thenThrow checkedException
        interceptUncheckedException {
          uncheckedManager.uninstall()
        }
      }
    }
  }

  private def checkedException = new LicenseManagementException

  private def interceptUncheckedException =
    intercept[UncheckedLicenseManagementException] _
}
