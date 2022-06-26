package org.ets.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class LinkValidatorModelTest {

    private final AemContext aemContext = new AemContext();
    LinkValidatorModel linkValidatorModel;

    @BeforeEach
    void setUp() {
        aemContext.addModelsForClasses(LinkValidatorModel.class);
    }

    @Test
    void getLinkTestExternalLinkWithoutHTTP() {
        aemContext.request().setAttribute("link","www.google.com");
        linkValidatorModel=aemContext.request().adaptTo(LinkValidatorModel.class);
        assertEquals("https://www.google.com",linkValidatorModel.getLink());
    }

    @Test
    void getLinkTestExternalLinkWithHTTP() {
        aemContext.request().setAttribute("link","http://www.yahoo.com");
        linkValidatorModel=aemContext.request().adaptTo(LinkValidatorModel.class);
        assertEquals("http://www.yahoo.com",linkValidatorModel.getLink());
    }

    @Test
    void getLinkTestInternalLink() {
        aemContext.request().setAttribute("link","/content/en");
        linkValidatorModel=aemContext.request().adaptTo(LinkValidatorModel.class);
        assertEquals("/content/en.html",linkValidatorModel.getLink());
    }

}