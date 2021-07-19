package Services;

import Models.InventoryMovement;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;
import java.util.List;

public class InventoryMovementService {
    MongoClient mongoClient = MongoClients.create("mongodb://localhost");
    MongoDatabase database = mongoClient.getDatabase("order");
    MongoCollection<Document> movementCollection = database.getCollection("inventoryMovement");

    public InventoryMovement getMovementById(int id){
        InventoryMovement inventoryMovement = null;
        Document show = new Document();
        Bson filter = Filters.eq("id",id);
        show.append("_id",0).append("warhouseId",1).append("type",1).append("productId",1).append("quantity",1).append("date",1);
        try(MongoCursor<Document> movementMongo = movementCollection.find(filter).projection(show).iterator()){
            Document aux = new Document();
            while(movementMongo.hasNext()){
                aux = movementMongo.next();
                int varId = aux.getInteger("id");
                int idWarehouse = aux.getInteger("warehouseID");
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
        List<InventoryMovement> inventoryMovement = null;
        Document show = new Document();
        show.append("_id",0).append("warhouseId",1).append("type",1).append("productId",1).append("quantity",1).append("date",1);
        try(MongoCursor<Document> movementMongo = movementCollection.find().projection(show).iterator()){
            Document aux = new Document();
            while(movementMongo.hasNext()){
                aux = movementMongo.next();
                int varId = aux.getInteger("id");
                int idWarehouse = aux.getInteger("warehouseID");
                String type = aux.getString("type");
                String productId = aux.getString("productId");
                int quantity = aux.getInteger("quantity");
                Date date = aux.getDate("date");
                InventoryMovement myInventoryMovement = new InventoryMovement(varId, idWarehouse,type,productId,quantity,date);
                inventoryMovement.add(myInventoryMovement);
            }
        }
        return inventoryMovement;
    }

    public void postInventoryMovement(InventoryMovement inventoryMovement){
        Document aux = new Document();
        aux.put("id",inventoryMovement.getId());
        aux.put("warehouseId", inventoryMovement.getWarehouse());
        aux.put("type",inventoryMovement.getType());
        aux.put("quantity", inventoryMovement.getQuantity());
        aux.put("date", inventoryMovement.getDate());
        movementCollection.insertOne(aux);
    }
}
