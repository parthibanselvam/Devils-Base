package org.ets.core.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This class has all the Network related calls
 */
public class NetworkUtil {
    private static final String BEARER = "Bearer ";
    private static final String SUCCESS= "success";
    private static final String FAILURE = "failure";
    private static final String STATUS = "status";
    private static final String DATA = "data";
    private static final String ERRORMESSAGE= "errorMessage";
    private static final String EMPTYRESULT = "No results found with search criteria, try again...";
    private static final String ERRORRESULT = "Something went wrong. Try again later…";
    private static final Logger log = LoggerFactory.getLogger(NetworkUtil.class);

    public static String getEPNToken(String url,String clientId,String clientSecret ,String username ,String password) {
        String jsonString = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
         try {
            HttpPost post = new HttpPost(url);
            String plainCredentials = username + ":" + password;
            String base64Credentials = new String(Base64.getEncoder().encode(plainCredentials.getBytes()));
            // Create authorization header
            String authorizationHeader = "Basic " + base64Credentials;
             // add request parameters or form parameters
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("grant_type", "password"));
            urlParameters.add(new BasicNameValuePair("client_id", clientId));
            urlParameters.add(new BasicNameValuePair("client_secret", clientSecret));
            urlParameters.add(new BasicNameValuePair("username", username));
            urlParameters.add(new BasicNameValuePair("password", password));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            post.addHeader("Authorization", authorizationHeader);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            CloseableHttpResponse response = httpClient.execute(post);
            jsonString = EntityUtils.toString(response.getEntity());
        } catch (ParseException | IOException e) {

                log.error("Exception in Network getSalseForceToken Method {}", e.getMessage());
            }
     finally {
         try {
             httpClient.close();
         } catch (final IOException e) {
             log.error("IOException in closing httpClient {}", e.getMessage());
         }
        }
        return jsonString;
    }

    public static String readEPNJson(String url, String token) {
        CloseableHttpClient httpClient = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(5000)
                    .build();
            final HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", BEARER+token);
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            final CloseableHttpResponse response = httpClient.execute(httpGet);
            /* Return the response */
            return IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
        }
        catch(Exception e) {
            log.error("Exception in read Json {}",e.getMessage());
        }
        finally {
            if(httpClient!=null) {
                 try {
                     httpClient.close();
                 } catch (final IOException e) {
                     log.error("IOException in closing httpClient {}", e.getMessage());
                 }
            }
        }
        return StringUtils.EMPTY;
    }

    public static String httpCall(HttpUriRequest httpUriRequest) {
        String jsonString = StringUtils.EMPTY;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JsonObject responseObject = new JsonObject();
        try {
            CloseableHttpResponse response = httpClient.execute(httpUriRequest);

            if(response.getStatusLine().getStatusCode() == 200){
                jsonString = EntityUtils.toString(response.getEntity());
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
                responseObject.addProperty(STATUS, SUCCESS);
                responseObject.add(DATA, jsonArray);
                responseObject.addProperty(ERRORMESSAGE, EMPTYRESULT);
                String jsonStringMsg = responseObject.get(DATA).toString();
                return responseObject.toString();
            }
            else if(response.getStatusLine().getStatusCode() >= 400 &&
                        response.getStatusLine().getStatusCode() <= 499)
            {
                jsonString = EntityUtils.toString(response.getEntity());
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
                responseObject.addProperty(STATUS, FAILURE);
                responseObject.add(DATA, jsonArray);
                responseObject.addProperty(ERRORMESSAGE, ERRORRESULT);
                return responseObject.toString();
            }
        }
        catch (Exception e) {
            log.error("Exception in httpCall Method {}", e.getMessage());
        }
        finally {
            try {
                httpClient.close();
            } catch (final IOException e) {
                log.error("Exception in closing httpClient {}", e.getMessage());
            }
        }
        return jsonString;
    }
}