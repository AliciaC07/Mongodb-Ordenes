package Services;

import Models.Dto.AvailableDays;
import Models.Dto.OrderDate;
import Models.Product;
import Models.SellOrder;
import Models.SoldProduct;
import Models.Supplier;
import com.google.gson.Gson;
import com.mongodb.client.*;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellOrderService {

    MongoClient mongoClient = MongoClients.create("mongodb://localhost");
    MongoDatabase database = mongoClient.getDatabase("test");
    MongoCollection<Document> orderCollection = database.getCollection("sellOrder");
    MongoCollection<Document> supplierCollection = database.getCollection("supplier");
    private static SellOrderService sellOrderService;
    private static List<SellOrder> sellOrders;

    public SellOrderService() {
    }

    public static List<SellOrder> getSellOrders(){
        if(sellOrders == null)
            sellOrders = new ArrayList<>();
        return sellOrders;
    }

    public static SellOrderService getInstance() {
        if (sellOrderService == null) {
            sellOrderService = new SellOrderService();
        }

        return sellOrderService;
    }
    public List<OrderDate> buildOrders(LocalDate localDate, List<Product> products){
        List<OrderDate> orderDates = new ArrayList<>();
        Set<LocalDate> datesReserve = new HashSet<>();
        List<SoldProduct> soldProducts = new ArrayList<>();
        Map<String, Supplier> generatedS = new HashMap<>();
        LocalDate dateGenerated;
        Supplier supplier;
        for (Product pro: products) {
            AvailableDays ava = ProductService.getInstance().getAvailableDays(pro.getId(), Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            int totalUsage = 0;
            int totalRequire = 0;
            int amountRequired = 0;
            int consume = InventoryMovementService.getInstance().dailySale(ava.getId());
            ///calculo consumo
            totalUsage += ava.getAvailableDays() * consume;
            ///calculo requerido
            totalRequire += totalUsage + consume;
            amountRequired += totalRequire-ava.getTotalAvailable();
            //ahora calcular cuando en cuantos dias se gasta por consumo
            int daysCount = 0;
            for (int i = 0; i <= ava.getAvailableDays(); i++){
                if (ava.getTotalAvailable() > 0 && ava.getTotalAvailable() >= consume){
                    ava.setTotalAvailable(ava.getTotalAvailable() - consume);
                    daysCount++;
                }
            }
            System.out.println(daysCount);
            supplier = SupplierService.getInstance().getSupplierByDeliberydate(ava.getId(), daysCount);
            if (supplier == null){
                supplier = SupplierService.getInstance().getSupplierByLess(ava.getId());

            }
            dateGenerated = localDate.minusDays(supplier.getDeliveryDate()+1);
            System.out.println(dateGenerated);
            SoldProduct soldProduct = new SoldProduct();
            soldProduct.setProduct(ProductService.getInstance().getProductById(ava.getId()));
            soldProduct.setQuantity(amountRequired);
            soldProduct.setPrice(supplier.getPrice());
            soldProduct.setDateGenerated(dateGenerated);
            soldProduct.setSupplier(supplier);
            soldProducts.add(soldProduct);
            generatedS.put(pro.getId(), supplier);
            datesReserve.add(dateGenerated);
        }

        for (LocalDate l: datesReserve) {
            OrderDate orderDate = new OrderDate();
            orderDate.setDate(l);
            for (SoldProduct s : soldProducts) {
                if (s.getDateGenerated().equals(l)){
                    orderDate.setSupplier(s.getSupplier());
                    orderDate.getProducts().add(s);
                }
            }
            orderDates.add(orderDate);
        }
        return orderDates;
    }



    public SellOrder generateReceipt(Supplier supplier, List<SoldProduct> soldProduct, Date orderDate) {
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
            order.put("date", orderDate);
            List<Document> productsOrder = new ArrayList<>();
            float totalAmount = 0.0f;

        for (SoldProduct sp: soldProduct) {
            for (Document d: products) {
                Document doc = new Document();
                if (d.get("id").toString().equalsIgnoreCase(sp.getProduct().getId())){
                    doc.put("productId",sp.getProduct().getId());
                    doc.put("quantity", sp.getQuantity());
                    doc.put("unit", sp.getProduct().getUnit());
                    doc.put("price", sp.getPrice());
                    totalAmount += sp.getPrice()* sp.getQuantity();
                    productsOrder.add(doc);
                }
            }
        }
        order.put("products", productsOrder);
        order.put("total", totalAmount);
        orderCollection.insertOne(order);
        sellOrders.setId(Integer.parseInt(order.get("id").toString()));
        sellOrders.setDate(orderDate);
        sellOrders.setSoldProducts(soldProduct);
        sellOrders.setSupplierId(supplier.getId());
        return sellOrders;

    }
}
