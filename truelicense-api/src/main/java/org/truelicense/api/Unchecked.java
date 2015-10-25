package org.truelicense.api;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.misc.ContextProvider;

import java.util.Objects;

/**
 * Wraps license managers so that they throw unchecked exceptions instead of
 * checked exceptions.
 *
 * @author Christian Schlichtherle
 */
public final class Unchecked {

    private Unchecked() { }

    /**
     * Returns a vendor license manager which throws unchecked exceptions
     * instead of checked exceptions.
     *
     * @param manager the vendor license manager to wrap.
     */
    public static UncheckedVendorLicenseManager wrap(VendorLicenseManager manager) {
        return new UncheckedVendorTrueLicenseManager(manager);
    }

    /**
     * Returns a consumer license manager which throws unchecked exceptions
     * instead of checked exceptions.
     *
     * @param manager the consumer license manager to wrap.
     */
    public static UncheckedConsumerLicenseManager wrap(ConsumerLicenseManager manager) {
        return new UncheckedConsumerTrueLicenseManager(manager);
    }

    static <V> V wrap(final Task<V> task) {
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
                    return wrap(new Task<License>() {
                        @Override
                        public License run() throws LicenseManagementException {
                            return generator.license();
                        }
                    });
                }

                @Override
                public LicenseKeyGenerator writeTo(final Sink sink) throws UncheckedLicenseManagementException {
                    return wrap(new Task<LicenseKeyGenerator>() {
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
            wrap(new Task<Void>() {
                @Override
                public Void run() throws LicenseManagementException {
                    manager.install(source);
                    return null;
                }
            });
        }

        @Override
        public License view() throws UncheckedLicenseManagementException {
            return wrap(new Task<License>() {
                @Override
                public License run() throws LicenseManagementException {
                    return manager.view();
                }
            });
        }

        @Override
        public void verify() throws UncheckedLicenseManagementException {
            wrap(new Task<Void>() {
                @Override
                public Void run() throws LicenseManagementException {
                    manager.verify();
                    return null;
                }
            });
        }

        @Override
        public void uninstall() throws UncheckedLicenseManagementException {
            wrap(new Task<Void>() {
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

        @Override
        public LicenseManagementContext context() {
            return manager.context();
        }

        @Override
        public LicenseManagementParameters parameters() {
            return manager.parameters();
        }
    }

    private interface Task<V> {

        V run() throws LicenseManagementException;
    }
}
