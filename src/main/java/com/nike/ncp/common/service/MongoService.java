package com.nike.ncp.common.service;

import cn.hutool.core.bean.BeanUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.nike.ncp.common.model.pagination.Page;
import com.nike.ncp.common.model.pagination.PageResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MongoService {

    private final MongoTemplate mongoTemplate;

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
