package org.rhq.modules.plugins.aquarium;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ManualAddFacet;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;


/**
 * Discovery class
 */
public class BaseDiscovery implements ResourceDiscoveryComponent, ManualAddFacet {

    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * This method is an empty dummy, as you have selected manual addition
     * in the plugin generator.
     * If you want to have auto discovery too, remove the "return emptySet"
     * and implement the auto discovery logic.
     */
    public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext discoveryContext) throws Exception {

        Configuration config = discoveryContext.getDefaultPluginConfiguration();
        String host = config.getSimpleValue("host");
        String port = config.getSimpleValue("port");

        DiscoveredResourceDetails detail = new DiscoveredResourceDetails(
            discoveryContext.getResourceType(), // ResourceType
              host+":"+port, // resKey TODO
              host+":"+port, // resName TODO
              null, // Version TODO
              "Glassfish server",
              discoveryContext.getDefaultPluginConfiguration(),
              null
        );

        Set<DiscoveredResourceDetails> results = new HashSet<DiscoveredResourceDetails>(1);
        results.add(detail);

        return results;

        }

      /**
       * Do the manual add of this one resource
       */
      public DiscoveredResourceDetails discoverResource(Configuration pluginConfiguration, ResourceDiscoveryContext context) throws InvalidPluginConfigurationException {

            DiscoveredResourceDetails detail = new DiscoveredResourceDetails(
                context.getResourceType(), // ResourceType
                  "bla", // resKey TODO
                  "bla", // resName TODO
                  null, // Version TODO
                  "Glassfish server",
                  pluginConfiguration,
                  null
            );

            return detail;
      }
}