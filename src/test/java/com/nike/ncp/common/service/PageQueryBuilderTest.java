package com.nike.ncp.common.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class PageQueryBuilderTest {

    public static final String COLUMN_NAME = "column";

    private PageQueryBuilder query;

    @Before
    public void setUp() {
        query = PageQueryBuilder.page(1, 10);
    }

    @Test
    public void all() {
        PageQueryBuilder allQuery = PageQueryBuilder.all();
        assertNotNull(allQuery.getQuery());
        assertNotNull(allQuery.getCountQuery());
    }

    @Test
    public void page() {
        assertNotNull(query.getCountQuery());
        assertNotNull(query.getCountQuery().getFieldsObject().get("_id"));
        assertNotNull(query.getQuery());
        assertEquals(10, query.getQuery().getLimit());
        assertEquals(0, query.getQuery().getSkip());
    }

    @Test
    public void sort() {
        query.sort(COLUMN_NAME, "DESC");
        assertNotNull(query.getCountQuery().getSortObject().get(COLUMN_NAME));
        assertNotNull(query.getQuery().getSortObject().get(COLUMN_NAME));
    }

    @Test
    public void sortWithNull() {
        query.sort(null, null);
        assertNull(query.getCountQuery().getSortObject().get(COLUMN_NAME));
        assertNull(query.getQuery().getSortObject().get(COLUMN_NAME));
    }

    @Test
    public void columnOperation() {
        query.eq("columnA", "value");
        query.eq("columnB", null);
        query.like("columnC", "value");
        query.like("columnD", null);
        query.gte("columnE", Date.from(Instant.now()));
        query.lte("columnF", Date.from(Instant.now()));
        assertNotNull(query.getCountQuery());
        assertNotNull(query.getQuery());
    }
}
