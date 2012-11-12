package org.rhq.modules.plugins.aquarium.misc;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.rhq.modules.plugins.aquarium.GFConnection;
import org.rhq.modules.plugins.aquarium.GFResponse;

/**
 * Generator to create plugin descriptor snippets.
 * The base path is given as argument and the generates services
 * recursively from that starting point. The result is written to
 * standard out.
 * @author Heiko W. Rupp
 */
public class Generator {

    private final Log log = LogFactory.getLog(Generator.class);
    private GFConnection conn;

    public static void main(String[] args) throws Exception {
        String path = args[0];
        Generator gen = new Generator();
        gen.run(path);
    }

    private void run(String path) {

        String baseUrl = "http://localhost:4848/monitoring/domain/server";
        conn = new GFConnection(baseUrl);

        if (path.endsWith("/")) {
            path = path.substring(0,path.length()-1);
        }

        GFResponse response = conn.get(path);
        if (response.get_statusCode()!=200) {
            log.error("Got a " + response.get_statusCode() + " for " + path);
            return;
        }

        String serviceName;
        if (path.contains("/")) {
            serviceName = path.substring(path.lastIndexOf("/")+1);
        } else {
            serviceName = path;
        }

        createHeader(serviceName);
        createMetrics(response);
        createChildren(response, path);
        System.out.println("</service>");

    }

    private void createChildren(GFResponse response, String parentPath) {
        Map<String,String> cMap = (Map<String, String>) response.getExtraProperties().get("childResources");
        if (cMap==null)
            return;

        for (String name : cMap.keySet()) {
            createHeader(name);

            GFResponse cResponse = conn.get(parentPath + "/" + name);
            createMetrics(cResponse);

            System.out.println("</service>");
        }


    }

    private void createHeader(String serviceName) {
        System.out.println("<service name=\"" + serviceName + "\"");
        System.out.println("    class=\"BaseComponent\"");
        System.out.println("    discovery=\"SubsystemDiscovery\"");
        System.out.println(">");
        System.out.println("  <plugin-configuration>");
        System.out.println("    <c:simple-property name=\"path\" default=\"" + serviceName + "\" readOnly=\"true\"/>");
        System.out.println("  </plugin-configuration>");
    }

    private void createMetrics(GFResponse response) {
        Map<String,?> eMap = (Map<String, ?>) response.getExtraProperties().get("entity");
        for (String entry: eMap.keySet()) {
            Map<String,?> map = (Map<String, ?>) eMap.get(entry);
            StringBuilder builder = new StringBuilder("  <metric property=\"");
            builder.append(entry).append("\" displayName=\"");
            builder.append(map.get("name")).append("\" ");
            String unit = (String) map.get("unit");
            unit = mangleUnit(unit);
            if (unit.equals("string")) {
                builder.append("dataType=\"trait\" ");
            } else {
                builder.append("units=\"").append(unit).append("\"");
            }
            builder.append(" \n");
            builder.append("    description=\"" ).append(map.get("description")).append("\"/>");
            System.out.println(builder.toString());
        }
    }

    private String mangleUnit(String unit) {

        if (unit.equals("count"))
            unit ="none";
        else if (unit.equals("String"))
            unit = "string";
        else if (unit.equals("nanosecond"))
            unit = "nanoseconds";
        else if (unit.equals("millisecond"))
            unit = "milliseconds";


        return unit;
    }

}
