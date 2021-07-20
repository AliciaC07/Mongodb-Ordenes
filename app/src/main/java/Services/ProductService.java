package Services;

import Models.Dto.AvailableDays;
import Models.Product;
import Models.SellOrder;
import Models.Warehouse;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProductService {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost");
    MongoDatabase database = mongoClient.getDatabase("test");
    MongoCollection<Document> productsCollection = database.getCollection("product");

    private static ProductService productService;

    public static ProductService getInstance(){
        if(productService == null)
            productService = new ProductService();
        return productService;
    }

    public Product getProductById(String id){
        Product product = null;
        Bson filter = Filters.eq("id",id);
        Document show = new Document();
        List<Document> params = new ArrayList<>();
        show.append("id",id);
        params.add(new Document("$match",show));
        AggregateIterable<Document> productString = productsCollection.aggregate(params);

        for (Document myProduct: productString) {
            try{
                /*List<Warehouse> warehouse = new ArrayList<>();
                product = new Product();
                product.setId(myProduct.getString("id"));
                product.setDescription(myProduct.getString("description"));
                product.setUnit(myProduct.getString("unit"));
                warehouse = myProduct.getList("availability", warehouse.getClass());
                product.setWarehouse(warehouse);*/
                product = new Product();
                //System.out.println(myProduct.toJson());
                product = new Gson().fromJson(myProduct.toJson(), Product.class);
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

    public Warehouse getWareouseWithMoreProducts(Product product){
        Warehouse warehouse = new Warehouse();
        for (Warehouse mwh: product.getAvailability()) {
            if (mwh.getQuantity() > warehouse.getQuantity())
                warehouse = mwh;
        }
        return warehouse;
    }

    public AvailableDays getAvailableDays(String productId, Date forWhen){
        AvailableDays availableDays = null;
        List<Document>params = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        LocalDate future = LocalDate.parse(format.format(forWhen));
        LocalDate today = LocalDate.now();

        //DTO que contenga el id del suplidor, cantidad de dias pde entrega del suplidor y la fecha en la que se tiene que pedir

        // Match para obtener solo el articulo
        Document match = new Document();
        match.append("id",productId);
        params.add(new Document("$match",match));

        // unwind de los warehouses
        params.add(new Document("$unwind","$availability"));

        // agrupar por id para contar los productos disponibles totales
        Document group1 = new Document();
        group1.append("_id", new Document("id","$id"));
        Document totalAvailable = new Document("$sum","$availability.quantity");
        group1.append("totalAvailable",totalAvailable);
        params.add(new Document("$group",group1));

        //project para sacar los valores
        Document project = new Document("_id",0);
        project.append("id","$_id.id").append("totalAvailable","$totalAvailable");
        params.add(new Document("$project",project));

        // le hacemos set a la cantidad de dias disponibles hasta la entrega
        //primero la resta de fechas
        BasicDBList subtract = new BasicDBList();
        subtract.add(new Document("$dateFromString",new Document("dateString",future.toString())
                .append("format","%Y-%m-%d")));
        subtract.add(new Document("$dateFromString",new Document("dateString",today.toString())
                .append("format","%Y-%m-%d")));

        //luego la division con la constante
        BasicDBList divide = new BasicDBList();
        divide.add(new BasicDBObject("$subtract",subtract));
        divide.add(1000*60*60*24);

        //ahora el round para obtener un entero aproximado
        BasicDBList round = new BasicDBList();
        round.add(new Document("$divide",divide));

        //set de AvailableDays
        Document myAvailableDays = new Document("availableDays",new BasicDBObject("$round",round));
        params.add(new Document("$set",myAvailableDays));

        //project final para sacar las variables
        Document project2 = new Document();
        project2.append("id","$id").append("totalAvailable","$totalAvailable")
                .append("availableDays","$availableDays");

        AggregateIterable<Document> showMe = productsCollection.aggregate(params);

        for (Document document:showMe) {
            availableDays = new Gson().fromJson(document.toJson(),AvailableDays.class);
        }

        return availableDays;
    }
}
