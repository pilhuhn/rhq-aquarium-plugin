
package org.rhq.modules.plugins.aquarium;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.measurement.DataType;
import org.rhq.core.domain.measurement.MeasurementDataTrait;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.core.pluginapi.operation.OperationContext;
import org.rhq.core.pluginapi.operation.OperationFacet;
import org.rhq.core.pluginapi.operation.OperationResult;


public class BaseComponent <T extends ResourceComponent<?>> implements ResourceComponent<T>, MeasurementFacet , OperationFacet
{
    private final Log log = LogFactory.getLog(this.getClass());

    private ResourceContext<T> context;

    private String path;
    private GFConnection connection;

    /**
     * Return availability of this resource
     *  @see org.rhq.core.pluginapi.inventory.ResourceComponent#getAvailability()
     */
    public AvailabilityType getAvailability() {
        GFResponse res = connection.get(path);
        if (res==null)
            return AvailabilityType.DOWN;

        if (res._statusCode==200)
            return AvailabilityType.UP;

        return AvailabilityType.DOWN;
    }


    /**
     * Start the resource connection
     * @see org.rhq.core.pluginapi.inventory.ResourceComponent#start(org.rhq.core.pluginapi.inventory.ResourceContext)
     */
    public void start(ResourceContext<T> context) throws InvalidPluginConfigurationException, Exception {

        this.context = context;
        Configuration conf = context.getPluginConfiguration();
        path = conf.getSimpleValue("path");

        if (context.getParentResourceComponent() instanceof BaseComponent) {
            path = ((BaseComponent) context.getParentResourceComponent()).getPath() + "/" + path;
            connection = ((BaseComponent) context.getParentResourceComponent()).getConnection();
        }
        else {

            String host = conf.getSimpleValue("host");
            String portString = conf.getSimpleValue("port");
            String baseUrl = "http://" + host + ":" + portString + "/monitoring"  ;
            connection = new GFConnection(baseUrl);
        }
    }


    /**
     * Tear down the resource connection
     * @see org.rhq.core.pluginapi.inventory.ResourceComponent#stop()
     */
    public void stop() {


    }



    /**
     * Gather measurement data
     *  @see org.rhq.core.pluginapi.measurement.MeasurementFacet#getValues(org.rhq.core.domain.measurement.MeasurementReport, java.util.Set)
     */
    public  void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> metrics) throws Exception {


        Map values = (Map) connection.get(path).extraProperties.get("entity");

         for (MeasurementScheduleRequest req : metrics) {
            if (values.containsKey(req.getName())) {
                Object oo = values.get(req.getName());
                Map<String,?> m = (Map) oo;
                Object o = m.get("count");
                if (req.getDataType()== DataType.MEASUREMENT) {
                    Double val = Double.parseDouble(o.toString());
                    MeasurementDataNumeric res = new MeasurementDataNumeric(req,val);
                    report.addData(res);
                }
                else if (req.getDataType()==DataType.TRAIT) {
                    String s = o.toString();
                    MeasurementDataTrait res = new MeasurementDataTrait(req,s);
                    report.addData(res);
                }
            }
             else {
                log.warn("No attribute '" + req.getName() + "' found at " + path);
            }
         }
    }



    public void startOperationFacet(OperationContext context) {

    }


    /**
     * Invokes the passed operation on the managed resource
     * @param name Name of the operation
     * @param params The method parameters
     * @return An operation result
     * @see org.rhq.core.pluginapi.operation.OperationFacet
     */
    public OperationResult invokeOperation(String name, Configuration params) throws Exception {

        OperationResult res = new OperationResult();
        if ("dummyOperation".equals(name)) {
            // TODO implement me

        }
        return res;
    }

    public String getPath() {
        return path;
    }

    public GFConnection getConnection() {
        return connection;
    }
}
