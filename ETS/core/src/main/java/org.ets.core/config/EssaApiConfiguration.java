package org.ets.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * This interface represents an OSGi configuration for eSSA API which can be found at - 
 * ./system/console/configMgr
 */
@ObjectClassDefinition(
        name = "ETS eSSA API Configuration", 
        description = "This configuration reads the values to make an HTTP call to eSSA API")
public @interface EssaApiConfiguration {

    @AttributeDefinition(
            name = "TokenApiEndpoint", 
            description = "Enter the end point for token")
    public String getTokenApiEndpoint();

    @AttributeDefinition(
            name = "Location Availability Endpoint", 
            description = "Enter the end point for location")
    public String getLocationAvailabilityEndpoint();

    @AttributeDefinition(
            name = "Username",
            description = "Enter the Username")
    public String getUsername();

    @AttributeDefinition(
            name = "Password",
            description = "Enter the Password",
            type = AttributeType.PASSWORD)
    public String getPassword();

    @AttributeDefinition(
            name = "Expiry Age",
            description = "Enter the expiry age",
            type = AttributeType.INTEGER)
    public int getExpiryTime();

    @AttributeDefinition(
            name = "Seats Availability Endpoint", 
            description = "Enter the end point for Seats Availability")
    public String getSeatsAvailabilityEndpoint();

}