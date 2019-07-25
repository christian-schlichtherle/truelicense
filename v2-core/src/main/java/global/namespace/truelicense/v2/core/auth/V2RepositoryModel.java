/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.v2.core.auth;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A repository model for use with V2 format license keys.
 * All properties are set to {@code null} by default.
 *
 * @author Christian Schlichtherle
 */
// #TRUELICENSE-50: The XML root element name MUST be explicitly defined!
// Otherwise, it would get derived from the class name, but this would break if the class name gets obfuscated, e.g.
// when using the ProGuard configuration from the TrueLicense Maven Archetype.
@XmlRootElement(name = "repository")
// #TRUELICENSE-52: Dito for the XML type.
// This annotation enables objects of this class to participate in larger object graphs which the application wants to
// encode/decode to/from XML.
@XmlType(name = "repository")
public final class V2RepositoryModel {

    @XmlElement(required = true)
    public  String algorithm, artifact, signature;
}
