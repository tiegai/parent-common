package com.nike.ncp.common.redis.properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class RedisClusterPropertiesTest {

//    @Mock
    private RedisClusterProperties redisClusterProperties;

    @Before
    public void setUp(){
      redisClusterProperties =new RedisClusterProperties();
    }

    @Test
    public void getNodes() {
        redisClusterProperties.setNodes(new ArrayList<>());
        assertEquals(redisClusterProperties.getNodes(),new ArrayList<>());
    }

}
