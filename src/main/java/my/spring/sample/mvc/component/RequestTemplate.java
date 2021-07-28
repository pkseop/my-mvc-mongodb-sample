package my.spring.sample.mvc.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RequestTemplate {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.api.host}")
    private String externalApiHost;

    @Value("${external.api.username}")
    private String externalApiUsername;

    @Value("${external.api.password}")
    private String externalApiPassword;

    private String accessToken, refreshToken;

    private final static String JWT_EXPIRED_HEADER = "jwt-expired";


    private String toUrl(String uri) {
        return externalApiHost + uri;
    }

    private HttpEntity<Object> httpEntity(boolean withBearerToken, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(withBearerToken)
            headers.set("Authorization", "Bearer " + this.accessToken);

        return new HttpEntity<Object>(body, headers);
    }

    private HttpEntity<Object> httpEntityMultipart(boolean withBearerToken, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        if(withBearerToken)
            headers.set("Authorization", "Bearer " + this.accessToken);

        return new HttpEntity<Object>(body, headers);
    }

    private synchronized void login() throws Exception {
        Map<String, String> reqBody = Map.of("email", externalApiUsername, "password", externalApiPassword);
        ResponseEntity<String> responseEntity = restTemplate.exchange(toUrl("/login"), HttpMethod.POST, httpEntity(false, reqBody), String.class);
        if(responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Login to archisketch failed.");
        } else {
            String resBody = responseEntity.getBody();
            JSONObject jsonObj = new JSONObject(resBody);
            this.accessToken = jsonObj.getString("token");
            this.refreshToken = jsonObj.getString("refreshToken");
            log.info("================ Login succeeded! ================");
        }
    }

    private synchronized void refreshToken() throws Exception {
        Map<String, String> reqBody = Map.of("accessToken", this.accessToken, "refreshToken", this.refreshToken);
        ResponseEntity<String> responseEntity = restTemplate.exchange(toUrl("/refresh-token"), HttpMethod.POST, httpEntity(false, reqBody), String.class);
        if(responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Refresh token failed.");
        } else {
            String resBody = responseEntity.getBody();
            JSONObject jsonObj = new JSONObject(resBody);
            this.accessToken = jsonObj.getString("accessToken");
            this.refreshToken = jsonObj.getString("refreshToken");
            log.info("================ Token refreshed! ================");
        }
    }

    private boolean refreshToken(HttpHeaders headers) throws Exception {
        List<String> list = headers.get(JWT_EXPIRED_HEADER);
        if(list != null && list.isEmpty() == false) {
            refreshToken();
            return true;
        }
        return false;
    }

    public <T> T sendRequest(String uri, HttpMethod httpMethod, Object requestBody, Class<T> clazz) throws Exception {
        if(this.accessToken == null)
            login();

        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(toUrl(uri), httpMethod, httpEntity(true, requestBody), clazz);
            return responseEntity.getBody();
        } catch(HttpClientErrorException.Unauthorized ex) {
            ex.printStackTrace();
            if(refreshToken(ex.getResponseHeaders())) {
                ResponseEntity<T> responseEntity = restTemplate.exchange(toUrl(uri), httpMethod, httpEntity(true, requestBody), clazz);
                return responseEntity.getBody();
            } else {
                throw new Exception("Request failed: [" + ex.getResponseBodyAsString() + "]");
            }
        }
    }

    public <T> T sendRequest(String uri, HttpMethod httpMethod, Object requestBody, ParameterizedTypeReference<T> ptype) throws Exception {
        if(this.accessToken == null)
            login();

        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(toUrl(uri), httpMethod, httpEntity(true, requestBody), ptype);
            return responseEntity.getBody();
        } catch(HttpClientErrorException.Unauthorized ex) {
            ex.printStackTrace();
            if(refreshToken(ex.getResponseHeaders())) {
                ResponseEntity<T> responseEntity = restTemplate.exchange(toUrl(uri), httpMethod, httpEntity(true, requestBody), ptype);
                return responseEntity.getBody();
            } else {
                throw new Exception("Request failed: [" + ex.getResponseBodyAsString() + "]");
            }
        }
    }

    public <T> T sendMultiPartRequest(String uri, HttpMethod httpMethod, Object requestBody, ParameterizedTypeReference<T> ptype) throws Exception {
        if(this.accessToken == null)
            login();

        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(toUrl(uri), httpMethod, httpEntityMultipart(true, requestBody), ptype);
            return responseEntity.getBody();
        } catch(HttpClientErrorException.Unauthorized ex) {
            ex.printStackTrace();
            if(refreshToken(ex.getResponseHeaders())) {
                ResponseEntity<T> responseEntity = restTemplate.exchange(toUrl(uri), httpMethod, httpEntityMultipart(true, requestBody), ptype);
                return responseEntity.getBody();
            } else {
                throw new Exception("Request failed: [" + ex.getResponseBodyAsString() + "]");
            }
        }
    }
}
