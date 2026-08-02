/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.proguard

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

/**
 * The default input jar must stay out of `@Parameter(defaultValue = ...)`: Maven splits such a value into list
 * elements at every comma, and the filter contains one, so the default would reach ProGuard as a second `-injars`
 * argument and fail to parse.
 */
class ProGuardMojoSpec extends AnyWordSpec {

  private def injars(mojo: ProGuardMojo, finalName: String, packaging: String) = {
    for ((name, value) <- Seq("finalName" -> finalName, "packaging" -> packaging)) {
      val field = classOf[ProGuardMojo].getDeclaredField(name)
      field.setAccessible(true)
      field.set(mojo, value)
    }
    mojo.injars()
  }

  "A ProGuardMojo" should {
    "default to exactly one input jar, filter included" in {
      val i = injars(new ProGuardMojo, "app-1.0", "jar")
      i should have size 1
      i.get(0) shouldBe "app-1.0.jar(!module-info.class,!META-INF/maven/**)"
    }

    "keep an explicit configuration" in {
      val mojo = new ProGuardMojo
      val field = classOf[ProGuardMojo].getDeclaredField("injars")
      field.setAccessible(true)
      field.set(mojo, java.util.Collections.singletonList("custom.jar"))
      injars(mojo, "app-1.0", "jar").get(0) shouldBe "custom.jar"
    }

    "declare no list parameter with a comma in its default value" in {
      val in = getClass.getClassLoader.getResourceAsStream("META-INF/maven/plugin.xml")
      val descriptor =
        try scala.io.Source.fromInputStream(in, "UTF-8").mkString
        finally in.close()
      val mojo = "(?s)<mojo>(?:(?!</mojo>).)*<goal>proguard</goal>.*?</mojo>".r
        .findFirstIn(descriptor)
        .getOrElse(fail("The proguard goal is missing from the plugin descriptor."))
      val offenders = """<(\w+) implementation="java\.util\.List" default-value="([^"]*,[^"]*)"""".r
        .findAllMatchIn(mojo)
        .map(m => s"${m.group(1)}=${m.group(2)}")
        .toList
      // Maven splits such a default into one list element per comma.
      offenders shouldBe empty
    }
  }
}
