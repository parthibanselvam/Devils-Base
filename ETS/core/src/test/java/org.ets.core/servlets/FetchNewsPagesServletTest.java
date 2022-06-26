package org.ets.core.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class,MockitoExtension.class})
class FetchNewsPagesServletTest {

    private AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    FetchNewsPagesServlet testServlet = new FetchNewsPagesServlet();

    private Map<String, Object> parameterMap = new HashMap<String, Object>();

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query query;

    @Mock
    private SearchResult searchResult;

    private List<Resource> queryResults = new ArrayList<>();

     @BeforeEach
        public void setUp() throws Exception {
         aemContext.registerAdapter(ResourceResolver.class, QueryBuilder.class, queryBuilder);
         lenient().when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);
         lenient().when(query.getResult()).thenReturn(searchResult);
         aemContext.load().json("/org/ets/core/servlets/FetchNewsResource.json", "/content");
         queryResults.add(aemContext.currentResource("/content/press-release/article-one"));
         queryResults.add(aemContext.currentResource("/content/press-release/article-two"));
         queryResults.add(aemContext.currentResource("/content/press-release/article-three"));
         Iterator<Resource> resultIterator=queryResults.iterator();
         lenient().when(searchResult.getResources()).thenReturn(resultIterator);
        }

    @Test
    void testDoGetSlingHttpServletRequestSlingHttpServletResponse() {
        FetchNewsPagesServlet fetchNewsServletObj = new FetchNewsPagesServlet();
        aemContext.currentResource("/content/press-release");
        MockSlingHttpServletRequest mockSlingRequest = aemContext.request();
        MockSlingHttpServletResponse mockSlingResponse = aemContext.response();
        parameterMap.put("path", "/content/press-release");    
        parameterMap.put("category", "ets:category/research");    
        parameterMap.put("contentType", "ets:filters/press-releases");    
        mockSlingRequest.setParameterMap(parameterMap);
        fetchNewsServletObj.doGet(mockSlingRequest, mockSlingResponse);
        assertEquals(200, mockSlingResponse.getStatus());
    }
}