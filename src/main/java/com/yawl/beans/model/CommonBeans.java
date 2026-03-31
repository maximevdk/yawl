package com.yawl.beans.model;

/**
 * Constants for the names of framework-provided beans registered in the {@link com.yawl.beans.ApplicationContext}.
 */
public final class CommonBeans {
    private CommonBeans() {}

    /** Bean name for the {@link com.yawl.configuration.ApplicationProperties.Application} instance. */
    public static final String APPLICATION_PROPERTIES_NAME = "applicationProperties";
    /** Bean name for the {@link tools.jackson.databind.json.JsonMapper} instance. */
    public static final String JSON_MAPPER_NAME = "jsonMapper";
    /** Bean name for the {@link tools.jackson.dataformat.yaml.YAMLMapper} instance. */
    public static final String YAML_MAPPER_NAME = "yamlMapper";
    /** Bean name for the {@link com.yawl.events.EventPublisher} instance. */
    public static final String EVENT_PUBLISHER_NAME = "eventPublisher";
    /** Bean name for the {@link com.yawl.configuration.Environment} instance. */
    public static final String ENVIRONMENT_NAME = "environment";
    /** Bean name for the embedded web server. */
    public static final String WEB_SERVER_NAME = "webserver";
    /** Bean name for the request parameter argument resolver. */
    public static final String REQUEST_PARAMETER_ARGUMENT_RESOLVER = "requestParameterArgumentResolver";
}
