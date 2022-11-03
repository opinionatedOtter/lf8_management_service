package de.szut.lf8_project.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.repository.EmployeeRestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

public class TestWithRealKeycloak {
    private static boolean setUpIsDone = false;

    @Autowired
    private Environment env;
    private JWT jwt;

    @BeforeEach
    public void setUpJwt() throws JsonProcessingException {
        if (setUpIsDone) return;
        this.jwt = getFreshJwt();
        setUpIsDone = true;
    }

    private JWT getFreshJwt() throws JsonProcessingException {
        String jsonString = new RestTemplate()
                .postForEntity(
                        Objects.requireNonNull(env.getProperty("authProvider.url")),
                        getPostRequestBody(),
                        String.class)
                .getBody();
        Map<String, String> map = new ObjectMapper().readValue(jsonString, Map.class);
        return new JWT("Bearer " + map.get("access_token"));
    }

    private HttpEntity<MultiValueMap<String, String>> getPostRequestBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> bodyParamMap = new LinkedMultiValueMap<>();
        bodyParamMap.add("grant_type", "password");
        bodyParamMap.add("client_id", env.getProperty("authProvider.client_id"));
        bodyParamMap.add("username", env.getProperty("authProvider.user"));
        bodyParamMap.add("password", env.getProperty("authProvider.password"));
        return new HttpEntity<>(bodyParamMap, headers);
    }

}
