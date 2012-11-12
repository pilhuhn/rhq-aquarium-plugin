package org.rhq.modules.plugins.aquarium;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.jackson.JacksonJsonpInterceptor;

/**
 * Interface to talk to the server
 * @author Heiko W. Rupp
 */
public class GFConnection {

    private String baseUrl;
    private Client client;

    public GFConnection(String baseUrl) {
        this.baseUrl = baseUrl;
        if (!this.baseUrl.endsWith("/")) {
            this.baseUrl = this.baseUrl + "/";
        }
        client = ClientFactory.newClient();

        // We need to manually reqister the providers, as the plugin containers' classloader is not
        // honoring the META-INF/service entries
        client.configuration().register(org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider.class);
        client.configuration().register(JacksonJsonpInterceptor.class);

    }

    public GFResponse get(String path) {
        String urlString = baseUrl + path;
        WebTarget target = client.target(urlString);

        Invocation.Builder request = target.request();
        request.header("Accept", MediaType.APPLICATION_JSON);
        Response response = request.get();
        GFResponse gfResponse = response.readEntity(GFResponse.class);
        gfResponse.set_statusCode(response.getStatus());

        return gfResponse;
    }
}
