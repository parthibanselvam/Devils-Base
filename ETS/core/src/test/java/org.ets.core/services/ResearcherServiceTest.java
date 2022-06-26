package org.ets.core.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.ets.core.services.impl.ResearcherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class,MockitoExtension.class })
class ResearcherServiceTest {

        public AemContext aemContext = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

        private ResearcherServiceImpl researcherService = new ResearcherServiceImpl();
        private ResourceResolver resourceResolver;
        private String damPath;

        @BeforeEach
        public void setup() {
            aemContext.load().json("/org/ets/core/service/ResearcherServiceResource.json", "/content");
            resourceResolver = aemContext.resourceResolver();
            damPath ="/content/dam/ets-sample-record.xml";            
            aemContext.registerService(researcherService);
        }

        @Test
        void testPatentPage() throws FileNotFoundException {
            InputStream xmlAsset = researcherService.getXmlAsset(resourceResolver, damPath);
            assertNull(xmlAsset);
            File file = new File("src/test/resources/org/ets/core/service/ETS-Patent-Records.xml");
            FileInputStream input = new FileInputStream(file);
            String status=researcherService.convertXmlToPages(resourceResolver, input);
            assertNotNull(status);
        }

        @Test
        void testAuthorRecordPage() throws FileNotFoundException {
            File file = new File("src/test/resources/org/ets/core/service/Export-ETS-Authors-Records.xml");
            FileInputStream input = new FileInputStream(file);
            String status=researcherService.convertXmlToPages(resourceResolver, input);
            assertNotNull(status);
        }
}