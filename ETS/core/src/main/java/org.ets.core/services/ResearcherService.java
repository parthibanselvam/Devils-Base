package org.ets.core.services;

import java.io.InputStream;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * This interface exposes the functionality of calling a JSON Web Service
 */
public interface ResearcherService {
    public String convertXmlToPages(ResourceResolver resourceResolver, InputStream inputStream);
    public InputStream getXmlAsset(ResourceResolver resourceResolver, String assetPath);
}