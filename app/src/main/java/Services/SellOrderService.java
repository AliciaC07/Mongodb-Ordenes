package Services;

import Models.SellOrder;
import Models.SoldProduct;
import Models.Supplier;
import com.google.gson.Gson;
import com.mongodb.client.*;
import org.bson.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SellOrderService {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost");
    MongoDatabase database = mongoClient.getDatabase("test");
    MongoCollection<Document> orderCollection = database.getCollection("sellOrder");
    MongoCollection<Document> supplierCollection = database.getCollection("supplier");
    private static SellOrderService sellOrderService;

    public SellOrderService() {
    }

    public static SellOrderService getInstance() {
        if (sellOrderService == null) {
            sellOrderService = new SellOrderService();
        }

        return sellOrderService;
    }


    public SellOrder generateReceipt(Supplier supplier, List<SoldProduct> soldProduct) {
        SellOrder sellOrders = new SellOrder();
            List<Document> buildAggregate= new ArrayList<>();
            //match
            buildAggregate.add(new Document("$match", new Document("id", supplier.getId())));
            //lookup
            Document docLookup = new Document("from","product")
                    .append("localField","productId")
                    .append("foreignField", "id")
                    .append("as", "lookup");
            buildAggregate.add(new Document("$lookup", docLookup));
            buildAggregate.add(new Document("$unwind", "$lookup"));
            //projecj
            Document docProject = new Document("description", "$lookup.description")
                    .append("id", "$lookup.id")
                    .append("availability", "$lookup.availability")
                    .append("unit", "$lookup.unit");
            buildAggregate.add(new Document("$project", docProject));
            System.out.println(new Gson().toJson(buildAggregate));

            AggregateIterable<Document> products = supplierCollection.aggregate(buildAggregate);


        //build sellOrder
            Document order = new Document();
            if (orderCollection.countDocuments() > 0){
                order.put("id", Long.valueOf(orderCollection.countDocuments() + 1).intValue());

            }else {
                order.put("id",1);
            }
            order.put("supplierId", supplier.getId());
            order.put("date", LocalDate.now());
            List<Document> productsOrder = new ArrayList<>();
            float totalAmount = 0.0f;
        ///System.out.println(new Gson().toJson(products));
        for (SoldProduct sp: soldProduct) {
            for (Document d: products) {
                Document doc = new Document();
                if (d.get("id").toString().equalsIgnoreCase(sp.getProduct().getId())){
                    doc.put("productId",sp.getProduct().getId());
                    doc.put("quantity", sp.getQuantity());
                    doc.put("unit", sp.getProduct().getUnit());
                    doc.put("price", sp.getPrice());
                    totalAmount += sp.getPrice();
                    productsOrder.add(doc);
                }
            }
        }
        System.out.println(productsOrder.get(0));
        order.put("products", productsOrder);
        order.put("total", totalAmount);
        orderCollection.insertOne(order);
        sellOrders.setId(Integer.parseInt(order.get("id").toString()));
        sellOrders.setDate(new Date());
        sellOrders.setSoldProducts(soldProduct);
        sellOrders.setSupplierId(supplier.getId());
        return sellOrders;

    }
}
