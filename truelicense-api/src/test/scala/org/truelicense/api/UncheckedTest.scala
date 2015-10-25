package org.truelicense.api

import org.junit.runner.RunWith
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock
import org.truelicense.api.io.{Source, Sink}

@RunWith(classOf[JUnitRunner])
class UncheckedTest extends WordSpec {

  "The unchecked vendor license manager returned by Unchecked.wrap(VendorLicenseManager)" should {
    val checkedManager = mock[VendorLicenseManager]

    val uncheckedManager = Unchecked wrap checkedManager

    "throw only unchecked exceptions" when {
      "generating license keys" when {
        val checkedGenerator = mock[LicenseKeyGenerator]
        when(checkedManager generator any[License]) thenReturn checkedGenerator

        val uncheckedGenerator = uncheckedManager generator mock[License]

        "calling license()" in {
          when(checkedGenerator.license) thenThrow checkedException
          interceptUncheckedException { uncheckedGenerator.license }
        }

        "calling writeTo(Sink)" in {
          when(checkedGenerator writeTo any[Sink]) thenThrow checkedException
          interceptUncheckedException { uncheckedGenerator writeTo mock[Sink] }
        }
      }
    }
  }

  "The unchecked consumer license manager returned by Unchecked.wrap(ConsumerLicenseManager)" should {
    val checkedManager = mock[ConsumerLicenseManager]

    val uncheckedManager = Unchecked wrap checkedManager

    "throw only unchecked exceptions" when {
      "installing a license key" in {
        when(checkedManager install any[Source]) thenThrow checkedException
        interceptUncheckedException { uncheckedManager install mock[Source] }
      }

      "viewing a license key" in {
        when(checkedManager view ()) thenThrow checkedException
        interceptUncheckedException { uncheckedManager view () }
      }

      "verifying a license key" in {
        when(checkedManager verify ()) thenThrow checkedException
        interceptUncheckedException { uncheckedManager verify () }
      }

      "uninstalling a license key" in {
        when(checkedManager uninstall ()) thenThrow checkedException
        interceptUncheckedException { uncheckedManager uninstall () }
      }
    }
  }

  private def checkedException = new LicenseManagementException

  private def interceptUncheckedException =
    intercept[UncheckedLicenseManagementException] _
}
