package com.nike.ncp.common.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestServiceTest {

    private RestService service;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ResponseEntity responseEntity;

    @Before
    public void setUp() {
        service = new RestService(restTemplate);
    }

    @Test
    public void get() {
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(responseEntity.getBody()).thenReturn("ok");
        assertEquals("ok", service.get("url", String.class));
        assertEquals("ok", service.getBearerToken("url", "token", String.class));
    }

    @Test
    public void post() {
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(responseEntity.getBody()).thenReturn("ok");
        assertEquals("ok", service.post("url", "{}", String.class));
        assertEquals("ok", service.postBearerToken("url", "token", "{}", String.class));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        assertEquals("ok", service.post("url", "{}", headers, String.class));
    }

    @Test(expected = RuntimeException.class)
    public void postSocketTimeout() {
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenThrow(RestClientException.class);
        service.post("url", "{}", String.class);
    }

    @Test(expected = RuntimeException.class)
    public void getNotOkResponse() {
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        service.get("url", String.class);
    }
}
