/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.maven.plugin

import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.PropertyChecks._

/**
 * @author Christian Schlichtherle
 */
@RunWith(classOf[JUnitRunner])
class VersionToolTest extends WordSpec {

  "A VersionTool should correctly parse the given version string" in {
    val table = Table(
      ("string", "major", "minor", "incremental"),
      ("2.3.4", 2, 3, 4),
      ("2.4-SNAPSHOT", 2, 4, 0),
      ("2.4", 2, 4, 0)
    )
    forAll(table) { (string, major, minor, incremental) =>
      val version = new VersionTool().parse(string)
      version.getMajorVersion should be (major)
      version.getMinorVersion should be (minor)
      version.getIncrementalVersion should be (incremental)
    }
  }
}
