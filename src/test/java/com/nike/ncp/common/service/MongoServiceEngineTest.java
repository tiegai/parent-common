package com.nike.ncp.common.service;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
        reflectUtil.when(()->ReflectUtil.getFields(any(Class.class))).thenReturn(new Field[]{});
        when(mongoTemplate.save(audience)).thenReturn(audience);
        assertEquals("1", mongoService.insert(audience));
    }

    @Test
    public void insertAll() {
        mongoService.insertAll(new ArrayList<>());
        verify(mongoTemplate, times(1)).insertAll(any(List.class));
    }


    @Test
    public void update() {
        reflectUtil.when(() -> ReflectUtil.getFields(any(Class.class))).thenReturn(new Field[]{});
        when(mongoTemplate.findById(anyString(), any(Class.class))).thenReturn(audience);
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
        when(mongoTemplate.remove(any(Query.class), anyString())).thenReturn(deleteResult);
        when(mongoTemplate.remove(any(Query.class), any(Class.class))).thenReturn(deleteResult);
        List<String> list = new ArrayList<>();
        list.add("1");
        mongoService.deleteByIds(list, Audience.class);
        verify(mongoTemplate, times(1)).remove(any(Query.class), any(Class.class));
        mongoService.deleteByQuery(new CriteriaAndWrapper(), collectionName);
        verify(mongoTemplate, times(1)).remove(any(Query.class), anyString());
        mongoService.deleteByIds(list, collectionName);
        mongoService.deleteById("1", collectionName);
        mongoService.deleteById("2", Audience.class);
        mongoService.deleteByQuery(new CriteriaAndWrapper(), Audience.class);
    }

    @Test
    public void find() {
        when(mongoTemplate.findOne(any(Query.class), any(Class.class))).thenReturn(audience);
        when(mongoTemplate.findOne(any(Query.class), any(Class.class),anyString())).thenReturn(audience);
        when(mongoTemplate.findById(anyString(), any(Class.class), anyString())).thenReturn(audience);
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
        when(mongoTemplate.find(any(Query.class), any(Class.class))).thenReturn(new ArrayList());
        when(mongoTemplate.find(any(Query.class), any(Class.class),anyString())).thenReturn(new ArrayList());
        CriteriaAndWrapper wrapper = new CriteriaAndWrapper();
        SortBuilder sortBuilder = new SortBuilder();
        assertEquals(Collections.emptyList(),mongoService.findListByQuery(wrapper, Audience.class));
        assertEquals(Collections.emptyList(),mongoService.findListByQuery(wrapper, Audience.class, collectionName));
        assertEquals(Collections.emptyList(),mongoService.findListByQuery(wrapper, sortBuilder, Audience.class));
        assertEquals(Collections.emptyList(),mongoService.findListByQuery(wrapper, sortBuilder, Audience.class, collectionName));
    }

    @Test
    public void findListByIds() {
        when(mongoTemplate.find(any(Query.class), any(Class.class))).thenReturn(new ArrayList());
        when(mongoTemplate.find(any(Query.class), any(Class.class),anyString())).thenReturn(new ArrayList());
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
        when(mongoTemplate.find(any(Query.class), any(Class.class))).thenReturn(new ArrayList());
        Class<Audience> audienceClass = Audience.class;
        assertEquals(Collections.emptyList(), mongoService.findAll(audienceClass));
        assertEquals(Collections.emptyList(), mongoService.findAll(new SortBuilder(), audienceClass));
    }

    @Test
    public void findCount() {
        when(mongoTemplate.getCollectionName(any(Class.class))).thenReturn(collectionName);
        when(mongoTemplate.getCollection(anyString())).thenReturn(collection);
        when(mongoTemplate.count(any(Query.class), anyString())).thenReturn(2L);
        when(collection.estimatedDocumentCount()).thenReturn(3L);
        assertEquals(Optional.of(2L).get(), mongoService.findCountByQuery(new CriteriaAndWrapper(),collectionName));
        assertEquals(Optional.of(3L).get(), mongoService.findAllCount(Audience.class));
        assertEquals(Optional.of(2L).get(), mongoService.findAllCount(collectionName));
    }

    @Test
    public void findPage() {
        when(mongoTemplate.getCollectionName(any(Class.class))).thenReturn(collectionName);
        when(mongoTemplate.getCollection(anyString())).thenReturn(collection);
        when(collection.estimatedDocumentCount()).thenReturn(100L);
        when(mongoTemplate.find(any(Query.class),any(Class.class))).thenReturn(new ArrayList());
        assertEquals(0,mongoService.findPage(new CriteriaAndWrapper(1,10),Audience.class).getData().size());
        assertEquals(0,mongoService.findPage(new CriteriaAndWrapper(1,10),Audience.class,collectionName).getData().size());
        assertEquals(0,
                mongoService.findPage(new CriteriaAndWrapper(1,10),new SortBuilder(),Audience.class).getData().size());
        assertEquals(0,mongoService.findPage(new CriteriaAndWrapper(1,10),new SortBuilder(),Audience.class,
                collectionName).getData().size());
    }

    @Test
    public void findListByCursor(){
        when(mongoTemplate.getCollectionName(any(Class.class))).thenReturn(collectionName);
        when(mongoTemplate.getCollection(anyString())).thenReturn(collection);
        when(collection.find(any(Bson.class))).thenReturn(iterable);
        when(iterable.iterator()).thenReturn(mongoCursor);
        assertEquals(Collections.emptyList(),mongoService.findListByCursorWithCondition(new CriteriaAndWrapper(),
                Audience.class));
        assertEquals(Collections.emptyList(),mongoService.findListByCursorWithCondition(new CriteriaAndWrapper(),
                Audience.class,collectionName));
    }

    @Test
    public void findListByCursorBranch(){
        when(mongoTemplate.getCollectionName(any(Class.class))).thenReturn(collectionName);
        when(mongoTemplate.getCollection(anyString())).thenReturn(collection);
        assertEquals(Collections.emptyList(),mongoService.findListByCursorWithCondition(new CriteriaAndWrapper(),
                Audience.class));
        assertEquals(Collections.emptyList(),mongoService.findListByCursorWithCondition(new CriteriaAndWrapper(),
                Audience.class,collectionName));
    }

    @Test
    public void findAllByCursorBranch(){
        when(mongoTemplate.getCollectionName(any(Class.class))).thenReturn(collectionName);
        when(mongoTemplate.getCollection(anyString())).thenReturn(collection);
        when(collection.find()).thenReturn(iterable);
        when(iterable.iterator()).thenReturn(mongoCursor);
        assertEquals(Collections.emptyList(),mongoService.findAllByCursor(Audience.class));
    }

    @Test
    public void findAllByCursor(){
        when(mongoTemplate.getCollectionName(any(Class.class))).thenReturn(collectionName);
        when(mongoTemplate.getCollection(anyString())).thenReturn(collection);
        assertEquals(Collections.emptyList(),mongoService.findAllByCursor(Audience.class));
    }

    @Test
    public void findPageByCursor(){
        when(mongoTemplate.getCollectionName(any(Class.class))).thenReturn(collectionName);
        when(mongoTemplate.getCollection(anyString())).thenReturn(collection);
        when(collection.countDocuments(any(Bson.class))).thenReturn(100L);
        when(collection.find(any(Bson.class))).thenReturn(iterable);
        when(iterable.limit(10)).thenReturn(iterable);
        when(iterable.sort(any(Bson.class))).thenReturn(iterable);
        when(iterable.iterator()).thenReturn(mongoCursor);
        assertEquals(Optional.of(100L).get(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class).getPage().getTotal());
        assertEquals(Optional.of(100L).get(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class,collectionName).getPage().getTotal());
    }

    @Test
    public void findPageByCursor2(){
        when(mongoTemplate.getCollectionName(any(Class.class))).thenReturn(collectionName);
        when(mongoTemplate.getCollection(anyString())).thenReturn(collection);
        when(collection.countDocuments(any(Bson.class))).thenReturn(101L);
        when(collection.find(any(Bson.class))).thenReturn(iterable);
        when(iterable.limit(10)).thenReturn(iterable);
        when(iterable.sort(any(Bson.class))).thenReturn(iterable);
        when(iterable.iterator()).thenReturn(mongoCursor);
        assertEquals(Collections.emptyList(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class).getData());
        assertEquals(Collections.emptyList(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class,collectionName).getData());
    }

    @Test
    public void findPageByCursor3(){
        when(mongoTemplate.getCollectionName(any(Class.class))).thenReturn(collectionName);
        when(mongoTemplate.getCollection(anyString())).thenReturn(collection);
        when(collection.find(any(Bson.class))).thenReturn(iterable);
        when(iterable.limit(10)).thenReturn(iterable);
        when(iterable.sort(any(Bson.class))).thenReturn(iterable);
        when(iterable.iterator()).thenReturn(mongoCursor);
        assertEquals(Collections.emptyList(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class).getData());
        assertEquals(Collections.emptyList(),mongoService.findPageByCursor(new CriteriaAndWrapper(1,10),
                "63c3d3ff323e5300970a76fe",Audience.class,collectionName).getData());
    }
}
