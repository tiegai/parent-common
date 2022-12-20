package com.nike.ncp.common.service;

import com.nike.ncp.common.model.pagination.Page;
import com.nike.ncp.common.model.pagination.PageResp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PageServiceTest {

    @Mock
    protected MongoTemplate mongoTemplate;
    private MongoService mongoService;
    private PageQueryBuilder query;

    @Before
    public void setUp() {
        query = PageQueryBuilder.page(1, 10);
        mongoService = new MongoService(mongoTemplate);

    }

    @Test(expected = RuntimeException.class)
    public void findPage() {
        PageQueryBuilder query = mock(PageQueryBuilder.class);
        Page page = mock(Page.class);
        when(mongoService.findPageByCursor(query,page,any(Class.class))).thenReturn(new PageResp());
    }

}
