/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

/**
 * Provides life cycle management for license keys.
 * License keys are small binary objects which can be stored in a file, a
 * preferences node, the heap or any custom implementation of the
 * {@link net.truelicense.api.io.Store} interface.
 * License keys pass the following life cycle phases as defined by the
 * interfaces {@link net.truelicense.api.VendorLicenseManager} and
 * {@link net.truelicense.api.ConsumerLicenseManager}:
 * <ol>
 * <li>{@linkplain net.truelicense.api.VendorLicenseManager#generateKeyFrom generation},
 * <li>{@linkplain net.truelicense.api.ConsumerLicenseManager#install installation},
 * <li>{@linkplain net.truelicense.api.ConsumerLicenseManager#load loading},
 * <li>{@linkplain net.truelicense.api.ConsumerLicenseManager#verify verification} and
 * <li>{@linkplain net.truelicense.api.ConsumerLicenseManager#uninstall uninstallation}
 * </ol>
 * <p>
 * A license bean is an instance of the
 * {@link net.truelicense.api.License} class.
 * This class follows the Java Bean pattern and defines common properties for
 * the license management subject, issue date, issuer, holder, consumer
 * type/amount, validity period and custom data.
 * These properties get
 * {@linkplain net.truelicense.api.LicenseInitialization#initialize initialized}
 * and
 * {@linkplain net.truelicense.api.LicenseValidation#validate validated}
 * whenever a license key gets generated.
 * They also get validated whenever a license key gets installed or verified.
 * <p>
 * A license key gets generated from a license bean by applying the following
 * function composition:
 * <pre>
 * {@code encrypt(compress(encode(sign(encode(validate(initialize(duplicate(bean))))))))}
 * </pre>
 * <p>
 * When installing or verifying a license key, a duplicate of the original
 * license bean gets derived by applying the following (almost
 * inverse) function composition (note that there is no initialization and
 * hence no duplication required):
 * <pre>
 * {@code validate(decode(authenticate(decode(decompress(decrypt(key))))))}
 * </pre>
 * <p>
 * The validation step is skipped when a license key is just viewed.
 * Encryption is done via JCA with a configurable password based encryption
 * algorithm.
 * Authentication is done via JCA with a configurable key store type and
 * signature algorithm.
 * Encoding is done with {@link java.beans.XMLEncoder} for simple,
 * schema-less, yet resilient long term archival.
 * <p>
 * TrueLicense 3 retains full compatibility with TrueLicense 1 ({@code V1}) and
 * TrueLicense 2 ({@code V2/*}) format license keys.
 * <p>
 * Applications use an instance of the interface
 * {@link net.truelicense.api.LicenseApplicationContext} as their starting point
 * for configuring a {@link net.truelicense.api.LicenseManagementContext} and
 * subsequently one or more {@link net.truelicense.api.ConsumerLicenseManager}
 * or {@link net.truelicense.api.VendorLicenseManager} instances.
 * <p>
 * For security, passwords are represented as an instance of the
 * {@link net.truelicense.api.passwd.PasswordProtection} interface.
 * Other security critical strings (algorithms, paths, etc.) are annotatable
 * with {@literal @}{@link net.truelicense.obfuscate.Obfuscate} and
 * processible with the TrueLicense Maven Plugin for simple, reliable
 * obfuscation of constant string values in the byte code for license
 * applications.
 * <p>
 * <strong>Warning:</strong>
 * To protect against reverse engineering and modification of your
 * application, it is strictly necessary to generally process <em>all</em> byte
 * code which is comprised in your deployment unit with a tool for byte code
 * obfuscation.
 * TrueLicense does not provide general byte code obfuscation because there
 * exist good quality third party tools, e.g. ProGuard.
 *
 * @author Christian Schlichtherle
 */
package net.truelicense.api;
