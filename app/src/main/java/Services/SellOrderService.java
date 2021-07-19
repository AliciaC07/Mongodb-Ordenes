package Services;

import Models.Product;
import Models.SellOrder;
import Models.SoldProduct;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class SellOrderService {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost");
    MongoDatabase database = mongoClient.getDatabase("order");
    MongoCollection<Document> orderCollection = database.getCollection("sellOrder");

    public List<SellOrder> generateReceipt(List<SoldProduct> products){
        List<SellOrder> myReceipt = null;
        float total = 0;
        for (SoldProduct product: products) {
            total += product.getPrice()*product.getQuantity();
        }
        return myReceipt;
    }
}
