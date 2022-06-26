package org.ets.core.services;

import org.apache.sling.api.SlingHttpServletRequest;

/**
 * This interface exposes the functionality of calling a JSON Web Service
 */


public interface EPNContactAPIService {


    /**
     * This method makes the HTTP call on the given URL
     * 
     * @param url
     * @return {@link String}
     */

    public String getToken();

    /**
     * @param token
     * @param request
     * @return
     */


    public String getEPNContact(String token, SlingHttpServletRequest request);
}