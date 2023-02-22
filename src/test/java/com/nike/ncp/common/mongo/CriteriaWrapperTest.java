package com.nike.ncp.common.mongo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.annotation.processing.SupportedAnnotationTypes;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CriteriaWrapperTest {

    @Mock
    CriteriaWrapper criteriaWrapper;

    @Mock
    Criteria criteria;

    CriteriaAndWrapper criteriaAndWrapper = new CriteriaAndWrapper(1,10);

    CriteriaOrWrapper criteriaOrWrapper = new CriteriaOrWrapper(1,10);


    @Test
    public void and(){
        assertEquals(criteriaAndWrapper,criteriaAndWrapper.and(new CriteriaOrWrapper()));
        assertEquals(criteriaAndWrapper,criteriaAndWrapper.and(new Criteria()));
    }

    @Test
    public void or(){
        assertEquals(criteriaOrWrapper,criteriaOrWrapper.or(new CriteriaOrWrapper()));
        assertEquals(criteriaOrWrapper,criteriaOrWrapper.or(new Criteria()));
        criteriaOrWrapper.build();
    }

    @Test
    public void like(){
        assertEquals(criteriaOrWrapper,criteriaOrWrapper.like("name","jack chen"));
        assertEquals(criteriaOrWrapper,criteriaOrWrapper.like("name",""));
    }

    @Test
    public void eq(){
        assertEquals(criteriaOrWrapper,criteriaOrWrapper.eq("name","jack chen"));
    }

    @Test
    public void containOr(){
        assertEquals(criteriaOrWrapper,criteriaOrWrapper.containOr("name", Collections.emptyList()));
    }

}
