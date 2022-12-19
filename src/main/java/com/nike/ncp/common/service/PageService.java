package com.nike.ncp.common.service;

import com.nike.ncp.common.model.pagination.Page;
import com.nike.ncp.common.model.pagination.PageResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PageService {

    private final MongoTemplate mongoTemplate;

    public <T> PageResp<T> findPage(PageQueryBuilder queryBuilder, Page page, Class<T> clazz) {
        PageResp<T> pageResp = new PageResp<>();
        Page pageResult = Page.builder().build();
        BeanUtils.copyProperties(page, pageResult);
        Long count = findCountByQuery(queryBuilder, clazz);
        if (count > 0) {
            if (count % page.getTotal() == 0) {
                pageResult.setPages(count / page.getTotal());
            } else {
                pageResult.setPages(count / page.getTotal() + 1);
            }
        } else {
            pageResult.setPages(0L);
        }
        pageResult.setTotal(count);

        Query query = queryBuilder.getQuery();
//        query.with(sortBuilder.toSort());
        query.skip((page.getCurrent() - 1) * page.getSize());
        query.limit(page.getSize());
        List<T> list = mongoTemplate.find(query, clazz);
        pageResp.setData(list);
        pageResp.setPage(pageResult);
        return pageResp;
    }

    public Long findCountByQuery(PageQueryBuilder queryBuilder, Class<?> clazz) {
        Long count;
        Query query = queryBuilder.getCountQuery();
        if (query.getQueryObject().isEmpty()) {
            count = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz)).estimatedDocumentCount();
        } else {
            count = mongoTemplate.count(query, clazz);
        }
        return count;
    }
}
