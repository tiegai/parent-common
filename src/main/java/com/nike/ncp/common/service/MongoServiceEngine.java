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
import com.nike.ncp.common.mongo.bean.CreatedTime;
import com.nike.ncp.common.mongo.bean.SortBuilder;
import com.nike.ncp.common.mongo.bean.UpdateBuilder;
import com.nike.ncp.common.mongo.bean.UpdatedTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.QueryMapper;
import org.springframework.data.mongodb.core.convert.UpdateMapper;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MongoServiceEngine<T> implements MongoService<T> {

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
            setCreatedTime(object, time);
            setUpdatedTime(object, time);
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
            setUpdatedTime(objectOrg, time);
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

    @Override
    public String insert(Object object, String collectionName) {
        ReflectUtil.setFieldValue(object, Constant.ID, null);
        insertOrUpdate(object, collectionName);
        return (String) ReflectUtil.getFieldValue(object, Constant.ID);
    }

    /**
     * batch insert
     *
     * @param list
     */
    @Override
    public void insertAll(List<T> list) {
        construction(list);
        mongoTemplate.insertAll(list);
    }

    @Override
    public void batchInsert(List<T> list, Class<T> clazz) {
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, clazz);
        list.forEach(t -> {
            ops.insert(t);
        });
        ops.execute();
    }

    private <T> void construction(List<T> list) {
        Long time = System.currentTimeMillis();
        for (Object object : list) {
            ReflectUtil.setFieldValue(object, Constant.ID, null);
            setCreatedTime(object, time);
            setUpdatedTime(object, time);
        }
    }

    private void setUpdatedTime(Object object, Long time) {
        Field[] fields = ReflectUtil.getFields(object.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(UpdatedTime.class) && field.getType().equals(LocalDateTime.class)) {
                ReflectUtil.setFieldValue(object, field, time);
            }
        }
    }

    private void setCreatedTime(Object object, Long time) {
        Field[] fields = ReflectUtil.getFields(object.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(CreatedTime.class) && field.getType().equals(LocalDateTime.class)) {
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
        String id = (String) ReflectUtil.getFieldValue(object, Constant.ID);
        Assert.hasLength(id, "Id must not be empty.");
        Assert.notNull(findById((String) ReflectUtil.getFieldValue(object, Constant.ID), object.getClass()));
        insertOrUpdate(object, null);
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
        Assert.hasLength(id, "Id must not be empty.");
        deleteByQuery(new CriteriaAndWrapper().eq(Constant::getId, id), clazz);
    }

    /**
     * batch delete by id
     *
     * @param ids
     * @param clazz
     */
    public void deleteByIds(List<String> ids, Class<?> clazz) {
        Assert.notEmpty(ids, "collection must not be empty");
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
     * query by id
     *
     * @param id    id
     * @param clazz
     * @return T
     */
    public <T> T findById(String id, Class<T> clazz) {
        Assert.hasLength(id, "Id must not be empty");
        T t = (T) mongoTemplate.findById(id, clazz);
        return t;
    }

    /**
     * Find a single by criteria.
     *
     * @param criteriaWrapper
     * @param clazz
     * @return T
     */
    public T findOneByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz) {
        SortBuilder sortBuilder = new SortBuilder(Constant::getId, Sort.Direction.DESC);
        return (T) findOneByQuery(criteriaWrapper, sortBuilder, clazz);
    }

    /**
     * According to the query criteria and sorting rules, query a piece of data in pages.
     *
     * @param criteriaWrapper
     * @param sortBuilder
     * @param clazz
     * @return
     */
    public T findOneByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz) {

        Query query = new Query(criteriaWrapper.build());
        query.limit(1);
        query.with(sortBuilder.toSort());
        exclude(criteriaWrapper, query);
        T t = (T) mongoTemplate.findOne(query, clazz);
        return t;

    }

    private void exclude(CriteriaWrapper criteriaWrapper, Query query) {
        if (criteriaWrapper.getFields() != null) {
            query.fields().exclude(criteriaWrapper.getFields());
        }
    }


    /**
     * The query conditions are constructed by the condition constructor, and the set data of the query is returned.
     *
     * @param criteriaWrapper CriteriaWrapper is a conditional constructor. When constructing a condition,
     *                        it is necessary to create its subclass. CriteriaAndWrapper or CriteriaOrWrapper.
     *                        Eg:
     *                        CriteriaAndWrapper andWrapper = new CriteriaAndWrapper();
     *                        andWrapper.eq(propertiesName,propertiesValue).eq(propertiesName,propertiesValue);
     * @param clazz
     * @return
     */
    public List<T> findListByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz) {
        SortBuilder sortBuilder = new SortBuilder().add(Constant::getId, Sort.Direction.DESC);
        return findListByQuery(criteriaWrapper, sortBuilder, clazz);

    }

    /**
     * The query conditions are constructed by the condition constructor, and the sorting rules are constructed
     * by the sorting constructor, and the set data of the query is returned.
     *
     * @param criteriaWrapper CriteriaWrapper is a conditional constructor. When constructing a condition,
     *                        it is necessary to create its subclass. CriteriaAndWrapper or CriteriaOrWrapper.
     *                        Eg:
     *                        CriteriaAndWrapper andWrapper = new CriteriaAndWrapper();
     *                        andWrapper.eq(propertiesName,propertiesValue).eq(propertiesName,propertiesValue);
     * @param sortBuilder     sorting rules
     * @param clazz
     * @return
     */
    public List<T> findListByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz) {
        Query query = new Query(criteriaWrapper.build());
        query.with(sortBuilder.toSort());
        exclude(criteriaWrapper, query);
        List<T> list = mongoTemplate.find(query, clazz);
        return list;

    }

    /**
     * Query data by id collection,and sort desc by default
     *
     * @param ids   id collection
     * @param clazz Document object
     * @return
     */
    public List<T> findListByIds(Collection<String> ids, Class<T> clazz) {
        CriteriaWrapper criteriaAndWrapper = new CriteriaAndWrapper().in(Constant::getId, ids);
        return findListByQuery(criteriaAndWrapper, clazz);
    }

    /**
     * Query data by id collection,and set the sorting rules as required
     *
     * @param ids         id collection
     * @param sortBuilder sorting rules
     * @param clazz       Document object class
     * @return
     */
    public List<T> findListByIds(Collection<String> ids, SortBuilder sortBuilder, Class<T> clazz) {
        CriteriaWrapper criteriaAndWrapper = new CriteriaAndWrapper().in(Constant::getId, ids);
        return findListByQuery(criteriaAndWrapper, sortBuilder, clazz);
    }


    /**
     * Query all data,Sort by id by default
     *
     * @param clazz Document object class
     * @return
     */
    public List<T> findAll(Class<T> clazz) {
        SortBuilder sortBuilder = new SortBuilder(Constant::getId, Sort.Direction.DESC);
        return findListByQuery(new CriteriaAndWrapper(), sortBuilder, clazz);
    }


    /**
     * Custom collation queries all data.
     *
     * @param sortBuilder Conditional constructor
     * @param clazz
     * @return
     */
    public List<T> findAll(SortBuilder sortBuilder, Class<T> clazz) {
        return findListByQuery(new CriteriaAndWrapper(), sortBuilder, clazz);
    }

    /**
     * Total number for queried data by condition
     *
     * @param criteriaWrapper Conditional constructor
     * @param clazz
     * @return
     */
    public Long findCountByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz) {
        Long count;
        Query query = new Query(criteriaWrapper.build());
        exclude(criteriaWrapper, query);
        if (query.getQueryObject().isEmpty()) {
            count = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz)).estimatedDocumentCount();
        } else {
            count = mongoTemplate.count(query, clazz);
        }

        return count;
    }

    /**
     * Total number for queried data
     *
     * @param clazz Document object
     * @return
     */
    public Long findAllCount(Class<?> clazz) {
        return findCountByQuery(new CriteriaAndWrapper(), clazz);
    }


    /**
     * If you want to make paging query by skip, please call this method, but its performance is not as high as
     * that of getting data by cursor, but it can sort queries at will, which is its advantage.
     *
     * @param criteriaWrapper Conditions for querying data
     * @param clazz           Document object
     * @return PageResp, the returned response data model, has already processed all the data you need,
     * and you can use it directly without secondary encapsulation.
     */
    public PageResp<T> findPage(CriteriaWrapper criteriaWrapper, Class<T> clazz) {
        return findPage(criteriaWrapper, null, clazz);
    }


    @Override
    public PageResp<T> findPage(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz) {
        Assert.notNull(criteriaWrapper.getCurrent(), "Current value must not be null and must be greater than 0.");
        Assert.notNull(criteriaWrapper.getSize(), "Size value must not be null and must be greater than 0.");
        PageResp<T> pageResp = new PageResp<T>();
        Page pageResult = Page.builder().build();
        pageResult.setCurrent(criteriaWrapper.getCurrent());
        pageResult.setSize(criteriaWrapper.getSize());
        Query query = new Query(criteriaWrapper.build());
        calculatePages(criteriaWrapper, query, pageResult, clazz);
        if (sortBuilder != null) {
            query.with(sortBuilder.toSort());
        }
        query.skip((criteriaWrapper.getCurrent() - 1) * criteriaWrapper.getSize());
        query.limit(criteriaWrapper.getSize());
        exclude(criteriaWrapper, query);
        List<T> list = mongoTemplate.find(query, clazz);
        pageResp.setPage(pageResult);
        pageResp.setData(list);
        return pageResp;
    }

    /**
     * A cursor-based query that meets the conditions.
     *
     * @param criteriaWrapper Conditions for querying data
     * @param clazz           Document object
     * @return </> PageResp, the returned response data model, has already processed all the data you need,
     * and you can use it directly without secondary encapsulation.
     */
    public List<T> findListByCursorWithCondition(CriteriaWrapper criteriaWrapper, Class<T> clazz) {
        Query query = new Query(criteriaWrapper.build());
        exclude(criteriaWrapper, query);
        Document condition = query.getQueryObject();
        MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz));
        FindIterable<Document> iterable = collection.find(condition);
        if (Objects.nonNull(iterable)) {
            return getTs(clazz, iterable);
        }
        return new ArrayList<T>();
    }

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


    /**
     * Query all the data in the collection based on cursor. It is recommended to call this method if you want to query all data.
     *
     * @param clazz Document object
     * @return
     */
    public List<T> findAllByCursor(Class<T> clazz) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz));
        FindIterable<Document> iterable = collection.find();
        if (Objects.nonNull(iterable)) {
            return getTs(clazz, iterable);
        }
        return new ArrayList<T>();
    }

    /**
     * Paging query data based on cursor, but only sorting query according to id can be effective.
     * If data is sorted by other properties, paging effect cannot be guaranteed.
     *
     * @param criteriaWrapper Conditions for querying data
     * @param lastId          Get the id of the last piece of data last time. Every time you query, it will be included in the
     *                        page attribute lastId of the response data and returned to you.The first query can pass null,
     *                        and then each query must carry a return.
     * @param clazz           Document object
     * @return PageResp<T>. Model data
     */
    public PageResp<T> findPageByCursor(CriteriaWrapper criteriaWrapper, String lastId, Class<T> clazz) {
        return findPageByCursor(criteriaWrapper, null, lastId, clazz);
    }

    @Override
    public PageResp<T> findPageByCursor(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, String lastId, Class<T> clazz) {
        Assert.notNull(criteriaWrapper.getCurrent(), "Current value must not be null and must be greater than 0.");
        Assert.notNull(criteriaWrapper.getSize(), "Size value must not be null and must be greater than 0.");
        PageResp<T> pageResp = new PageResp<>();
        Page pageResult = Page.builder().build();
        pageResult.setCurrent(criteriaWrapper.getCurrent());
        pageResult.setSize(criteriaWrapper.getSize());
        Query query = new Query(criteriaWrapper.build());
        exclude(criteriaWrapper, query);
        if (sortBuilder != null) {
            query.with(sortBuilder.toSort());
        }
        Document condition = query.getQueryObject();
        MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz));
        calculatePages(collection, criteriaWrapper, condition, pageResult);
        FindIterable iterable;
        if (StringUtils.isNotBlank(lastId)) {
            condition.append("_id", new Document("$gt", new ObjectId(lastId)));
        }
        iterable = collection.find(condition).limit(criteriaWrapper.getSize());
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

    /**
     * Get the total number of transmitted condition through MongoCollection, and then calculate the total number of pages of query data.
     * This method mainly serves cursor query data.
     *
     * @param collection
     * @param criteriaWrapper
     * @param condition
     * @param pageResult
     */
    private void calculatePages(MongoCollection<Document> collection, CriteriaWrapper criteriaWrapper,
                                Document condition, Page pageResult) {
        int limit = criteriaWrapper.getSize();
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


    private void calculatePages(CriteriaWrapper criteriaWrapper, Query query, Page pageResult, Class<?> clazz) {
        Long count;
        if (query.getQueryObject().isEmpty()) {
            count = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz)).estimatedDocumentCount();
        } else {
            count = mongoTemplate.count(query, clazz);
        }
        if (count > 0) {
            Integer size = criteriaWrapper.getSize();
            if (count % size == 0) {
                pageResult.setPages(count / size);
            } else {
                pageResult.setPages(count / size + 1);
            }
        } else {
            pageResult.setPages(0L);
        }
        pageResult.setTotal(count);
    }
}
