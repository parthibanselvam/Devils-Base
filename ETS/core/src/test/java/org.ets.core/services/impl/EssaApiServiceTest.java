package org.ets.core.services.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;

import javax.servlet.http.Cookie;

import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.ets.core.config.EssaApiConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.testing.mock.aem.junit5.AemContext;

class EssaApiServiceTest {

        AemContext aemContext = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);
        EssaApiServiceImpl essaApiService;

        @BeforeEach
        public void setup() {
            essaApiService = aemContext.registerService(new EssaApiServiceImpl());
            EssaApiConfiguration config = mock(EssaApiConfiguration.class);
            when(config.getUsername()).thenReturn("username");
            when(config.getPassword()).thenReturn("password");
            when(config.getTokenApiEndpoint()).thenReturn("https://test.sample");
            when(config.getLocationAvailabilityEndpoint()).thenReturn("http://test.sample/location-availability-endpoint");
            when(config.getSeatsAvailabilityEndpoint()).thenReturn("http://test.sample/seats-availability-endpoint");
            essaApiService.activate(config);
        }

        @Test
        void testCookieMethod() throws FileNotFoundException {
            MockSlingHttpServletRequest mockSlingRequest = aemContext.request();
            MockSlingHttpServletResponse mockSlingResponse = aemContext.response();
            Cookie setTokenCookie = essaApiService.setTokenCookie(mockSlingRequest,mockSlingResponse);
            essaApiService.getAvailableLocation("tokenString", mockSlingRequest);
            essaApiService.getAvailableSeats("tokenString", mockSlingRequest);
            assertNotNull(setTokenCookie);
        }
}