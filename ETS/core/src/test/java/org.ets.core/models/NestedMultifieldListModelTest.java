package org.ets.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class NestedMultifieldListModelTest {

    private final AemContext ctx = new AemContext();

    @Mock
    private ModelFactory modelFactory;

    @BeforeEach
    public void setUp() throws Exception {
        ctx.addModelsForClasses(NestedMultifieldListModel.class);
        ctx.load().json("/org/ets/core/models/NestedMultifieldResource.json", "/content/ets-org/language-master/en/home");
        ctx.registerService(ModelFactory.class, modelFactory, org.osgi.framework.Constants.SERVICE_RANKING,
                Integer.MAX_VALUE);
    }

    @Test
    void getNavDetailsWithCurrentPageTabLink1() {
        ctx.currentResource("/content/ets-org/language-master/en/home/test-page/jcr:content/horizontalnavigation");
        NestedMultifieldListModel nestedMultifieldListModel = ctx.request().adaptTo(NestedMultifieldListModel.class);
        assertNotNull(nestedMultifieldListModel.getSecondaryList());
        assertNotNull(nestedMultifieldListModel.getTertiaryList());
    }
}