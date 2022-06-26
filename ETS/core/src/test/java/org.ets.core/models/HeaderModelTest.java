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
class HeaderModelTest {

    private AemContext context = new AemContext();

    @Mock
    private ModelFactory modelFactory;

    @BeforeEach
    public void setUp() throws Exception {
        context.addModelsForClasses(HeaderModel.class);
        context.load().json("/org/ets/core/models/HeaderModelResource.json", "/content");
        context.registerService(ModelFactory.class, modelFactory, org.osgi.framework.Constants.SERVICE_RANKING,
                Integer.MAX_VALUE);
    }

    @Test
    void currentPageIsHome() {
        context.currentResource("/content/home/jcr:content/header");
        HeaderModel headerModel = context.request().adaptTo(HeaderModel.class);
        assertNotNull(headerModel);
        assertNotNull(headerModel.getLangMap());
        assertNotNull(headerModel.getCfMap());
        assertNotNull(headerModel.getCfLists());
    }

    @Test
    void currentPageIsNotHome() {
        context.currentResource("/content/praxis/jcr:content/header");
        HeaderModel headerModel = context.request().adaptTo(HeaderModel.class);
        assertNotNull(headerModel);
    }
}