/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api.x500

import javax.security.auth.x500.X500Principal
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.TableDrivenPropertyChecks._

import scala.jdk.CollectionConverters._

class X500PrincipalBuilderSpec extends WordSpec {

  private val none = Map[String, String]()

  "An X.500 principal builder" when {
    "given a valid list of attributes and keywords" should {
      "properly build an X500 Principal" in {
        val table = Table(
          ("name", "attributes", "keywords"),
          ("", List(), none),
          ("CN=Duke", List("CN" -> "Duke"), none),
          ("CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US", List("CN" -> "Duke", "OU" -> "JavaSoft", "O" -> "Sun Microsystems", "C" -> "US"), none),
          ("CN=Duke Nukem", List("CN" -> "Duke", "CN" -> "Duke Nukem"), none),
          ("CN=\" Duke\"", List("CN" -> " Duke"), none),
          ("CN=\"#Duke\"", List("CN" -> "#Duke"), none),
          ("CN=\"Duke \"", List("CN" -> "Duke "), none),
          ("CN=\"Foo,Bar\"", List("CN" -> "Foo,Bar"), none),
          ("CN=\"Foo+Bar\"", List("CN" -> "Foo+Bar"), none),
          ("CN=\"Foo\\\"Bar\"", List("CN" -> "Foo\"Bar"), none),
          ("CN=\"Foo\\\\Bar\"", List("CN" -> "Foo\\Bar"), none),
          ("CN=\"Foo<Bar\"", List("CN" -> "Foo<Bar"), none),
          ("CN=\"Foo>Bar\"", List("CN" -> "Foo>Bar"), none),
          ("CN=\"Foo;Bar\"", List("CN" -> "Foo;Bar"), none),
          ("CN=Foo=Bar", List("CN" -> "Foo=Bar"), none),
          ("FOO=Bar", List("FOO" -> "Bar"), Map("FOO" -> "1.2.3.4.5.6.7.8.9")),
          ("1.2.3.4.5.6.7.8.9=Bar", List("FOO" -> "Bar"), Map("FOO" -> "1.2.3.4.5.6.7.8.9")),
          ("OU=JavaSoft", List("CN" -> "Duke", "CN" -> null, "OU" -> "JavaSoft"), none)
        )
        forAll(table) { (name, attributes, keywords) =>
          val expected = new X500Principal(name, keywords.asJava)
          builder(attributes, keywords).build shouldBe expected
        }
      }
    }

    "given an invalid list of attributes or keywords" should {
      "throw an IllegalArgumentException" in {
        val table = Table(
          ("attributes", "keywords"),
          (List("FOO" -> "Bar"), none),
          (List("FOO" -> "Bar"), Map("FOO" -> "Invalid OID"))
        )
        forAll(table) { (attributes, keywords) =>
          intercept[IllegalArgumentException] {
            builder(attributes, keywords).build
          }
        }
      }
    }
  }

  private def builder(attributes: List[(String, String)], keywords: Map[String, String]) = {
    val b = new X500PrincipalBuilder
    attributes foreach { case (t, v) => b addAttribute(t, v) }
    keywords foreach { case (n, o) => b addKeyword(n, o) }
    b
  }
}
