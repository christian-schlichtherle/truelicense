package global.namespace.truelicense.core

import java.security.SecureRandom

import global.namespace.fun.io.api.Filter
import global.namespace.fun.io.bios.BIOS._
import global.namespace.truelicense.core.Filters.compressionAndEncryption
import global.namespace.truelicense.core.FiltersSpec._
import javax.crypto.Cipher.{DECRYPT_MODE, ENCRYPT_MODE}
import javax.crypto.spec.{PBEKeySpec, PBEParameterSpec}
import javax.crypto.{Cipher, SecretKeyFactory}
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.TableDrivenPropertyChecks._

class FiltersSpec extends WordSpec {

  "compressAndEncrypt()" should {
    "round trip compress and encrypt some data" in {
      forAll(Tests) { (compression, encryption) =>
        val filter = compressionAndEncryption(compression, encryption)
        val store = memory map filter
        store content Message.getBytes
        new String(store.content) shouldBe Message
      }
    }

    "transparently read some data which has been processed in the wrong order when writing (issue #7)" in {
      forAll(Tests) { (compression, encryption) =>
        val wrong = compressionAndEncryption(encryption, compression)
        val right = compressionAndEncryption(compression, encryption)
        val store = memory
        store map wrong content Message.getBytes
        new String((store map right).content) shouldBe Message
      }
    }
  }
}

private object FiltersSpec {

  private val Message = "Hello world!"

  private[this] val Algorithm = "PBEWithMD5AndDES"

  private[this] val Skf = SecretKeyFactory getInstance Algorithm

  private[this] val PbeKeySpec = new PBEKeySpec("secret".toCharArray)

  private[this] val PbeParameterSpec = {
    val salt = new Array[Byte](8)
    new SecureRandom nextBytes salt
    new PBEParameterSpec(salt, 2017)
  }

  private[this] val PBE: Filter = {
    cipher { outputMode: java.lang.Boolean =>
      val secretKey = Skf generateSecret PbeKeySpec
      val cipher = Cipher getInstance Algorithm
      cipher.init(if (outputMode) ENCRYPT_MODE else DECRYPT_MODE, secretKey, PbeParameterSpec)
      cipher
    }
  }

  private val Tests = Table(
    ("compression", "encryption"),
    (deflate, PBE),
    (gzip, PBE)
  )
}
