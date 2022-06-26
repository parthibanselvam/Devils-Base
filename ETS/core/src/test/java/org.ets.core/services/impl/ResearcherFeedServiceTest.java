package org.ets.core.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.ets.core.config.ResearcherFeedConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ResearcherFeedServiceTest {

    AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);
    private ResearcherFeedServiceImpl underTest;

    @Mock
    private QueryBuilder queryBuilder1;

    @Mock
    private Query query1;

    @Mock
    private SearchResult searchResult1;

    private List<Resource> queryResults1 = new ArrayList<>();
    private ResourceResolver resourceResolver;

    @BeforeEach
    void setUp() {
        ctx.load().json("/org/ets/core/service/ResearcherFeedService.json", "/content/ets-org/language-master/en/research/policy_research_reports/publications/report");
        resourceResolver = ctx.resourceResolver();
        underTest = ctx.registerService(new ResearcherFeedServiceImpl());
        ctx.registerAdapter(ResourceResolver.class, QueryBuilder.class, queryBuilder1);
        lenient().when(queryBuilder1.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query1);
        lenient().when(query1.getResult()).thenReturn(searchResult1);
        queryResults1.add(ctx.currentResource("/content/ets-org/language-master/en/research/policy_research_reports/publications/report/2022/kdyf/jcr:content/root/container/researcher"));
        queryResults1.add(ctx.currentResource("/content/ets-org/language-master/en/research/policy_research_reports/publications/report/2022/kdya/jcr:content/root/container/researcher"));
        queryResults1.add(ctx.currentResource("/content/ets-org/language-master/en/research/policy_research_reports/publications/report/2022/kdki/jcr:content/root/container/researcher"));
        Iterator<Resource> resultIterator=queryResults1.iterator();
        lenient().when(searchResult1.getResources()).thenReturn(resultIterator);

        ResearcherFeedConfiguration config = mock(ResearcherFeedConfiguration.class);
        lenient().when(config.researcher_parent_path()).thenReturn("/content/ets-org/language-master/en/research/policy_research_reports/publications");
        lenient().when(config.researcher_json_storage_path()).thenReturn("/content/dam/ets-org/s/sydney/feeds");
        lenient().when(config.researcher_json_filename()).thenReturn("researcher.json");
        underTest.activate(config);
    }

    @Test
    void run() {
        ctx.currentResource("/content/ets-org/language-master/en/research/policy_research_reports/publications/report/2022");
        underTest.createJsonFeed(resourceResolver, queryBuilder1);
    }
}