package Services;

import Models.InventoryMovement;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryMovementService {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost");
    MongoDatabase database = mongoClient.getDatabase("order");
    MongoCollection<Document> movementCollection = database.getCollection("inventoryMovement");

    private static InventoryMovementService inventoryMovementService;

    public static InventoryMovementService getInstance(){
        if (inventoryMovementService == null)
            inventoryMovementService = new InventoryMovementService();
        return inventoryMovementService;
    }

    public InventoryMovement getMovementById(int id){
        InventoryMovement inventoryMovement = null;
        Document show = new Document();
        show.append("id",id);
        List<Document>params = new ArrayList<>();
        params.add(new Document("$match",show));
        try(MongoCursor<Document> movementMongo = movementCollection.aggregate(params).iterator()){
            Document aux = new Document();
            while(movementMongo.hasNext()){
                aux = movementMongo.next();
                int varId = aux.getInteger("id");
                int idWarehouse = aux.getInteger("warehouseId");
                String type = aux.getString("type");
                String productId = aux.getString("productId");
                int quantity = aux.getInteger("quantity");
                Date date = aux.getDate("date");
                inventoryMovement = new InventoryMovement(varId, idWarehouse,type,productId,quantity,date);
            }
        }
        return inventoryMovement;
    }

    public List<InventoryMovement> getAll(){
        List<InventoryMovement> inventoryMovement = new ArrayList<>();
        Document show = new Document();
        show.append("_id",0).append("id",1).append("warehouseId",1).append("type",1).append("productId",1).append("quantity",1).append("date",1);
        try(MongoCursor<Document> movementMongo = movementCollection.find().projection(show).iterator()){

            while(movementMongo.hasNext()){
                Document aux = new Document();
                aux = movementMongo.next();
                int varId = aux.getInteger("id");
                int idWarehouse = aux.getInteger("warehouseId");
                String type = aux.getString("type");
                String productId = aux.getString("productId");
                int quantity = aux.getInteger("quantity");
                Date date = aux.getDate("date");
                InventoryMovement myInventoryMovement = new InventoryMovement(varId, idWarehouse,type,productId,quantity,date);
                assert inventoryMovement != null;
                inventoryMovement.add(myInventoryMovement);
            }
        }
        return inventoryMovement;
    }

    public void postInventoryMovement(InventoryMovement inventoryMovement){
        Document aux = new Document();
        aux.put("id",inventoryMovement.getId());
        aux.put("productId", inventoryMovement.getIdProduct());
        aux.put("warehouseId", inventoryMovement.getWarehouse());
        aux.put("type",inventoryMovement.getType());
        aux.put("quantity", inventoryMovement.getQuantity());
        aux.put("date", inventoryMovement.getDate());
        movementCollection.insertOne(aux);
    }

    public List<InventoryMovement> getMovementByFilter(String type, String product, Date date) {
        List<InventoryMovement> movements = new ArrayList<>();
        if (!type.equalsIgnoreCase("") || !product.equalsIgnoreCase("") || !(date == null)) {
            List<Document> params = new ArrayList<>();
            Document show = new Document();
            if(!type.equalsIgnoreCase(""))
                show.append("type",type);
            if(!product.equalsIgnoreCase(""))
                show.append("productId",product);
            if(!(date == null))
                show.append("date", date);

            params.add(new Document("$match",show));
            try (MongoCursor<Document> inventoryMovement = movementCollection.aggregate(params).iterator()) {
                while (inventoryMovement.hasNext()) {
                    Document aux = new Document();
                    aux = inventoryMovement.next();
                    int id = aux.getInteger("id");
                    int warehouse = aux.getInteger("warehouseId");
                    String myType = aux.getString("type");
                    String productId = aux.getString("productId");
                    int quantity = aux.getInteger("quantity");
                    Date myDate = aux.getDate("date");
                    InventoryMovement ivm = new InventoryMovement(id, warehouse, myType, productId,quantity,myDate);
                    movements.add(ivm);
                }
            }
        }
        return movements;
    }

    public int dailySale(String productId){
        List<Document> params = new ArrayList<>();
        int quant = 0;

        //Match para filtrar los movimientos
        //que solo salgan los de Salida
        Document match = new Document();
        match.append("type","SALIDA").append("productId",productId);
        params.add(new Document("$match", match));

        //Group producto por id y date
        Document group1 = new Document();
        group1.append("_id", new Document("productId","$productId")).append("date","$date");
        Document sumSoldProduct = new Document();
        sumSoldProduct.append("$sum","$quantity");
        group1.append("totalProductSold",sumSoldProduct);
        params.add(new Document("$group", group1));

        //project de los datos de la etapa anterior
        Document project = new Document();
        project.append("_id",0).append("productId","$_id.productId")
                .append("date","$_id.date").append("totalProductSold","$totalProductSold");
        params.add(new Document("$project",project));

        //agrupamos para poder sacar el promedio
        Document group2 = new Document("_id", new Document("productId","$productId"));
        Document average = new Document();
        average.append("$avg", "$totalProductSold");
        group2.append("sellAverage",average);
        params.add(new Document("$group", group2));

        //project para rendondear el average
        Document project2 = new Document();
        project2.append("productId","$_id.productId")
                .append("sellAverage",new Document("$floor","$sellAverage"));
        params.add(new Document("$project",project2));

        //se ejecuta el aggregate finally!!!
        AggregateIterable<Document> showMe = movementCollection.aggregate(params);

        //se obtiene el resultado
        for (Document document: showMe) {
            quant = (int)Double.parseDouble(document.getDouble("average").toString());
        }
        return quant;
    }
}
