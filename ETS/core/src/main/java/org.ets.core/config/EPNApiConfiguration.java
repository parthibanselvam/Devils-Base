package org.ets.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


@ObjectClassDefinition(
        name = "EPN Contact API Configuration", 
        description = "This configuration reads the values to make an HTTP call to EPN Contact API")

public @interface EPNApiConfiguration {

    @AttributeDefinition(
            name = "AccessTokenUrl", 
            description = "Enter the Access token url for token")
    public String getTokenApiEndpoint();

    @AttributeDefinition(
            name = "EPN Endpoint", 
            description = "Enter the end point for SalseforceAPI")
    public String getEpnEndpoint();

    @AttributeDefinition(
            name = "ClientId", 
            description = "Enter the client id for token")
    public String getClientId();

    @AttributeDefinition(
            name = "ClientSecret", 
            description = "Enter the client secret for token")
    public String getSecret();

    @AttributeDefinition(
            name = "Username",
            description = "Enter the Username")
    public String getUsername();

    @AttributeDefinition(
            name = "Password",
            description = "Enter the Password")
    public String getPassword();

}