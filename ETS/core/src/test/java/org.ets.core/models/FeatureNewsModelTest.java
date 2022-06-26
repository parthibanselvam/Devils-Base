package org.ets.core.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FeatureNewsModelTest {

    private AemContext ctx = new AemContext();

    @Mock
    private ModelFactory modelFactory;

    @BeforeEach
    public void setUp() throws Exception {
        ctx.addModelsForClasses(FeatureNewsModel.class);
        ctx.load().json("/org/ets/core/models/FeatureNewsResource.json", "/content");
        ctx.registerService(ModelFactory.class, modelFactory, org.osgi.framework.Constants.SERVICE_RANKING,
                Integer.MAX_VALUE);
    }
    @Test
    void getPageList() {
        ctx.currentResource("/content/feature-news/jcr:content/featurenews");
        FeatureNewsModel featureNewsModel = ctx.request().adaptTo(FeatureNewsModel.class);
        assertNotNull(featureNewsModel.getPageList());
    }
}