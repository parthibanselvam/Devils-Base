package org.ets.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.ets.core.helper.FetchProductsHelper;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class, property={
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/bin/ets/fetchProducts" })
@ServiceDescription("Products Servlet")
public class FetchProductsServlet extends SlingSafeMethodsServlet implements Serializable {

    private static final long serialVersionUID = 78746479550010233L;
    protected static final Logger log = LoggerFactory.getLogger(FetchProductsServlet.class);

    private static final String PATH = "path";

    private Map<String,Object> mapObject = new HashMap<>();
    private FetchProductsHelper fetchProductsHelper = new FetchProductsHelper();

    @Override
    protected void doGet(SlingHttpServletRequest request, final SlingHttpServletResponse response) {

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = request.getResourceResolver();
            String productsCfFolderPath = request.getParameter(PATH);
            mapObject.put("ets-products",fetchProductsHelper.getProductsList(productsCfFolderPath,resourceResolver));
            mapObject.put("category-list",fetchProductsHelper.getCategorySet());
            JSONObject jsonObject = new JSONObject(mapObject);
            String jsonString = jsonObject.toString();
            log.info("JsonString: {}",jsonString);
            response.getWriter().write(jsonString);
        }
        catch(Exception e){
            log.error("Exception in FetchNewsPagesServlet class {}", e.getMessage());
        }
    }

}