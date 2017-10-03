/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.v1

import de.schlichtherle.xml.GenericCertificate
import net.truelicense.it.core.RepositoryTestSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V1RepositoryTest
extends RepositoryTestSuite[GenericCertificate]
with V1TestContext
