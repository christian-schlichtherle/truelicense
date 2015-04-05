/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.x500

import javax.security.auth.x500.X500Principal

import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner

/**
 * @author Christian Schlichtherle
 */
@RunWith(classOf[JUnitRunner])
class MyX500PrincipalBuilderTest extends WordSpec {

  private val expected = {
    import collection.JavaConverters._
    new X500Principal(
      "CN=CN,L=L,ST=ST,O=O,OU=OU,C=C,STREET=STREET,DC=DC,UID=UID,T=T,DNQ=DNQ,DNQUALIFIER=DNQUALIFIER,SURNAME=SURNAME,GIVENNAME=GIVENNAME,INITIALS=INITIALS,GENERATION=GENERATION,EMAILADDRESS=EMAILADDRESS,SERIALNUMBER=SERIALNUMBER,FOO=FOO",
      Map("FOO" -> "1.2.3.4.5.6.7.8.9").asJava)
  }

  "Using my X.500 principal builder class" should {
    "be compilable without adding type casts" in {
      new MyX500PrincipalBuilder()
        .addCN("CN")
        .addL("L")
        .addST("ST")
        .addO("O")
        .addOU("OU")
        .addC("C")
        .addSTREET("STREET")
        .addDC("DC")
        .addUID("UID")
        .addT("T")
        .addDNQ("DNQ")
        .addDNQUALIFIER("DNQUALIFIER")
        .addSURNAME("SURNAME")
        .addGIVENNAME("GIVENNAME")
        .addINITIALS("INITIALS")
        .addGENERATION("GENERATION")
        .addEMAILADDRESS("EMAILADDRESS")
        .addSERIALNUMBER("SERIALNUMBER")
        .addFOO("FOO")
        .build should equal (expected)
    }
  }
}
