package org.truelicense.api;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.misc.ContextProvider;

import java.util.Objects;

/**
 * Adapts vendor and consumer license managers so that they generally throw an
 * {@link UncheckedLicenseManagementException} rather than a (checked)
 * {@link LicenseManagementException}.
 *
 * @author Christian Schlichtherle
 */
public final class UncheckedManager {

    private UncheckedManager() { }

    /**
     * Returns a vendor license manager which generally throws an
     * {@link UncheckedLicenseManagementException} rather than a (checked)
     * {@link LicenseManagementException}.
     *
     * @param manager the vendor license manager to adapt.
     */
    public static UncheckedVendorLicenseManager from(VendorLicenseManager manager) {
        return new UncheckedVendorTrueLicenseManager(manager);
    }

    /**
     * Returns a consumer license manager which generally throws an
     * {@link UncheckedLicenseManagementException} rather than a (checked)
     * {@link LicenseManagementException}.
     *
     * @param manager the consumer license manager to adapt.
     */
    public static UncheckedConsumerLicenseManager from(ConsumerLicenseManager manager) {
        return new UncheckedConsumerTrueLicenseManager(manager);
    }

    static <V> V runUnchecked(final CheckedTask<V> task) {
        try {
            return task.run();
        } catch (LicenseManagementException e) {
            throw new UncheckedLicenseManagementException(e);
        }
    }

    private static final class UncheckedVendorTrueLicenseManager
    extends UncheckedTrueLicenseManager<VendorLicenseManager>
    implements UncheckedVendorLicenseManager {

        UncheckedVendorTrueLicenseManager(VendorLicenseManager manager) {
            super(manager);
        }

        @Override
        public UncheckedLicenseKeyGenerator generator(final License bean) {
            return new UncheckedLicenseKeyGenerator() {

                final LicenseKeyGenerator generator = manager.generator(bean);

                @Override
                public License license() throws UncheckedLicenseManagementException {
                    return runUnchecked(new CheckedTask<License>() {
                        @Override
                        public License run() throws LicenseManagementException {
                            return generator.license();
                        }
                    });
                }

                @Override
                public LicenseKeyGenerator writeTo(final Sink sink) throws UncheckedLicenseManagementException {
                    return runUnchecked(new CheckedTask<LicenseKeyGenerator>() {
                        @Override
                        public LicenseKeyGenerator run() throws LicenseManagementException {
                            return generator.writeTo(sink);
                        }
                    });
                }
            };
        }
    }

    private static final class UncheckedConsumerTrueLicenseManager
    extends UncheckedTrueLicenseManager<ConsumerLicenseManager>
    implements UncheckedConsumerLicenseManager {

        UncheckedConsumerTrueLicenseManager(ConsumerLicenseManager manager) {
            super(manager);
        }

        @Override
        public void install(final Source source) throws UncheckedLicenseManagementException {
            runUnchecked(new CheckedTask<Void>() {
                @Override
                public Void run() throws LicenseManagementException {
                    manager.install(source);
                    return null;
                }
            });
        }

        @Override
        public License view() throws UncheckedLicenseManagementException {
            return runUnchecked(new CheckedTask<License>() {
                @Override
                public License run() throws LicenseManagementException {
                    return manager.view();
                }
            });
        }

        @Override
        public void verify() throws UncheckedLicenseManagementException {
            runUnchecked(new CheckedTask<Void>() {
                @Override
                public Void run() throws LicenseManagementException {
                    manager.verify();
                    return null;
                }
            });
        }

        @Override
        public void uninstall() throws UncheckedLicenseManagementException {
            runUnchecked(new CheckedTask<Void>() {
                @Override
                public Void run() throws LicenseManagementException {
                    manager.uninstall();
                    return null;
                }
            });
        }
    }

    private static abstract class UncheckedTrueLicenseManager<
            M extends ContextProvider<LicenseManagementContext> & LicenseManagementParametersProvider>
    implements
            ContextProvider<LicenseManagementContext>,
            LicenseManagementParametersProvider {

        final M manager;

        UncheckedTrueLicenseManager(final M manager) {
            this.manager = Objects.requireNonNull(manager);
        }

        public M checked() { return manager; }

        @Override
        public LicenseManagementContext context() { return manager.context(); }

        @Override
        public LicenseManagementParameters parameters() {
            return manager.parameters();
        }
    }

    private interface CheckedTask<V> {

        V run() throws LicenseManagementException;
    }
}
