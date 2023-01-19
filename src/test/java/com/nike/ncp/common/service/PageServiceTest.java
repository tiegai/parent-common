package com.nike.ncp.common.service;

import com.nike.ncp.common.model.pagination.Page;
import com.nike.ncp.common.model.pagination.PageResp;
import com.nike.ncp.common.mongo.CriteriaWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PageServiceTest {

    @Mock
    protected MongoTemplate mongoTemplate;
    @Mock
    private MongoConverter mongoConverter;

    private MongoService mongoService;

    @Before
    public void setUp() {
        mongoService = new MongoServiceEngine(mongoConverter, mongoTemplate);

    }

    @Test(expected = RuntimeException.class)
    public void findPage() {
        CriteriaWrapper query = mock(CriteriaWrapper.class);
        Page page = mock(Page.class);
        when(mongoService.findPageByCursor(query,any(String.class),any(Class.class))).thenReturn(new PageResp());
    }

}
