package com.nike.ncp.common.mongo;

import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Query builder, AND connection
 */
public class CriteriaAndWrapper extends CriteriaWrapper {

    public CriteriaAndWrapper() {
        super(true, null, null);
    }

    public CriteriaAndWrapper(Integer current, Integer size) {
        super(true, current, size);
    }

    public CriteriaAndWrapper and(Criteria criteria) {
        getList().add(criteria);
        return this;
    }

    public CriteriaAndWrapper and(CriteriaWrapper criteriaWrapper) {
        getList().add(criteriaWrapper.build());
        return this;
    }

}
