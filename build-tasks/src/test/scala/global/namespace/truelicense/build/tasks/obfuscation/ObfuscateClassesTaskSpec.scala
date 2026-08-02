/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.obfuscation

import global.namespace.truelicense.build.tasks.commons.Logger
import global.namespace.truelicense.obfuscate.Obfuscate
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.{ClassWriter, Type}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.{Files, Path}
import java.util
import scala.collection.mutable

class ObfuscateClassesTaskSpec extends AnyWordSpec {

  /**
   * Records what a task logs. Only the error level is enabled, because only it decides whether the task fails, and
   * a logger reporting a level as disabled suppresses the corresponding calls before they are ever counted.
   */
  private class RecordingLogger extends Logger {

    val errors: mutable.Buffer[String] = mutable.Buffer.empty

    override def isDebugEnabled: Boolean = false
    override def isInfoEnabled: Boolean = false
    override def isWarnEnabled: Boolean = false
    override def isErrorEnabled: Boolean = true
    override def debug(error: Throwable): Unit = ()
    override def debug(message: CharSequence): Unit = ()
    override def debug(message: CharSequence, error: Throwable): Unit = ()
    override def info(error: Throwable): Unit = ()
    override def info(message: CharSequence): Unit = ()
    override def info(message: CharSequence, error: Throwable): Unit = ()
    override def warn(error: Throwable): Unit = ()
    override def warn(message: CharSequence): Unit = ()
    override def warn(message: CharSequence, error: Throwable): Unit = ()
    override def error(error: Throwable): Unit = errors += String.valueOf(error)
    override def error(message: CharSequence): Unit = errors += message.toString
    override def error(message: CharSequence, error: Throwable): Unit = errors += message.toString
  }

  private def withTempDir[A](f: Path => A): A = {
    val dir = Files.createTempDirectory("obfuscate-task-spec")
    try f(dir)
    finally Files.walk(dir).sorted(util.Comparator.reverseOrder[Path]).forEach(p => Files.delete(p))
  }

  private def writeRecord(dir: Path): Path = {
    val cw = new ClassWriter(0)
    cw.visit(V16, ACC_PUBLIC | ACC_FINAL | ACC_SUPER | ACC_RECORD, "com/example/Rec", null, "java/lang/Record", null)
    cw.visitRecordComponent("name", "Ljava/lang/String;", null).visitEnd()
    cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, "TAG", "Ljava/lang/String;", null, "PKCS12").visitEnd()
    cw.visitEnd()
    write(dir, "com/example/Rec.class", cw)
  }

  /**
   * Writes a class with an annotated field which cannot be obfuscated: a non-static field carries no
   * {@code ConstantValue} attribute, so there is no constant string value to rewrite.
   */
  private def writeAnnotatedNonConstantField(dir: Path): Path = {
    val cw = new ClassWriter(0)
    cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/example/Bad", null, "java/lang/Object", null)
    val fv = cw.visitField(ACC_PUBLIC, "notConstant", "Ljava/lang/String;", null, null)
    // @Obfuscate has the default retention, hence it is invisible at runtime.
    fv.visitAnnotation(Type.getDescriptor(classOf[Obfuscate]), false).visitEnd()
    fv.visitEnd()
    cw.visitEnd()
    write(dir, "com/example/Bad.class", cw)
  }

  private def write(dir: Path, path: String, cw: ClassWriter): Path = {
    val file = dir.resolve(path)
    Files.createDirectories(file.getParent)
    Files.write(file, cw.toByteArray)
    file
  }

  private def task(dir: Path, log: Logger, obfuscationScope: Scope): ObfuscateClassesTask =
    new ObfuscateClassesTask {
      override def logger(): Logger = log
      override def intern(): Boolean = false
      override def maxBytes(): Int = ObfuscateClassesTask.MAX_BYTES
      override def methodNameFormat(): String = ObfuscateClassesTask.METHOD_NAME_FORMAT
      override def outputDirectory(): Path = dir
      override def scope(): Scope = obfuscationScope
    }

  "An ObfuscateClassesTask" should {
    "obfuscate a class file that declares a record" in withTempDir { dir =>
      val file = writeRecord(dir)
      new String(Files.readAllBytes(file), "ISO-8859-1") should include("PKCS12")

      task(dir, new RecordingLogger, Scope.all).execute()

      val rewritten = new String(Files.readAllBytes(file), "ISO-8859-1")
      rewritten should not include "PKCS12"
      rewritten should include("truelicense/obfuscate/ObfuscatedString")
      // The record component must survive the rewrite.
      rewritten should include("name")
    }

    "fail after logging an error" in withTempDir { dir =>
      writeAnnotatedNonConstantField(dir)
      val log = new RecordingLogger

      val thrown = the[Exception] thrownBy task(dir, log, Scope.annotated).execute()

      thrown.getMessage shouldBe "Errors have been logged."
      log.errors should have size 1
      log.errors.head should include("does not have a constant string value")
    }
  }
}
