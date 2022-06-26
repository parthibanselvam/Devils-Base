package org.ets.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FetchProductsServletTest {

    private AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);
    private Map<String, Object> parameter = new HashMap<String, Object>();

    @BeforeEach
    public void setUp() throws Exception {
        ctx.load().json("/org/ets/core/servlets/FetchProductsResource.json", "/content/dam/ets-org/ets-content-fragment");
    }

    @Test
    void testDoGetSlingHttpServletRequestSlingHttpServletResponse() {
        FetchProductsServlet underTest = new FetchProductsServlet();
        ctx.currentResource("/content/dam/ets-org/ets-content-fragment/all-products-listing");
        MockSlingHttpServletRequest mockSlingRequest = ctx.request();
        MockSlingHttpServletResponse mockSlingResponse = ctx.response();
        parameter.put("path", "/content/dam/ets-org/ets-content-fragment/all-products-listing");
        mockSlingRequest.setParameterMap(parameter);
        underTest.doGet(mockSlingRequest, mockSlingResponse);
        assertEquals(200, mockSlingResponse.getStatus());

    }
}