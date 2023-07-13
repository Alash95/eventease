//package com.alash.eventease.config;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.servers.Server;
//import io.swagger.v3.oas.models.servers.ServerVariable;
//import io.swagger.v3.oas.models.servers.ServerVariables;
//import org.springdoc.core.SwaggerUiConfigParameters;
//import org.springdoc.core.SwaggerUiConfigProperties;
//import org.springdoc.ui.SwaggerConfig;
//import org.springdoc.ui.SwaggerUiConfig;
//import java.util.List;
//
//public class CustomSwaggerUiConfig extends SwaggerUiConfig {
//
//    public CustomSwaggerUiConfig(SwaggerUiConfigParameters swaggerUiConfigParameters, SwaggerUiConfigProperties swaggerUiConfigProperties) {
//        super(swaggerUiConfigParameters, swaggerUiConfigProperties);
//    }
//
//    @Override
//    public void addServers(SwaggerConfig swaggerConfig, OpenAPI openAPI) {
//        super.addServers(swaggerConfig, openAPI);
//
//        // Add the padlock button server
//        Server padlockServer = new Server();
//        padlockServer.setUrl("https://api.example.com"); // Replace with your padlock server URL
//
//        // Add the padlock button server variable
//        ServerVariable padlockVariable = new ServerVariable();
//        padlockVariable.setDefault("true");
//        padlockVariable.setDescription("Padlock Button Server");
//        padlockVariable.setEnumValues(List.of("true", "false"));
//
//        ServerVariables padlockVariables = new ServerVariables();
//        padlockVariables.addServerVariable("padlock", padlockVariable);
//
//        padlockServer.setVariables(padlockVariables);
//
//        openAPI.addServersItem(padlockServer);
//    }
//}
