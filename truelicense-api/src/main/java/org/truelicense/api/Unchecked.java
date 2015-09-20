package org.truelicense.api;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.misc.ContextProvider;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author Christian Schlichtherle
 */
public final class Unchecked {

    public UncheckedVendorLicenseManager wrap(VendorLicenseManager manager) {
        return new UncheckedVendorTrueLicenseManager(manager);
    }

    public UncheckedConsumerLicenseManager wrap(ConsumerLicenseManager manager) {
        return new UncheckedConsumerTrueLicenseManager(manager);
    }

    private Unchecked() { }

    private static final class UncheckedVendorTrueLicenseManager
    extends UncheckedTrueLicenseManager<VendorLicenseManager>
    implements UncheckedVendorLicenseManager {

        UncheckedVendorTrueLicenseManager(VendorLicenseManager manager) {
            super(manager);
        }

        @Override
        public UncheckedLicenseKeyGenerator generator(final License bean) throws LicenseManagementRuntimeException {
            return new UncheckedLicenseKeyGenerator() {

                final LicenseKeyGenerator generator = manager.generator(bean);

                @Override
                public License license() throws LicenseManagementRuntimeException {
                    return wrap(new Callable<License>() {
                        @Override
                        public License call() throws Exception {
                            return generator.license();
                        }
                    });
                }

                @Override
                public LicenseKeyGenerator writeTo(final Sink sink) throws LicenseManagementRuntimeException {
                    return wrap(new Callable<LicenseKeyGenerator>() {
                        @Override
                        public LicenseKeyGenerator call() throws Exception {
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
        public void install(final Source source) throws LicenseManagementRuntimeException {
            wrap(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    manager.install(source);
                    return null;
                }
            });
        }

        @Override
        public License view() throws LicenseManagementRuntimeException {
            return wrap(new Callable<License>() {
                @Override
                public License call() throws Exception {
                    return manager.view();
                }
            });
        }

        @Override
        public void verify() throws LicenseManagementRuntimeException {
            wrap(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    manager.verify();
                    return null;
                }
            });
        }

        @Override
        public void uninstall() throws LicenseManagementRuntimeException {
            wrap(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
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

        static <V> V wrap(final Callable<V> task) {
            try {
                return task.call();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new LicenseManagementRuntimeException(e);
            }
        }

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
}
