/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.v1

import de.schlichtherle.xml.GenericCertificate
import net.truelicense.api.auth.RepositoryContext
import net.truelicense.it.core.RepositoryITSuite
import net.truelicense.v1.auth.V1RepositoryContext

/** @author Christian Schlichtherle */
class V1RepositoryIT extends RepositoryITSuite[GenericCertificate] with V1TestContext {

  val context: RepositoryContext[GenericCertificate] = new V1RepositoryContext
}
