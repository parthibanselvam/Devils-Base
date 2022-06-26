package org.ets.core.servlets;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.ets.core.services.EssaApiService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/bin/ets/test-location-availability",
        "sling.servlet.extensions=" + "json"})
@ServiceDescription("Test location availability Servlet")
public class TestLocationAvailabilityServlet extends SlingSafeMethodsServlet implements Serializable{

    private static final long serialVersionUID = -5835590412332179115L;

    protected static final Logger log = LoggerFactory.getLogger(TestLocationAvailabilityServlet.class);

    @Reference
    private EssaApiService essaApiService;

    @Override
        protected void doGet(SlingHttpServletRequest request, final SlingHttpServletResponse response) {
            try
            {
                Cookie cookie = essaApiService.setTokenCookie(request, response);
                String jsonResponse = essaApiService.getAvailableLocation(cookie.getValue(), request);
                response.setContentType("application/json");
                response.getWriter().print(jsonResponse);
            }catch (IOException e) {
                log.error("IOException in AllSeatsAvailabilityServlet : {}",e.getMessage());
            }
         }
}