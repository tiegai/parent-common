package com.nike.ncp.common.utilities.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JsonServiceTest {

    private JsonService service = new JsonService(new ObjectMapper());

    @Test
    public void toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        assertEquals("{\"key\":\"value\"}", service.toJson(map));
    }

    @Test
    public void fromString() {
        Map map = service.fromString("{\"key\":\"value\"}", Map.class);
        assertEquals("value", map.get("key"));

        List<String> list = service.fromString("[\"value1\", \"value2\"]",
                new TypeReference<>() {});
        assertEquals(2, list.size());
        assertEquals("value2", list.get(1));
    }

    @Test(expected = RuntimeException.class)
    public void toStringException() throws JsonProcessingException {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        service = new JsonService(mapper);
        service.toJson(new Object());
    }

    @Test(expected = RuntimeException.class)
    public void fromStringException() throws JsonProcessingException {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.readValue(anyString(), any(Class.class))).thenThrow(JsonProcessingException.class);
        service = new JsonService(mapper);
        service.fromString("{}", Object.class);
    }

    @Test(expected = RuntimeException.class)
    public void fromStringTypeRefException() throws JsonProcessingException {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenThrow(JsonProcessingException.class);
        service = new JsonService(mapper);
        service.fromString("{}", new TypeReference<List<String>>() {});
    }
}
