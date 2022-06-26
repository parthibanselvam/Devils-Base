package org.ets.core.servlets;


import java.io.Serializable;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.ets.core.services.EPNContactAPIService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = Servlet.class, property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/bin/ets/test-epn-contact",
        "sling.servlet.extensions=" + "json"})
@ServiceDescription("EPN Contact Servlet")

public class EPNContactServlet extends SlingSafeMethodsServlet implements Serializable{

    private static final long serialVersionUID = -5835590412332179115L;

    protected static final Logger log = LoggerFactory.getLogger(EPNContactServlet.class);
    @Reference
    EPNContactAPIService epnContactService;

    @Override
    protected void doGet(SlingHttpServletRequest request, final SlingHttpServletResponse response) {

        log.info("Start of EPN servlet");
    try{
        Cookie cookie=request.getCookie("epnBearerToken");
        if(cookie==null) {
            String accessToken=epnContactService.getToken();
            cookie=new Cookie("epnBearerToken", accessToken);
            cookie.setPath("/");
            cookie.setMaxAge(59*60);
            cookie.setDomain(request.getServerName());
            response.addCookie(cookie);
            log.info("token accesstoken{} ", accessToken);
        }
        String jsonResponse = epnContactService.getEPNContact(cookie.getValue(), request);
        /* Printing the JSON response on the browser*/
        response.setContentType("application/json");
        response.getWriter().print(jsonResponse);
    }catch (Exception e) {
        log.error(e.getMessage(), e);
    }
    log.info("End of EPN servlet");
    }
}