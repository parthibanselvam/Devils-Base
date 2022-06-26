package org.ets.core.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class MultifieldListModelTest {

    private final AemContext ctx = new AemContext();

    @Mock
    private ModelFactory modelFactory;

    @Mock
    Resource mockResource;

    @BeforeEach
    public void setUp() throws Exception {
        ctx.addModelsForClasses(MultifieldListModel.class);
        ctx.load().json("/org/ets/core/models/MultifieldResource.json", "/content");
        ctx.registerService(ModelFactory.class, modelFactory, org.osgi.framework.Constants.SERVICE_RANKING,
                Integer.MAX_VALUE);
    }

    @Test
    void testGetMultifield() {
        final Map<String,Resource> expected = new HashMap<String,Resource>();
        expected.put("item0", null);
        expected.put("item1", null);
        expected.put("item2", null);
        expected.put("item3", null);
        Resource sample=ctx.currentResource("/content/herobanner");
        MultifieldListModel multifieldListModel = sample.adaptTo(MultifieldListModel.class);
        Map<String,Resource> actual=new HashMap<String,Resource>();
        if(null!=multifieldListModel) {
            actual= multifieldListModel.getMultifield();
        }
        assertNotNull(actual);
    }

}