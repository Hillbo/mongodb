package com.hillbo.mongodb.controller;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDBController {

    /**
     * 新建MongoDBCollection
     */
    private void addCollection() {

        // 获取连接
        MongoClient mongoClient = MongoDBUtils.getMongoDBClient();

        // 获取database
        MongoDatabase mongoDatabase = mongoClient.getDatabase("zhb_mongo");

        // 创建一个collection
        String collectionName = "test_collection";
        mongoDatabase.createCollection(collectionName);

        // 关闭mongoDB客户端资源，释放连接
        mongoClient.close();
        System.out.println("新建Collection成功！");
    }

}
