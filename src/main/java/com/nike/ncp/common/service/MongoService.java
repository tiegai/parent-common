package com.nike.ncp.common.service;

import com.nike.ncp.common.model.pagination.PageResp;
import com.nike.ncp.common.mongo.CriteriaWrapper;
import com.nike.ncp.common.mongo.bean.SortBuilder;
import com.nike.ncp.common.mongo.bean.UpdateBuilder;

import java.util.Collection;
import java.util.List;

public interface MongoService {

    String insert(Object object);

    String insert(Object object, String collectionName);

    <T> void insertAll(List<T> list);

    <T> void batchInsert(List<T> list, Class<T> clazz);

    void updateById(Object object);

    void updateById(Object object, String collectionName);

    void updateFirst(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, Class<?> clazz);

    void updateFirst(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, String collectionName);

    void updateMulti(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, Class<?> clazz);

    void updateMulti(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, String collectionName);

    void deleteById(String id, Class<?> clazz);

    void deleteById(String id, String collectionName);

    void deleteByIds(List<String> ids, Class<?> clazz);

    void deleteByIds(List<String> ids, String collectionName);

    void deleteByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz);

    void deleteByQuery(CriteriaWrapper criteriaWrapper, String collectionName);

    <T> T findById(String id, Class<T> clazz);

    <T> T findById(String id, Class<T> clazz, String collectionName);

    <T> T findOneByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz);

    <T> T findOneByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz, String collectionName);

    <T> T findOneByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz);

    <T> T findOneByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz,
                         String collectionName);

    <T> List<T> findListByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz);

    <T> List<T> findListByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz, String collectionName);

    <T> List<T> findListByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz);

    <T> List<T> findListByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz,
                                String collectionName);

    <T> List<T> findListByIds(Collection<String> ids, Class<T> clazz);

    <T> List<T> findListByIds(Collection<String> ids, Class<T> clazz, String collectionName);

    <T> List<T> findListByIds(Collection<String> ids, SortBuilder sortBuilder, Class<T> clazz);

    <T> List<T> findListByIds(Collection<String> ids, SortBuilder sortBuilder, Class<T> clazz, String collectionName);

    <T> List<T> findAll(Class<T> clazz);

    <T> List<T> findAll(SortBuilder sortBuilder, Class<T> clazz);

    Long findCountByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz);

    Long findCountByQuery(CriteriaWrapper criteriaWrapper, String collectionName);

    Long findAllCount(Class<?> clazz);

    Long findAllCount(String collectionName);

    <T> List<T> findListByCursorWithCondition(CriteriaWrapper criteriaWrapper, Class<T> clazz);

    <T> List<T> findListByCursorWithCondition(CriteriaWrapper criteriaWrapper, Class<T> clazz, String collectionName);

    <T> List<T> findAllByCursor(Class<T> clazz);

    <T> PageResp<T> findPage(CriteriaWrapper criteriaWrapper, Class<T> clazz);

    <T> PageResp<T> findPage(CriteriaWrapper criteriaWrapper, Class<T> clazz, String collectionName);

    <T> PageResp<T> findPage(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz);

    <T> PageResp<T> findPage(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz,
                             String collectionName);

    <T> PageResp<T> findPageByCursor(CriteriaWrapper criteriaWrapper, String lastId, Class<T> clazz);

    <T> PageResp<T> findPageByCursor(CriteriaWrapper criteriaWrapper, String lastId, Class<T> clazz,
                                     String collectionName);
}
