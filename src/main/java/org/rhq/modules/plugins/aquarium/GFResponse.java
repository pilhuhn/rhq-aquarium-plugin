package org.rhq.modules.plugins.aquarium;

import java.util.Map;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * // TODO: Document this
 * @author Heiko W. Rupp
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class GFResponse {

    public GFResponse() {
    }

    String message;
    String command;
    String exit_code;
    Map<String,Object> extraProperties;
    int _statusCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getExit_code() {
        return exit_code;
    }

    public void setExit_code(String exit_code) {
        this.exit_code = exit_code;
    }

    public Map<String, Object> getExtraProperties() {
        return extraProperties;
    }

    public void setExtraProperties(Map<String, Object> extraProperties) {
        this.extraProperties = extraProperties;
    }

    public int get_statusCode() {
        return _statusCode;
    }

    public void set_statusCode(int _statusCode) {
        this._statusCode = _statusCode;
    }

}
