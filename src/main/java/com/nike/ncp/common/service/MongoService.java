package com.nike.ncp.common.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.nike.ncp.common.configuration.Constant;
import com.nike.ncp.common.model.pagination.Page;
import com.nike.ncp.common.model.pagination.PageResp;
import com.nike.ncp.common.mongo.CriteriaAndWrapper;
import com.nike.ncp.common.mongo.CriteriaWrapper;
import com.nike.ncp.common.mongo.bean.CreateTime;
import com.nike.ncp.common.mongo.bean.SortBuilder;
import com.nike.ncp.common.mongo.bean.UpdateBuilder;
import com.nike.ncp.common.mongo.bean.UpdateTime;
import com.nike.ncp.common.mongo.reflection.SerializableFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.QueryMapper;
import org.springframework.data.mongodb.core.convert.UpdateMapper;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MongoService {

    private final MongoConverter mongoConverter;
    private QueryMapper queryMapper;
    private UpdateMapper updateMapper;

    @PostConstruct
    public void init() {
        queryMapper = new QueryMapper(mongoConverter);
        updateMapper = new UpdateMapper(mongoConverter);
    }

    private final MongoTemplate mongoTemplate;


    /**
     * insert or update
     *
     * @param object
     */
    private String insertOrUpdate(Object object, String collectionName) {

        Long time = System.currentTimeMillis();
        String id = (String) ReflectUtil.getFieldValue(object, Constant.ID);
        Object objectOrg = StrUtil.isNotEmpty(id) ? findById(id, object.getClass()) : null;

        if (objectOrg == null) {
            setCreateTime(object, time);
            setUpdateTime(object, time);
            ReflectUtil.setFieldValue(object, Constant.ID, null);
            if (StringUtils.isNotEmpty(collectionName)) {
                mongoTemplate.save(object, collectionName);
            } else {
                mongoTemplate.save(object);
            }
            id = (String) ReflectUtil.getFieldValue(object, Constant.ID);

        } else {
            Field[] fields = ReflectUtil.getFields(object.getClass());
            for (Field field : fields) {
                if (!field.getName().equals(Constant.ID) && ReflectUtil.getFieldValue(object, field) != null) {
                    ReflectUtil.setFieldValue(objectOrg, field, ReflectUtil.getFieldValue(object, field));
                }
            }
            setUpdateTime(objectOrg, time);
            if (StringUtils.isNotEmpty(collectionName)) {
                mongoTemplate.save(object, collectionName);
            } else {
                mongoTemplate.save(object);
            }
        }
        return id;
    }

    /**
     * insert
     *
     * @param object
     */
    public String insert(Object object) {
        ReflectUtil.setFieldValue(object, Constant.ID, null);
        insertOrUpdate(object, null);
        return (String) ReflectUtil.getFieldValue(object, Constant.ID);
    }

    /**
     * batch insert
     *
     * @param <T>
     * @param list
     */
    public <T> void insertAll(List<T> list) {
        construction(list);
        mongoTemplate.insertAll(list);
    }

    private <T> void construction(List<T> list) {
        Long time = System.currentTimeMillis();
        for (Object object : list) {
            ReflectUtil.setFieldValue(object, Constant.ID, null);
            setCreateTime(object, time);
            setUpdateTime(object, time);
        }
    }

    private void setUpdateTime(Object object, Long time) {
        Field[] fields = ReflectUtil.getFields(object.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(UpdateTime.class) && field.getType().equals(LocalDateTime.class)) {
                ReflectUtil.setFieldValue(object, field, time);
            }
        }
    }

    private void setCreateTime(Object object, Long time) {
        Field[] fields = ReflectUtil.getFields(object.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(CreateTime.class) && field.getType().equals(LocalDateTime.class)) {
                ReflectUtil.setFieldValue(object, field, time);
            }
        }
    }

    /**
     * update by id
     *
     * @param object
     */
    public void updateById(Object object) {
        if (StrUtil.isEmpty((String) ReflectUtil.getFieldValue(object, Constant.ID))) {
            return;
        }
        if (findById((String) ReflectUtil.getFieldValue(object, Constant.ID), object.getClass()) == null) {
            return;
        }
        insertOrUpdate(object, null);
    }

    /**
     * Update all fields according to id.
     *
     * @param object
     */
    public void updateAllColumnById(Object object) {

        if (StrUtil.isEmpty((String) ReflectUtil.getFieldValue(object, Constant.ID))) {
            return;
        }
        if (findById((String) ReflectUtil.getFieldValue(object, Constant.ID), object.getClass()) == null) {
            return;
        }
        Long time = System.currentTimeMillis();
        setUpdateTime(object, time);
        mongoTemplate.save(object);
    }

    /**
     * Update the first item found.
     *
     * @param criteriaWrapper
     * @param updateBuilder
     * @param clazz
     */
    public void updateFirst(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, Class<?> clazz) {
        Query query = new Query(criteriaWrapper.build());
        mongoTemplate.updateFirst(query, updateBuilder.toUpdate(), clazz);
    }

    /**
     * Update all items found.
     *
     * @param criteriaWrapper
     * @param updateBuilder
     * @param clazz
     */
    public void updateMulti(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, Class<?> clazz) {
        mongoTemplate.updateMulti(new Query(criteriaWrapper.build()), updateBuilder.toUpdate(), clazz);
    }

    /**
     * Delete by id
     *
     * @param id
     * @param clazz
     */
    public void deleteById(String id, Class<?> clazz) {
        if (StrUtil.isEmpty(id)) {
            return;
        }
        deleteByQuery(new CriteriaAndWrapper().eq(Constant::getId, id), clazz);
    }

    /**
     * batch delete by id
     *
     * @param ids
     * @param clazz
     */
    public void deleteByIds(List<String> ids, Class<?> clazz) {

        if (ids == null || ids.size() == 0) {
            return;
        }

        deleteByQuery(new CriteriaAndWrapper().in(Constant::getId, ids), clazz);
    }

    /**
     * Delete according to conditions
     *
     * @param criteriaWrapper
     * @param clazz
     */
    public void deleteByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz) {
        Query query = new Query(criteriaWrapper.build());
        mongoTemplate.remove(query, clazz);
    }

    /**
     * Accumulate the number of a certain field, atomic operation
     */
    public <R, E> void addCountById(String id, SerializableFunction<E, R> property, Number count, Class<?> clazz) {
        UpdateBuilder updateBuilder = new UpdateBuilder().inc(property, count);

        updateFirst(new CriteriaAndWrapper().eq(Constant::getId, id), updateBuilder, clazz);
    }

    /**
     * query by id
     *
     * @param id    id
     * @param clazz
     * @return T
     */
    public <T> T findById(String id, Class<T> clazz) {

        if (StrUtil.isEmpty(id)) {
            return null;
        }
        T t = (T) mongoTemplate.findById(id, clazz);
        return t;
    }

    /**
     * Find a single by criteria.
     *
     * @param <T>
     * @param criteriaWrapper
     * @param clazz
     * @return T
     */
    public <T> T findOneByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz) {
        SortBuilder sortBuilder = new SortBuilder(Constant::getId, Sort.Direction.DESC);
        return (T) findOneByQuery(criteriaWrapper, sortBuilder, clazz);
    }


    public <T> T findOneByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz) {

        Query query = new Query(criteriaWrapper.build());
        query.limit(1);
        query.with(sortBuilder.toSort());
        T t = (T) mongoTemplate.findOne(query, clazz);
        return t;

    }


    public <T> T findOneByQuery(SortBuilder sortBuilder, Class<T> clazz) {
        return (T) findOneByQuery(new CriteriaAndWrapper(), sortBuilder, clazz);
    }


    public <T> List<T> findListByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz) {
        SortBuilder sortBuilder = new SortBuilder().add(Constant::getId, Sort.Direction.DESC);
        return findListByQuery(criteriaWrapper, sortBuilder, clazz);

    }

    public <T> List<T> findListByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz) {
        Query query = new Query(criteriaWrapper.build());
        query.with(sortBuilder.toSort());
        List<T> list = mongoTemplate.find(query, clazz);
        return list;

    }


//    public List<String> findIdsByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz) {
//        return findPropertiesByQuery(criteriaWrapper, clazz, Constant::getId);
//    }

//    public <T, R, E> List<T> findPropertiesByQuery(CriteriaWrapper criteriaWrapper, Class<?> documentClass, SerializableFunction<E, R> property, Class<T> propertyClass) {
//        Query query = new Query(criteriaWrapper.build());
//        query.fields().include(ReflectionUtil.getFieldName(property));
//        List<?> list = mongoTemplate.find(query, documentClass);
//        List<T> propertyList = extractProperty(list, ReflectionUtil.getFieldName(property), propertyClass);
//        return propertyList;
//    }


//    public <R, E> List<String> findPropertiesByQuery(CriteriaWrapper criteriaWrapper, Class<?> documentClass, SerializableFunction<E, R> property) {
//        return findPropertiesByQuery(criteriaWrapper, documentClass, property, String.class);
//    }


    public <T> List<T> findListByIds(Collection<String> ids, Class<T> clazz) {
        CriteriaWrapper criteriaAndWrapper = new CriteriaAndWrapper().in(Constant::getId, ids);
        return findListByQuery(criteriaAndWrapper, clazz);
    }


    public <T> List<T> findListByIds(Collection<String> ids, SortBuilder sortBuilder, Class<T> clazz) {
        CriteriaWrapper criteriaAndWrapper = new CriteriaAndWrapper().in(Constant::getId, ids);
        return findListByQuery(criteriaAndWrapper, sortBuilder, clazz);
    }


    public <T> List<T> findListByIds(String[] ids, SortBuilder sortBuilder, Class<T> clazz) {
        return findListByIds(Arrays.asList(ids), sortBuilder, clazz);
    }


    public <T> List<T> findListByIds(String[] ids, Class<T> clazz) {
        SortBuilder sortBuilder = new SortBuilder(Constant::getId, Sort.Direction.DESC);
        return findListByIds(ids, sortBuilder, clazz);
    }


    public <T> List<T> findAll(Class<T> clazz) {
        SortBuilder sortBuilder = new SortBuilder(Constant::getId, Sort.Direction.DESC);
        return findListByQuery(new CriteriaAndWrapper(), sortBuilder, clazz);
    }


    public <T> List<T> findAll(SortBuilder sortBuilder, Class<T> clazz) {
        return findListByQuery(new CriteriaAndWrapper(), sortBuilder, clazz);
    }


//    public List<String> findAllIds(Class<?> clazz) {
//        return findIdsByQuery(new CriteriaAndWrapper(), clazz);
//    }

    public Long findCountByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz) {
        Long count = null;

        Query query = new Query(criteriaWrapper.build());
        if (query.getQueryObject().isEmpty()) {
            count = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz)).estimatedDocumentCount();
        } else {
            count = mongoTemplate.count(query, clazz);
        }

        return count;
    }


    public Long findAllCount(Class<?> clazz) {
        return findCountByQuery(new CriteriaAndWrapper(), clazz);
    }


    public <T> PageResp<T> findPage(PageQueryBuilder queryBuilder, Class<T> clazz) {

        PageResp<T> pageResp = new PageResp<T>();
        Page pageResult = Page.builder().build();
        pageResult.setCurrent(queryBuilder.getCurrent());
        pageResult.setSize(queryBuilder.getSize());
        calculatePages(queryBuilder, pageResult, clazz);

        Query query = queryBuilder.getQuery();
        query.skip((queryBuilder.getCurrent() - 1) * queryBuilder.getSize());
        query.limit(queryBuilder.getSize());
        List<T> list = mongoTemplate.find(query, clazz);
        pageResp.setPage(pageResult);
        pageResp.setData(list);
        return pageResp;
    }

    public <T> List<T> findListByCursorWithCondition(PageQueryBuilder queryBuilder, Class<T> clazz) {
        Query query = queryBuilder.getQuery();
        Document condition = query.getQueryObject();
        MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz));
        FindIterable<Document> iterable = collection.find(condition);
        if (Objects.nonNull(iterable)) {
            return getTs(clazz, iterable);
        }
        return new ArrayList<T>();
    }

//    private <T> List<T> extractProperty(List<?> list, String property, Class<T> clazz) {
//        Set<T> rs = new HashSet<T>();
//        for (Object object : list) {
//            Object value = ReflectUtil.getFieldValue(object, property);
//            if (value != null && value.getClass().equals(clazz)) {
//                rs.add((T) value);
//            }
//        }
//
//        return new ArrayList<T>(rs);
//    }

    private <T> List<T> getTs(Class<T> clazz, FindIterable<Document> iterable) {
        List<T> list = new ArrayList<>();
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()) {
            Object next = cursor.next();
            if (next instanceof Document) {
                Document document = (Document) next;
                document.putIfAbsent("id", document.get("_id"));
                T t = BeanUtil.copyProperties(document, clazz);
                list.add(t);
            }
        }
        return list;
    }

    public <T> List<T> findAllByCursor(Class<T> clazz) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz));
        FindIterable<Document> iterable = collection.find();
        if (Objects.nonNull(iterable)) {
            return getTs(clazz, iterable);
        }
        return new ArrayList<T>();
    }

    public <T> PageResp<T> findPageByCursor(PageQueryBuilder queryBuilder, String lastId, Class<T> clazz) {
        PageResp<T> pageResp = new PageResp<>();
        Page pageResult = Page.builder().build();
        pageResult.setCurrent(queryBuilder.getCurrent());
        pageResult.setSize(queryBuilder.getSize());
        Query query = queryBuilder.getQuery();
        Document condition = query.getQueryObject();
        MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz));
        calculatePages(collection, queryBuilder, condition, pageResult);
        FindIterable iterable = null;
        if (queryBuilder.getCurrent() == 1) {
            iterable = collection.find(condition).limit(query.getLimit());
        } else {
            if (lastId != null) {
                condition.append("_id", new Document("$gt", new ObjectId(lastId)));
                iterable = collection.find(condition).limit(query.getLimit());
            }
        }
        List<T> list = new ArrayList<>();
        if (Objects.nonNull(iterable)) {
            MongoCursor cursor = iterable.iterator();
            while (cursor.hasNext()) {
                Object next = cursor.next();
                if (next instanceof Document) {
                    Document t = (Document) next;
                    pageResult.setLastId(t.get("_id").toString());
                    t.putIfAbsent("id", t.get("_id"));
                    T t1 = BeanUtil.copyProperties(t, clazz);
                    list.add(t1);
                }
            }
        }
        pageResp.setPage(pageResult);
        pageResp.setData(list);
        return pageResp;
    }

    private void calculatePages(MongoCollection<Document> collection, PageQueryBuilder queryBuilder,
                                Document condition, Page pageResult) {
        int limit = queryBuilder.getQuery().getLimit();
        long count = collection.countDocuments(condition);
        if (count > 0) {
            if (count % limit == 0) {
                pageResult.setPages(count / limit);
            } else {
                pageResult.setPages(count / limit + 1);
            }
        } else {
            pageResult.setPages(0L);
        }
        pageResult.setTotal(count);
    }

    private void calculatePages(PageQueryBuilder queryBuilder, Page pageResult, Class<?> clazz) {
        Long count = null;

        Query countQuery = queryBuilder.getCountQuery();
        if (countQuery.getQueryObject().isEmpty()) {
            count = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz)).estimatedDocumentCount();
        } else {
            count = mongoTemplate.count(countQuery, clazz);
        }
        if (count > 0) {
            if (count % queryBuilder.getSize() == 0) {
                pageResult.setPages(count / queryBuilder.getSize());
            } else {
                pageResult.setPages(count / queryBuilder.getSize() + 1);
            }
        } else {
            pageResult.setPages(0L);
        }
        pageResult.setTotal(count);
    }
}
