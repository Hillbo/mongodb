package com.hillbo.mongodb.controller;


import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.assertj.core.util.Lists;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MongoDBControllerTest {

    @Test
    public void createCollection(){
        // 获取连接
        MongoClient mongoClient = MongoDBUtils.getMongoDBClient();
        // 获取database
        MongoDatabase mongoDatabase = mongoClient.getDatabase("zhb_mongo");

        // 创建一个collection
        String collectionName = "test_collection";
        mongoDatabase.createCollection(collectionName);

        // 关闭mongoDB客户端资源，释放连接
        mongoClient.close();
    }

    /**
     * 新增一个Document
     */
    @Test
    public void addDocument(){
        // 获取连接
        MongoClient mongoClient = MongoDBUtils.getMongoDBClient();
        // 获取database
        MongoDatabase mongoDatabase = mongoClient.getDatabase("zhb_mongo");
        // 获取collection
        MongoCollection<Document> collection = mongoDatabase.getCollection("test_collection");
        // 创建一个Document
        Document document = new Document();
        document.put("id", "a");
        document.put("name", "x");
        document.put("office","qwe");
        document.put("num", 1);
        collection.insertOne(document);

        mongoClient.close();
    }

    /**
     * 新增多个Document
     */
    @Test
    public void addDocuments(){
        // 获取连接
        MongoClient mongoClient = MongoDBUtils.getMongoDBClient();
        // 获取database
        MongoDatabase mongoDatabase = mongoClient.getDatabase("zhb_mongo");
        // 获取collection
        MongoCollection<Document> collection = mongoDatabase.getCollection("test_collection");

        // 创建多个Document
        List<Document> documentList = new ArrayList<>();
        Document document1 = new Document();
        document1.put("id", "b");
        document1.put("name", "y");
        document1.put("office","asd");
        document1.put("num", 2);

        Document document2 = new Document();
        document2.put("id", "c");
        document2.put("name", "z");
        document2.put("office","zxc");
        document2.put("num", 3);
        documentList.add(document1);
        documentList.add(document2);

        collection.insertMany(documentList);

        mongoClient.close();
    }

    /**
     * 根据条件查询document
     */
    @Test
    public void queryDocumentByCondition(){
        // 获取连接
        MongoClient mongoClient = MongoDBUtils.getMongoDBClient();
        // 获取database
        MongoDatabase mongoDatabase = mongoClient.getDatabase("zhb_mongo");
        // 获取collection
        MongoCollection<Document> collection = mongoDatabase.getCollection("test_collection");

        // 查询id=a or id=2的数据(第一组权限)
        Bson bson1 = Filters.eq("id", "a");
        Bson bson2 = Filters.eq("id", "b");
        Bson authFirstFilter = Filters.or(bson1, bson2);

        // 第二组权限(按组织过滤)
        Bson bson3 = Filters.in("office", Lists.newArrayList("asd", "qwe"));
        Bson authSecondFilter = Filters.and(bson3);

        // 两组权限组合之后的权限
        Bson finalAuthBson = Filters.or(authFirstFilter, authSecondFilter);

        FindIterable<Document> documents = collection.find(finalAuthBson);

        for (Document document : documents) {
            System.out.print("id:" + document.get("id") + "   ");
            System.out.print("name:" + document.get("name") + "   ");
            System.out.println("num:" + document.get("num"));
        }

        System.out.println("-------------------------------------");


        // 根据权限过滤之后，模糊查询
        Bson dataBson1 = Filters.eq("num", null);
        // Bson dataBson2 = Filters.eq("name", "a");

        Bson dataFirstBson = Filters.and(dataBson1);

        // 权限和模糊查询组合后的过滤
        Bson filalBson = Filters.and(finalAuthBson, dataFirstBson);

        documents.filter(filalBson);

        for (Document document : documents) {
            System.out.print("id:" + document.get("id") + "   ");
            System.out.print("name:" + document.get("name") + "   ");
            System.out.println("num:" + document.get("num"));
        }
    }


}