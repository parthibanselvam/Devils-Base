package org.ets.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.ets.core.services.impl.ExcelToJsonServiceImpl;
import org.ets.core.utils.EtsResourceUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Excel to JSON Converter Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/ets/json/from/xls" })
public class ExcelToJSONServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -2613666358166400244L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelToJSONServlet.class);

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        ExcelToJsonServiceImpl excelToJsonServiceImpl = new ExcelToJsonServiceImpl();
        ResourceResolver resourceResolver;
        try {
            resourceResolver = EtsResourceUtil.getResourceResolver(resolverFactory);
            Workbook workbook = excelToJsonServiceImpl.getWorkbook(request,response);
            if(workbook!=null) {
                excelToJsonServiceImpl.dataExtractor(request, response, resourceResolver, workbook);
            }
        } catch (LoginException | IOException e) {
            LOGGER.error("Exception in XLS to JSON conversion {}",e.getMessage());
        }
    }
}