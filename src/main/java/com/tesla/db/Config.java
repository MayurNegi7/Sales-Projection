package com.tesla.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Config {
    // Updated with new credentials
    private final static String username = "mayurnegi5";  // Replace with your actual username if needed
    private final static String password = "mamaan123#";  // Replace with your actual password
    private final static String dbName = "SALESDATA";  // Replace with your actual database name
    private final static String url = "mongodb+srv://mayurnegi5:" + password + "@salesdata.mhgxx.mongodb.net/?retryWrites=true&w=majority&appName=SALESDATA";

    private static MongoCollection<Document> datasets;

    // Connect to MongoDB
    public static void connectDB() {
        try {
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
            mongoLogger.setLevel(Level.SEVERE);

            MongoClient client = MongoClients.create(url);
            MongoDatabase database = client.getDatabase(dbName);
            datasets = database.getCollection("datasets");
            System.out.println("MongoDB connected");
        } catch (Exception e) {
            System.out.println("Please check your internet connection.");
            System.exit(0);
        }
    }

    public static MongoCollection<Document> getDatasetCollection() {
        return datasets;
    }
}
