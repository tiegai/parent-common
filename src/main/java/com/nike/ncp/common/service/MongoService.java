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
import org.springframework.beans.BeanUtils;
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

    public <T> PageResp<T> findPageByCursor(PageQueryBuilder queryBuilder, Page page, Class<T> clazz) {
        PageResp<T> pageResp = new PageResp<>();
        Page pageResult = Page.builder().build();
        pageResult.setCurrent(page.getCurrent());
        BeanUtils.copyProperties(page, pageResult);
        Query query = queryBuilder.getQuery();
        Document condition = query.getQueryObject();
        MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz));
        calculatePages(collection, queryBuilder, condition, pageResult);
        FindIterable iterable = null;
        if (page.getCurrent() == 1) {
            iterable = collection.find(condition).limit(query.getLimit());
        } else {
            if (page.getLastId() != null) {
                condition.append("_id", new Document("$gt", new ObjectId(page.getLastId())));
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
}
