package com.nike.ncp.common.service;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.Objects;

@Getter
public final class PageQueryBuilder {

    public static final String ID = "_id";

    private Integer current;
    private Integer size;
    private Query query;
    private Query countQuery;

    private PageQueryBuilder(Query query, Query countQuery, Integer current, Integer size) {
        this.query = query;
        this.countQuery = countQuery;
        this.current = current;
        this.size = size;
    }

    public static PageQueryBuilder all() {
        return new PageQueryBuilder(new Query(), new Query(), 1, Integer.MAX_VALUE);
    }

    public static PageQueryBuilder page(int page, int size) {
        Query countQuery = new Query();
        countQuery.fields().include(ID);
        return new PageQueryBuilder(new Query().with(PageRequest.of(page - 1, size)), countQuery, page, size);
    }

    public PageQueryBuilder sort(String sort, String order) {
        if (StringUtils.isNotBlank(sort) && StringUtils.isNotBlank(order)) {
            Sort sortObject = Sort.by(Sort.Direction.fromString(order), sort);
            query.with(sortObject);
            countQuery.with(sortObject);
        }
        return this;
    }

    public PageQueryBuilder eq(String columnName, Object stringValue) {
        if (Objects.nonNull(stringValue)) {
            Criteria criteria = Criteria.where(columnName).is(stringValue);
            query.addCriteria(criteria);
            countQuery.addCriteria(criteria);
        }
        return this;
    }

    public PageQueryBuilder like(String columnName, Object stringValue) {
        if (Objects.nonNull(stringValue)) {
            Criteria criteria = Criteria.where(columnName).regex(".*" + stringValue + ".*", "i");
            query.addCriteria(criteria);
            countQuery.addCriteria(criteria);
        }
        return this;
    }

    public PageQueryBuilder gte(String columnName, Date dateValue) {
        if (dateValue != null) {
            Criteria criteria = Criteria.where(columnName).gte(dateValue);
            query.addCriteria(criteria);
            countQuery.addCriteria(criteria);
        }
        return this;
    }

    public PageQueryBuilder gt(String columnName, Date dateValue) {
        if (dateValue != null) {
            Criteria criteria = Criteria.where(columnName).gt(dateValue);
            query.addCriteria(criteria);
            countQuery.addCriteria(criteria);
        }
        return this;
    }

    public PageQueryBuilder lte(String columnName, Date dateValue) {
        if (dateValue != null) {
            Criteria criteria = Criteria.where(columnName).lte(dateValue);
            query.addCriteria(criteria);
            countQuery.addCriteria(criteria);
        }
        return this;
    }

    public PageQueryBuilder exclude(String... columnNames) {
        query.fields().exclude(columnNames);
        countQuery.fields().exclude(columnNames);
        return this;
    }

}
