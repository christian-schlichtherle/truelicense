package global.namespace.truelicense.tests.core

import scala.jdk.CollectionConverters._

trait ExtraMapTestContext {
  this: TestContext =>

  override protected def extra: AnyRef = {
    Map("message" -> "This is some private extra data!").asJava
  }
}
