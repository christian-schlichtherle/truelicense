/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package ${package}.keymgrsvc;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import static java.lang.System.*;
import net.truelicense.jax.rs.*;

/**
 * @author Christian Schlichtherle
 */
public class Main {

    public static void main(final String[] args) throws IOException {
        final ResourceConfig rc = new DefaultResourceConfig();
        rc.getClasses().add(ConsumerLicenseManagementService.class);
        rc.getClasses().add(ConsumerLicenseManagementServiceExceptionMapper.class);
        rc.getSingletons().add(new ConsumerLicenseManagerResolver());
        rc.getSingletons().add(new JacksonJaxbJsonProvider());
        final HttpServer server = HttpServerFactory.create("http://localhost:9998/", rc);
        server.start();

        out.println("Server running.");
        out.println("Visit: http://localhost:9998/license");
        out.println("Hit Enter to stop.");
        in.read();
        out.println("Stopping server...");
        server.stop(0);

        out.println("Server stopped.");
    }
}
