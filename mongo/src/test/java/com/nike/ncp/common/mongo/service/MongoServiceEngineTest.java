package com.nike.ncp.common.mongo.service;

import cn.hutool.core.util.ReflectUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.nike.ncp.common.model.journey.Audience;
import com.nike.ncp.common.mongo.CriteriaAndWrapper;
import com.nike.ncp.common.mongo.bean.SortBuilder;
import com.nike.ncp.common.mongo.bean.UpdateBuilder;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class MongoServiceEngineTest {

    @Mock
    protected MongoTemplate mongoTemplate;
    @Mock
    MongoCollection<Document> collection;
    @Mock
    DeleteResult deleteResult;
    @Mock
    FindIterable<Document> iterable;
    @Mock
    MongoCursor<Document> mongoCursor;

    private MongoService mongoService;

    MockedStatic<ReflectUtil> reflectUtil;
    Audience audience = new Audience();

    private String collectionName = "audience";

    @Before
    public void setUp() {
        reflectUtil = Mockito.mockStatic(ReflectUtil.class);
        mongoService = new MongoServiceEngine(mongoTemplate);
        audience.setId("1");
        audience.setNuid("123132");
        audience.setNextActivityId("63c3d3ff323e5300970a76fe");
        audience.setUpmid("swedf34235623");
    }

    @After
    public void after() {
        reflectUtil.close();
    }

    @Test
    public void insertOrUpdate() {
        reflectUtil.when(() -> ReflectUtil.getFieldValue(audience, "id")).thenReturn("1");
        reflectUtil.when(()->ReflectUtil.getFields(ArgumentMatchers.any(Class.class))).thenReturn(new Field[]{});
        Mockito.when(mongoTemplate.save(audience)).thenReturn(audience);
        assertEquals("1", mongoService.insert(audience));
    }

    @Test
    public void insertAll() {
        mongoService.insertAll(new ArrayList<>());
        Mockito.verify(mongoTemplate, Mockito.times(1)).insertAll(ArgumentMatchers.any(List.class));
    }


    @Test
    public void update() {
        reflectUtil.when(() -> ReflectUtil.getFields(ArgumentMatchers.any(Class.class))).thenReturn(new Field[]{});
        Mockito.when(mongoTemplate.findById(ArgumentMatchers.anyString(), ArgumentMatchers.any(Class.class))).thenReturn(audience);
        reflectUtil.when(() -> ReflectUtil.getFieldValue(audience, "id")).thenReturn("1");
        mongoService.updateById(audience);
        mongoService.updateById(audience, collectionName);
        mongoService.updateFirst(new CriteriaAndWrapper(), new UpdateBuilder(), Audience.class);
        mongoService.updateFirst(new CriteriaAndWrapper(), new UpdateBuilder(), collectionName);
        mongoService.updateMulti(new CriteriaAndWrapper(), new UpdateBuilder(), Audience.class);
        mongoService.updateMulti(new CriteriaAndWrapper(), new UpdateBuilder(), collectionName);
    }

    @Test
    public void delete() {
        Mockito.when(mongoTemplate.remove(ArgumentMatchers.any(Query.class), ArgumentMatchers.anyString())).thenReturn(deleteResult);
        Mockito.when(mongoTemplate.remove(ArgumentMatchers.any(Query.class), ArgumentMatchers.any(Class.class))).thenReturn(deleteResult);
        List<String> list = new ArrayList<>();
        list.add("1");
        mongoService.deleteByIds(list, Audience.class);
        Mockito.verify(mongoTemplate, Mockito.times(1)).remove(ArgumentMatchers.any(Query.class), ArgumentMatchers.any(Class.class));
        mongoService.deleteByQuery(new CriteriaAndWrapper(), collectionName);
        Mockito.verify(mongoTemplate, Mockito.times(1)).remove(ArgumentMatchers.any(Query.class), ArgumentMatchers.anyString());
        mongoService.deleteByIds(list, collectionName);
        mongoService.deleteById("1", collectionName);
        mongoService.deleteById("2", Audience.class);
        mongoService.deleteByQuery(new CriteriaAndWrapper(), Audience.class);
    }

    @Test
    public void find() {
        Mockito.when(mongoTemplate.findOne(ArgumentMatchers.any(Query.class), ArgumentMatchers.any(Class.class))).thenReturn(audience);
        Mockito.when(mongoTemplate.findOne(ArgumentMatchers.any(Query.class), ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString())).thenReturn(audience);
        Mockito.when(mongoTemplate.findById(ArgumentMatchers.anyString(), ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString())).thenReturn(audience);
        CriteriaAndWrapper wrapper = new CriteriaAndWrapper();
        wrapper.exclude("name");
        assertEquals(audience,mongoService.findById("1", Audience.class, collectionName));
        assertEquals(audience,mongoService.findOneByQuery(wrapper, Audience.class));
        assertEquals(audience,mongoService.findOneByQuery(wrapper, Audience.class, collectionName));
        assertEquals(audience,mongoService.findOneByQuery(wrapper, new SortBuilder(), Audience.class));
        assertEquals(audience,mongoService.findOneByQuery(wrapper, new SortBuilder(), Audience.class, collectionName));
    }

    @Test
    public void findList() {
        Mockito.when(mongoTemplate.find(ArgumentMatchers.any(Query.class), ArgumentMatchers.any(Class.class))).thenReturn(new ArrayList());
        Mockito.when(mongoTemplate.find(ArgumentMatchers.any(Query.class), ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString())).thenReturn(new ArrayList());
        CriteriaAndWrapper wrapper = new CriteriaAndWrapper();
        SortBuilder sortBuilder = new SortBuilder();
        assertEquals(Collections.emptyList(),mongoService.findListByQuery(wrapper, Audience.class));
        assertEquals(Collections.emptyList(),mongoService.findListByQuery(wrapper, Audience.class, collectionName));
        assertEquals(Collections.emptyList(),mongoService.findListByQuery(wrapper, sortBuilder, Audience.class));
        assertEquals(Collections.emptyList(),mongoService.findListByQuery(wrapper, sortBuilder, Audience.class, collectionName));
    }

    @Test
    public void findListByIds() {
        Mockito.when(mongoTemplate.find(ArgumentMatchers.any(Query.class), ArgumentMatchers.any(Class.class))).thenReturn(new ArrayList());
        Mockito.when(mongoTemplate.find(ArgumentMatchers.any(Query.class), ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString())).thenReturn(new ArrayList());
        SortBuilder sortBuilder = new SortBuilder();
        List<String> list = new ArrayList<>();
        assertEquals(Collections.emptyList(), mongoService.findListByIds(list, Audience.class));
        assertEquals(Collections.emptyList(), mongoService.findListByIds(list, Audience.class, collectionName));
        assertEquals(Collections.emptyList(), mongoService.findListByIds(list, sortBuilder, Audience.class));
        assertEquals(Collections.emptyList(), mongoService.findListByIds(list, sortBuilder, Audience.class,
                collectionName));
    }

    @Test
    public void findAll() {
        Mockito.when(mongoTemplate.find(ArgumentMatchers.any(Query.class), ArgumentMatchers.any(Class.class))).thenReturn(new ArrayList());
        Class<Audience> audienceClass = Audience.class;
        assertEquals(Collections.emptyList(), mongoService.findAll(audienceClass));
        assertEquals(Collections.emptyList(), mongoService.findAll(new SortBuilder(), audienceClass));
    }

    @Test
    public void findCount() {
        Mockito.when(mongoTemplate.getCollectionName(ArgumentMatchers.any(Class.class))).thenReturn(collectionName);
        Mockito.when(mongoTemplate.getCollection(ArgumentMatchers.anyString())).thenReturn(collection);
        Mockito.when(mongoTemplate.count(ArgumentMatchers.any(Query.class), ArgumentMatchers.anyString())).thenReturn(2L);
        Mockito.when(collection.estimatedDocumentCount()).thenReturn(3L);
        assertEquals(Optional.of(2L).get(), mongoService.findCountByQuery(new CriteriaAndWrapper(),collectionName));
        assertEquals(Optional.of(3L).get(), mongoService.findAllCount(Audience.class));
        assertEquals(Optional.of(2L).get(), mongoService.findAllCount(collectionName));
    }

    @Test
    public void findPage() {
        Mockito.when(mongoTemplate.getCollectionName(ArgumentMatchers.any(Class.class))).thenReturn(collectionName);
        Mockito.when(mongoTemplate.getCollection(ArgumentMatchers.anyString())).thenReturn(collection);
        Mockito.when(collection.estimatedDocumentCount()).thenReturn(100L);
        Mockito.when(mongoTemplate.find(ArgumentMatchers.any(Query.class), ArgumentMatchers.any(Class.class))).thenReturn(new ArrayList());
        assertEquals(0,mongoService.findPage(new CriteriaAndWrapper(1,10),Audience.class).getData().size());
        assertEquals(0,mongoService.findPage(new CriteriaAndWrapper(1,10),Audience.class,collectionName).getData().size());
        assertEquals(0,
                mongoService.findPage(new CriteriaAndWrapper(1,10),new SortBuilder(),Audience.class).getData().size());
        assertEquals(0,mongoService.findPage(new CriteriaAndWrapper(1,10),new SortBuilder(),Audience.class,
                collectionName).getData().size());
    }

    @Test
    public void findListByCursor(){
        Mockito.when(mongoTemplate.getCollectionName(ArgumentMatchers.any(Class.class))).thenReturn(collectionName);
        Mockito.when(mongoTemplate.getCollection(ArgumentMatchers.anyString())).thenReturn(collection);
        Mockito.when(collection.find(ArgumentMatchers.any(Bson.class))).thenReturn(iterable);
        Mockito.when(iterable.iterator()).thenReturn(mongoCursor);
        assertEquals(Collections.emptyList(),mongoService.findListByCursorWithCondition(new CriteriaAndWrapper(),
                Audience.class));
        assertEquals(Collections.emptyList(),mongoService.findListByCursorWithCondition(new CriteriaAndWrapper(),
                Audience.class,collectionName));
    }

    @Test
    public void findListByCursorBranch(){
        Mockito.when(mongoTemplate.getCollectionName(ArgumentMatchers.any(Class.class))).thenReturn(collectionName);
        Mockito.when(mongoTemplate.getCollection(ArgumentMatchers.anyString())).thenReturn(collection);
        assertEquals(Collections.emptyList(),mongoService.findListByCursorWithCondition(new CriteriaAndWrapper(),
                Audience.class));
        assertEquals(Collections.emptyList(),mongoService.findListByCursorWithCondition(new CriteriaAndWrapper(),
                Audience.class,collectionName));
    }

    @Test
    public void findAllByCursorBranch(){
        Mockito.when(mongoTemplate.getCollectionName(ArgumentMatchers.any(Class.class))).thenReturn(collectionName);
        Mockito.when(mongoTemplate.getCollection(ArgumentMatchers.anyString())).thenReturn(collection);
        Mockito.when(collection.find()).thenReturn(iterable);
        Mockito.when(iterable.iterator()).thenReturn(mongoCursor);
        assertEquals(Collections.emptyList(),mongoService.findAllByCursor(Audience.class));
    }

    @Test
    public void findAllByCursor(){
        Mockito.when(mongoTemplate.getCollectionName(ArgumentMatchers.any(Class.class))).thenReturn(collectionName);
        Mockito.when(mongoTemplate.getCollection(ArgumentMatchers.anyString())).thenReturn(collection);
        assertEquals(Collections.emptyList(),mongoService.findAllByCursor(Audience.class));
    }

    @Test
    public void findPageByCursor(){
        Mockito.when(mongoTemplate.getCollectionName(ArgumentMatchers.any(Class.class))).thenReturn(collectionName);
        Mockito.when(mongoTemplate.getCollection(ArgumentMatchers.anyString())).thenReturn(collection);
        Mockito.when(collection.countDocuments(ArgumentMatchers.any(Bson.class))).thenReturn(100L);
        Mockito.when(collection.find(ArgumentMatchers.any(Bson.class))).thenReturn(iterable);
        Mockito.when(iterable.limit(10)).thenReturn(iterable);
        Mockito.when(iterable.sort(ArgumentMatchers.any(Bson.class))).thenReturn(iterable);
        Mockito.when(iterable.iterator()).thenReturn(mongoCursor);
        assertEquals(Optional.of(100L).get(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class).getPage().getTotal());
        assertEquals(Optional.of(100L).get(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class,collectionName).getPage().getTotal());
    }

    @Test
    public void findPageByCursor2(){
        Mockito.when(mongoTemplate.getCollectionName(ArgumentMatchers.any(Class.class))).thenReturn(collectionName);
        Mockito.when(mongoTemplate.getCollection(ArgumentMatchers.anyString())).thenReturn(collection);
        Mockito.when(collection.countDocuments(ArgumentMatchers.any(Bson.class))).thenReturn(101L);
        Mockito.when(collection.find(ArgumentMatchers.any(Bson.class))).thenReturn(iterable);
        Mockito.when(iterable.limit(10)).thenReturn(iterable);
        Mockito.when(iterable.sort(ArgumentMatchers.any(Bson.class))).thenReturn(iterable);
        Mockito.when(iterable.iterator()).thenReturn(mongoCursor);
        assertEquals(Collections.emptyList(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class).getData());
        assertEquals(Collections.emptyList(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class,collectionName).getData());
    }

    @Test
    public void findPageByCursor3(){
        Mockito.when(mongoTemplate.getCollectionName(ArgumentMatchers.any(Class.class))).thenReturn(collectionName);
        Mockito.when(mongoTemplate.getCollection(ArgumentMatchers.anyString())).thenReturn(collection);
        Mockito.when(collection.find(ArgumentMatchers.any(Bson.class))).thenReturn(iterable);
        Mockito.when(iterable.limit(10)).thenReturn(iterable);
        Mockito.when(iterable.sort(ArgumentMatchers.any(Bson.class))).thenReturn(iterable);
        Mockito.when(iterable.iterator()).thenReturn(mongoCursor);
        assertEquals(Collections.emptyList(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class).getData());
        assertEquals(Collections.emptyList(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class,collectionName).getData());
    }
}
