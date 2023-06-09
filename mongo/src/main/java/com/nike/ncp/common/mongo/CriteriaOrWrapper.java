package com.nike.ncp.common.mongo;

import org.springframework.data.mongodb.core.query.Criteria;


/**
 * Query builder, OR connection
 */
public class CriteriaOrWrapper extends CriteriaWrapper {

    public CriteriaOrWrapper() {
        super(false, null, null);
    }

    public CriteriaOrWrapper(Integer current, Integer size) {
        super(false, current, size);
    }

    public CriteriaOrWrapper or(Criteria criteria) {
        getList().add(criteria);
        return this;
    }

    public CriteriaOrWrapper or(CriteriaWrapper criteriaWrapper) {
        getList().add(criteriaWrapper.build());
        return this;
    }

}
