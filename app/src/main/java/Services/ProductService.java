package Services;

import Models.Product;
import Models.Warehouse;
import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProductService {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost");
    MongoDatabase database = mongoClient.getDatabase("order");
    MongoCollection<Document> productsCollection = database.getCollection("product");

    public Product getProductById(String id){
        Product product = null;
        Bson filter = Filters.eq("id",id);
        Document show = new Document();
        List<Document> params = new ArrayList<>();
        show.append("id",id);
        params.add(show);
        AggregateIterable<Document> productString = productsCollection.aggregate(params);

        for (Document myProduct: productString) {
            try{
                List<Warehouse> warehouse = new ArrayList<>();
                product = new Product();
                product.setId(myProduct.getString("id"));
                product.setDescription(myProduct.getString("description"));
                product.setUnit(myProduct.getString("unit"));
                warehouse = myProduct.getList("availability",Warehouse.class);
                product.setWarehouse(warehouse);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        return product;
    }

    public List<Product> getAll(){
        List<Product> products = null;
        Document show = new Document();
        show.append("_id",0).append("id",1).append("description",1).append("availability",1).append("unit",1);
        try (MongoCursor<Document> productsMongo = productsCollection.find().projection(show).iterator()) {
            Document aux = new Document();
            while (productsMongo.hasNext()) {
                aux = productsMongo.next();
                String productJson = aux.toJson();
                Product product = new Gson().fromJson(productJson,Product.class);
                assert products != null;
                products.add(product);
            }
        }
        return products;
    }
}
