package com.nike.ncp.common.service;

import com.nike.ncp.common.model.pagination.PageResp;
import com.nike.ncp.common.mongo.CriteriaWrapper;
import com.nike.ncp.common.mongo.bean.SortBuilder;
import com.nike.ncp.common.mongo.bean.UpdateBuilder;

import java.util.Collection;
import java.util.List;

public interface MongoService<T> {

    String insert(Object object);

    void insertAll(List<T> list);

    void batchInsert(List<T> list, Class<T> clazz);

    void updateById(Object object);

    void updateFirst(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, Class<?> clazz);

    void updateMulti(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, Class<?> clazz);

    void batchUpdate(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder,
                     List<T> list, Class<T> clazz);

    void deleteById(String id, Class<?> clazz);

    void deleteByIds(List<String> ids, Class<?> clazz);

    void deleteByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz);

    T findOneByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz);

    T findOneByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz);

    List<T> findListByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz);

    List<T> findListByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz);

    List<T> findListByIds(Collection<String> ids, Class<T> clazz);

    List<T> findListByIds(Collection<String> ids, SortBuilder sortBuilder, Class<T> clazz);

    List<T> findAll(Class<T> clazz);

    List<T> findAll(SortBuilder sortBuilder, Class<T> clazz);

    Long findCountByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz);

    Long findAllCount(Class<?> clazz);

    List<T> findListByCursorWithCondition(CriteriaWrapper criteriaWrapper, Class<T> clazz);

    List<T> findAllByCursor(Class<T> clazz);

    PageResp<T> findPage(CriteriaWrapper criteriaWrapper, Class<T> clazz);
    PageResp<T> findPage(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz);

    PageResp<T> findPageByCursor(CriteriaWrapper criteriaWrapper, String lastId, Class<T> clazz);

    PageResp<T> findPageByCursor(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, String lastId, Class<T> clazz);


}
