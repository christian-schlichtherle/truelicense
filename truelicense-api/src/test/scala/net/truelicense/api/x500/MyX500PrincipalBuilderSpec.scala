/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.x500

import javax.security.auth.x500.X500Principal
import org.scalatest.Matchers._
import org.scalatest.WordSpec

/**
 * @author Christian Schlichtherle
 */
class MyX500PrincipalBuilderSpec extends WordSpec {

  private val expected = {
    import collection.JavaConverters._
    new X500Principal(
      "C=C,CN=CN,DC=DC,DNQ=DNQ,DNQUALIFIER=DNQUALIFIER,EMAILADDRESS=EMAILADDRESS,FOO=FOO,GENERATION=GENERATION,GIVENNAME=GIVENNAME,INITIALS=INITIALS,L=L,O=O,OU=OU,SERIALNUMBER=SERIALNUMBER,ST=ST,STREET=STREET,SURNAME=SURNAME,T=T,UID=UID",
      Map("FOO" -> "1.2.3.4.5.6.7.8.9").asJava)
  }

  "Using my X.500 principal builder class" should {
    "be compilable without adding type casts" in {
      new MyX500PrincipalBuilder()
        .addC("C")
        .addCN("CN")
        .addDC("DC")
        .addDNQ("DNQ")
        .addDNQUALIFIER("DNQUALIFIER")
        .addEMAILADDRESS("EMAILADDRESS")
        .addFOO("FOO")
        .addGENERATION("GENERATION")
        .addGIVENNAME("GIVENNAME")
        .addINITIALS("INITIALS")
        .addL("L")
        .addO("O")
        .addOU("OU")
        .addSERIALNUMBER("SERIALNUMBER")
        .addST("ST")
        .addSTREET("STREET")
        .addSURNAME("SURNAME")
        .addT("T")
        .addUID("UID")
        .build shouldBe expected
    }
  }
}
