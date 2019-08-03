/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v2.core

import global.namespace.truelicense.tests.core.TestContext
import global.namespace.truelicense.tests.v2.core.V2TestContext.prefix

trait V2TestContext extends TestContext {

  protected def chainedConsumerKeystoreName: String = prefix + "chained-public.jceks"

  protected def chainedVendorKeystoreName: String = prefix + "chained-private.jceks"

  protected def consumerKeystoreName: String = prefix + "public.jceks"

  protected def ftpConsumerKeystoreName: String = prefix + "ftp.jceks"

  protected def vendorKeystoreName: String = prefix + "private.jceks"
}

object V2TestContext {

  private def prefix = classOf[V2TestContext].getPackage.getName.replace('.', '/') + '/'
}
