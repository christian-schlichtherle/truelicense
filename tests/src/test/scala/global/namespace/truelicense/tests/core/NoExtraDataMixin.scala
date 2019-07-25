/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.core

trait NoExtraDataMixin {
  this: TestContext =>

  final override def extraData: AnyRef = null
}
