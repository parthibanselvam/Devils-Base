package org.ets.core.services.impl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.ets.core.config.EPNApiConfiguration;
import org.ets.core.services.EPNContactAPIService;
import org.ets.core.utils.NetworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Component(service = EPNContactAPIService.class, immediate = true)
@Designate(ocd = EPNApiConfiguration.class)
public class EPNContactTokenServiceGeneratorImpl implements EPNContactAPIService{

    /* Logger */
    private static final Logger log = LoggerFactory.getLogger(EPNContactTokenServiceGeneratorImpl.class);

    private EPNApiConfiguration configuration;

    @Activate
    protected void activate(EPNApiConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getToken() {
        try {
        String url = configuration.getTokenApiEndpoint();
        String clientId = configuration.getClientId();
        String clientSecret = configuration.getSecret();
        String username = configuration.getUsername();
        String password = configuration.getPassword();
        String jsonString = NetworkUtil.getEPNToken(url, clientId, clientSecret, username, password);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject)jsonParser.parse(jsonString);
        return jsonObject.get("access_token").getAsString();

        }catch(Exception e) {
            log.error(e.getMessage(), e);
            return "Error occurred" + e.getMessage();
        }
    }

    /**
     * Overridden method of the HttpService
     */

    @Override
    public String getEPNContact(String token, SlingHttpServletRequest request) {
        try {
            /* Reading values from the configuration*/
            String url = configuration.getEpnEndpoint();
            return NetworkUtil.readEPNJson(url, token);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Error occurred" + e.getMessage();
        }
    }

}