/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.obfuscation

import global.namespace.truelicense.build.tasks.commons.Logger
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.ClassWriter
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.{Files, Path}
import java.util

/**
 * The rewriter's ASM API level has to keep up with the class files it reads. A record is the cheapest thing that
 * proves it: an ASM `ClassVisitor` below API level 8 throws `UnsupportedOperationException: Records requires ASM8`
 * on the record component, so a project targeting Java 16 or later could not be obfuscated at all.
 *
 * The input is assembled with ASM rather than compiled, so this runs on every JDK in the matrix, including 8.
 */
class ObfuscateClassesTaskSpec extends AnyWordSpec {

  private def writeRecord(dir: Path): Path = {
    val cw = new ClassWriter(0)
    cw.visit(V16, ACC_PUBLIC | ACC_FINAL | ACC_SUPER | ACC_RECORD, "com/example/Rec", null, "java/lang/Record", null)
    cw.visitRecordComponent("name", "Ljava/lang/String;", null).visitEnd()
    cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, "TAG", "Ljava/lang/String;", null, "PKCS12").visitEnd()
    cw.visitEnd()
    val file = dir.resolve("com/example/Rec.class")
    Files.createDirectories(file.getParent)
    Files.write(file, cw.toByteArray)
    file
  }

  private def task(dir: Path): ObfuscateClassesTask =
    new ObfuscateClassesTask {
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
      override def intern(): Boolean = false
      override def maxBytes(): Int = ObfuscateClassesTask.MAX_BYTES
      override def methodNameFormat(): String = ObfuscateClassesTask.METHOD_NAME_FORMAT
      override def outputDirectory(): Path = dir
      override def scope(): Scope = Scope.all
    }

  "An ObfuscateClassesTask" should {
    "obfuscate a class file that declares a record" in {
      val dir = Files.createTempDirectory("obfuscate-task-spec")
      try {
        val file = writeRecord(dir)
        new String(Files.readAllBytes(file), "ISO-8859-1") should include("PKCS12")

        task(dir).execute()

        val rewritten = new String(Files.readAllBytes(file), "ISO-8859-1")
        rewritten should not include "PKCS12"
        rewritten should include("truelicense/obfuscate/ObfuscatedString")
        // The record component must survive the rewrite.
        rewritten should include("name")
      } finally {
        Files.walk(dir).sorted(util.Comparator.reverseOrder[Path]).forEach(p => Files.delete(p))
      }
    }
  }
}
