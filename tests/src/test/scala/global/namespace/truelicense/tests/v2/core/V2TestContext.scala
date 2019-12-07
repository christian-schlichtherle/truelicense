/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v2.core

import global.namespace.truelicense.tests.core.TestContext

trait V2TestContext extends TestContext {

  protected final lazy val prefix = classOf[V2TestContext].getPackage.getName.replace('.', '/') + '/'

  protected final lazy val postfix: String = ".jceks"
}
