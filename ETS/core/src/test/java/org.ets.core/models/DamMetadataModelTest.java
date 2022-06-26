package org.ets.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
class DamMetadataModelTest {

    private final AemContext ctx = new AemContext();

    @Mock
    private ModelFactory modelFactory;

    @Mock
    Resource mockResource;

    @BeforeEach
    public void setUp() throws Exception {
        ctx.addModelsForClasses(DamMetadataModel.class);
        ctx.load().json("/org/ets/core/models/DamMetadataModelResource.json", "/content");
        ctx.registerService(ModelFactory.class, modelFactory, org.osgi.framework.Constants.SERVICE_RANKING,
                Integer.MAX_VALUE);
    }

    @Test
    void testWithDescription() {
        String expected="Sample alt txt";
        String actual=null;
        ctx.request().setAttribute("imgPath", "/content/dam/sample-image.png");
        DamMetadataModel damMetadataModel = ctx.request().adaptTo(DamMetadataModel.class);
        actual= damMetadataModel.getAlttxt();
        assertEquals(actual,expected);
    }

    @Test
    void testWithoutDescription() {
        String actual=null;
        ctx.request().setAttribute("imgPath", "/content/dam/sample-image-without-alt.png");
        DamMetadataModel damMetadataModel = ctx.request().adaptTo(DamMetadataModel.class);
        actual= damMetadataModel.getAlttxt();
        assertNotNull(actual);
    }

    @Test
    void testWithInvalidResource() {
        String actual=null;
        ctx.request().setAttribute("imgPath", "/content/dam/sample-image-invalid-resource.png");
        DamMetadataModel damMetadataModel = ctx.request().adaptTo(DamMetadataModel.class);
        actual= damMetadataModel.getAlttxt();
        assertNotNull(actual);
    }

    @Test
    void testWithEmptyImagePath() {
        String actual=null;
        DamMetadataModel damMetadataModel = ctx.request().adaptTo(DamMetadataModel.class);
        actual= damMetadataModel.getAlttxt();
        assertNotNull(actual);
    }

}