package org.truelicense.api

import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers._
import org.scalatest.mock.MockitoSugar._

@RunWith(classOf[JUnitRunner])
class UncheckedTest extends WordSpec {

  "The unchecked vendor license manager returned by Unchecked.wrap(VendorLicenseManager)" should {
    "throw only unchecked exceptions" in {
      val checked = mock[VendorLicenseManager]
      val unchecked = Unchecked wrap checked

    }
  }
}
