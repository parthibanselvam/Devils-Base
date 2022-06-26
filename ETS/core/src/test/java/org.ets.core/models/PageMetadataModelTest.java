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
class PageMetadataModelTest {

    private AemContext context = new AemContext();

    @Mock
    private ModelFactory modelFactory;

    @BeforeEach
    public void setUp() throws Exception {
        context.addModelsForClasses(PageMetadataModel.class);
        context.load().json("/org/ets/core/models/PageMetadataModelResource.json", "/content");
        context.registerService(ModelFactory.class, modelFactory, org.osgi.framework.Constants.SERVICE_RANKING,Integer.MAX_VALUE);
    }

    @Test
    void testGetMultifield() {
        context.currentResource("/content/product-page/jcr:content");
        PageMetadataModel pageMetadataModel = context.request().adaptTo(PageMetadataModel.class);
        assertNotNull(pageMetadataModel);
    }

}