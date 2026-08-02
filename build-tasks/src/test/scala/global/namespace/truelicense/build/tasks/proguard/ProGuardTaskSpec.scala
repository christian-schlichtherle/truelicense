/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.proguard

import global.namespace.truelicense.build.tasks.commons.Logger
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.util.jar.JarFile
import java.util
import scala.jdk.CollectionConverters._

/**
 * ProGuard is started by class path and main class, because only the abandoned `net.sf.proguard:proguard-base` is a
 * shaded, runnable JAR. `com.guardsquare:proguard-base` has no `Main-Class` and expects its dependencies on the class
 * path, so `java -jar` fails on it before ProGuard ever starts.
 */
class ProGuardTaskSpec extends AnyWordSpec {

  private val fixtureResource = "global/namespace/truelicense/build/tasks/proguard/ProGuardFixture.class"

  /** The ProGuard distribution on this suite's own class path, as a test-scoped dependency. */
  private def proGuardClassPath: util.List[Path] = {
    val entries = System
      .getProperty("java.class.path")
      .split(File.pathSeparatorChar)
      .filter(e => e.contains("proguard") || e.contains("kotlin") || e.contains("gson") || e.contains("log4j") ||
        e.contains("json") || e.contains("annotations"))
      .map(Paths.get(_))
      .toList
    // Absence means the test dependency is gone, not that there is nothing to check.
    entries.exists(_.getFileName.toString.startsWith("proguard-base")) shouldBe true
    entries.asJava
  }

  private def readAllBytes(in: java.io.InputStream): Array[Byte] = {
    val out = new java.io.ByteArrayOutputStream
    val buf = new Array[Byte](8192)
    try {
      var n = in.read(buf)
      while (n > 0) { out.write(buf, 0, n); n = in.read(buf) }
    } finally in.close()
    out.toByteArray
  }

  private def task(dir: Path, classPath: util.List[Path], opts: List[String]): ProGuardTask =
    new ProGuardTask {
      override def logger(): Logger = new Logger {
        override def isDebugEnabled: Boolean = false
        override def isInfoEnabled: Boolean = false
        override def isWarnEnabled: Boolean = false
        override def isErrorEnabled: Boolean = false
        override def debug(error: Throwable): Unit = ()
        override def debug(message: CharSequence): Unit = ()
        override def debug(message: CharSequence, error: Throwable): Unit = ()
        override def info(error: Throwable): Unit = ()
        override def info(message: CharSequence): Unit = ()
        override def info(message: CharSequence, error: Throwable): Unit = ()
        override def warn(error: Throwable): Unit = ()
        override def warn(message: CharSequence): Unit = ()
        override def warn(message: CharSequence, error: Throwable): Unit = ()
        override def error(error: Throwable): Unit = ()
        override def error(message: CharSequence): Unit = ()
        override def error(message: CharSequence, error: Throwable): Unit = ()
      }
      override def buildDirectory(): Path = dir
      override def dependencies(): util.Set[Path] = new util.HashSet[Path]
      override def dependencyFilter(): String = null
      override def includeDependency(): Boolean = false
      override def includeDependencyInjar(): Boolean = false
      override def injars(): util.List[String] = util.Arrays.asList("in")
      override def libraryjars(): util.List[String] = new util.LinkedList[String]
      override def options(): util.List[String] = opts.asJava
      override def outjars(): util.List[String] = util.Arrays.asList("out.jar")
      override def proGuardClassPath(): util.List[Path] = classPath
    }

  "A ProGuardTask" should {
    "start ProGuard by class path and main class, never by -jar" in {
      val cp = util.Arrays.asList(Paths.get("/tmp/a.jar"), Paths.get("/tmp/b.jar"))
      val c = task(Paths.get("/tmp"), cp, Nil).commandLine().asScala.toList
      c.head should endWith("/bin/java")
      c should not contain "-jar"
      c(1) shouldBe "-cp"
      c(2) shouldBe s"/tmp/a.jar${File.pathSeparator}/tmp/b.jar"
      c(3) shouldBe "proguard.ProGuard"
      c should contain("-injars")
    }

    "obfuscate a class with the ProGuard on its class path" in {
      val dir = Files.createTempDirectory("proguard-task-spec")
      try {
        val in = dir.resolve("in").resolve(fixtureResource)
        Files.createDirectories(in.getParent)
        val source = getClass.getClassLoader.getResourceAsStream(fixtureResource)
        try Files.copy(source, in)
        finally source.close()

        task(dir, proGuardClassPath, List(
          "-dontoptimize", "-dontwarn", "-dontnote",
          "-keep", "public class " + classOf[ProGuardFixture].getName +
            " { public static void main(java.lang.String[]); }"
        )).execute()

        val out = dir.resolve("out.jar")
        Files.isRegularFile(out) shouldBe true
        val jar = new JarFile(out.toFile)
        try {
          val entry = jar.getEntry(fixtureResource)
          entry should not be null
          // `greeting` is not kept, so it must have been renamed.
          val bytes = new String(readAllBytes(jar.getInputStream(entry)), "ISO-8859-1")
          bytes should not include "greeting"
          bytes should include("main")
        } finally jar.close()
      } finally {
        Files.walk(dir).sorted(util.Comparator.reverseOrder[Path]).forEach(p => Files.delete(p))
      }
    }
  }
}
