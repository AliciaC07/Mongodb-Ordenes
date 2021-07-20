package Services;

import Models.Supplier;
import com.google.gson.Gson;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class SupplierService {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost");
    MongoDatabase database = mongoClient.getDatabase("test");
    MongoCollection<Document> supplierCollection = database.getCollection("supplier");
    private static SupplierService supplierService;

    public SupplierService() {
    }

    public static SupplierService getInstance() {
        if (supplierService == null) {
            supplierService = new SupplierService();
        }

        return supplierService;
    }

    public Supplier saveSupplier(String productId,Float price, Integer deliveryDate){

        Integer id = 0;
        if (supplierCollection.countDocuments() > 0){
            id = Long.valueOf(supplierCollection.countDocuments() + 1).intValue();
        }else{
            id = 1;
        }
        Document document = new Document("id", id)
                .append("productId", productId)
                .append("price", price)
                .append("deliveryDate", deliveryDate);
        System.out.println(new Gson().toJson(document));
        return new Gson().fromJson(document.toJson(), Supplier.class);

    }





    public Supplier getSupplierByDeliberydate(String productId, Integer deliveryTime){
        Supplier supplier = null;
        List<Document> buildAggregate = new ArrayList<>();
        ///Match
        Document doMatch = new Document("$match", new Document("productId",productId)
                .append("deliveryDate", new Document("$lte", deliveryTime)));

        buildAggregate.add(doMatch);
        //Sort
        Document docSort = new Document("$sort", new Document("price", 1));
        buildAggregate.add(docSort);
        //Limit
        Document docLimit = new Document("$limit", 1);
        buildAggregate.add(docLimit);
        //project
        Document docPro = new Document("$project", new Document("id", "$id")
                .append("productId", "$productId")
                .append("deliveryDate", "$deliveryDate")
                .append("price", "$price"));

        buildAggregate.add(docPro);
        System.out.println(new Gson().toJson(buildAggregate));
        AggregateIterable<Document> suppliers = supplierCollection.aggregate(buildAggregate);
        for (Document d : suppliers) {
            supplier = new Gson().fromJson(d.toJson(), Supplier.class);
        }
        return supplier;

    }
    public  Supplier getSupplierByLess(String productId){
        Supplier supplier = null;
        List<Document> buildAggregate = new ArrayList<>();
        ///Match
        Document doMatch = new Document("$match", new Document("productId",productId)
                );

        buildAggregate.add(doMatch);
        //Sort
        Document docSort = new Document("$sort", new Document("deliveryTime", 1));
        buildAggregate.add(docSort);
        //Limit
        Document docLimit = new Document("$limit", 1);
        buildAggregate.add(docLimit);
        //project
        Document docPro = new Document("$project", new Document("id", "$id")
                .append("productId", "$productId")
                .append("deliveryDate", "$deliveryDate")
                .append("price", "$price"));

        buildAggregate.add(docPro);
        System.out.println(new Gson().toJson(buildAggregate));
        AggregateIterable<Document> suppliers = supplierCollection.aggregate(buildAggregate);
        for (Document d : suppliers) {
            supplier = new Gson().fromJson(d.toJson(), Supplier.class);
        }
        return supplier;

    }
}
