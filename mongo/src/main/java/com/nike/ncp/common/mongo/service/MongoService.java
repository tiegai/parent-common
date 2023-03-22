package com.nike.ncp.common.mongo.service;

import com.nike.ncp.common.mongo.CriteriaWrapper;
import com.nike.ncp.common.mongo.bean.SortBuilder;
import com.nike.ncp.common.mongo.bean.UpdateBuilder;
import com.nike.ncp.common.mongo.pagination.PageResp;

import java.util.Collection;
import java.util.List;

public interface MongoService {

    String insert(Object object);

    String insert(Object object, String collectionName);

    <T> void insertAll(List<T> list);

    <T> void batchInsert(List<T> list, Class<T> clazz);

    void updateById(Object object);

    void updateById(Object object, String collectionName);

    long updateFirst(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, Class<?> clazz);

    long updateFirst(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, String collectionName);

    long updateMulti(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, Class<?> clazz);

    long updateMulti(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, String collectionName);

    long deleteById(String id, Class<?> clazz);

    long deleteById(String id, String collectionName);

    long deleteByIds(List<String> ids, Class<?> clazz);

    long deleteByIds(List<String> ids, String collectionName);

    long deleteByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz);

    long deleteByQuery(CriteriaWrapper criteriaWrapper, String collectionName);

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
