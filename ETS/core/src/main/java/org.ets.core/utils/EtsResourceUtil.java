package org.ets.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

public class EtsResourceUtil {

    private static final String SUBSERVICE_NAME = "ets-resource-resolver";

    public static ResourceResolver getResourceResolver(ResourceResolverFactory resolverFactory) throws LoginException {
        Map<String, Object> serviceNameParam = new HashMap<>();
        serviceNameParam.put(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_NAME);
        return resolverFactory.getServiceResourceResolver(serviceNameParam);
    }
}