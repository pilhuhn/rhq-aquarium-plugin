package org.rhq.modules.plugins.aquarium;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;

/**
 * Generic discovery for resources in the GF management tree
 * @author Heiko W. Rupp
 */
public class SubsystemDiscovery implements ResourceDiscoveryComponent<BaseComponent<?>> {

    @Override
    public Set<DiscoveredResourceDetails> discoverResources(
            ResourceDiscoveryContext<BaseComponent<?>> context) throws InvalidPluginConfigurationException, Exception {

        String parentPath = context.getParentResourceComponent().getPath();
        String path = context.getDefaultPluginConfiguration().getSimpleValue("path");
        if (path==null) {
            throw new InvalidPluginConfigurationException("Plugin descriptor did not specify a path for " + context.getResourceType().getName());
        }

        String localPath = path;
        if (parentPath.endsWith("/")) {
            path = parentPath + path;
        }
        else {
            path = parentPath + "/" + path;
        }

        Configuration config = new Configuration();
        config.put(new PropertySimple("path",path));

        // check if the resource is there
        GFConnection connection = context.getParentResourceComponent().getConnection();
        GFResponse res = connection.get(path);
        if (res == null || res._statusCode!=200) {
            return Collections.emptySet();
        }

        DiscoveredResourceDetails detail = new DiscoveredResourceDetails(
                context.getResourceType(), // ResourceType
                path, // Resource Key
                localPath, // resource Name
                null, // Version todo
                "Some " + localPath, // description
                context.getDefaultPluginConfiguration(),
                null);

        Set<DiscoveredResourceDetails> details = new HashSet<DiscoveredResourceDetails>(1);
        details.add(detail);


        return details;
    }
}
