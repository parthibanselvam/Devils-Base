package org.ets.core.services;

import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

/**
 * This interface exposes the functionality of calling a JSON Web Service
 */

public interface EssaApiService {

    public Cookie setTokenCookie(SlingHttpServletRequest request, SlingHttpServletResponse response);

    /**
     * @param token
     * @param request
     * @return
     */
    public String getAvailableLocation(String token, SlingHttpServletRequest request);

    /**
     * @param token
     * @param request
     * @return
     */
    public String getAvailableSeats(String token, SlingHttpServletRequest request);

}