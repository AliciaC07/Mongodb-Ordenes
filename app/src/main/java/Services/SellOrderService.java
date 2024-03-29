package Services;

import Models.Dto.AvailableDays;
import Models.Dto.OrderDate;
import Models.Dto.ProductCart;
import Models.Dto.SoldProductDto;
import Models.Product;
import Models.SellOrder;
import Models.SoldProduct;
import Models.Supplier;
import com.google.gson.Gson;
import com.mongodb.client.*;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import javax.print.Doc;
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


    public static SellOrderService getInstance() {
        if (sellOrderService == null) {
            sellOrderService = new SellOrderService();
        }

        return sellOrderService;
    }
    public List<OrderDate> buildOrders(LocalDate localDate, List<ProductCart> products){
        List<OrderDate> orderDates = new ArrayList<>();
        Set<LocalDate> datesReserve = new HashSet<>();
        Set<Supplier> suppliers = new HashSet<>();
        List<SoldProduct> soldProducts = new ArrayList<>();
        LocalDate dateGenerated;
        Supplier supplier;
        for (ProductCart pro: products) {
            AvailableDays ava = ProductService.getInstance().getAvailableDays(pro.getProduct().getId(), Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            int totalUsage = 0;
            int totalRequire = 0;
            int amountRequired = pro.getQuantity();

            int consume = InventoryMovementService.getInstance().dailySale(ava.getId());
            ///calculo consumo
            totalUsage += ava.getAvailableDays() * consume;
            ///calculo requerido
            totalRequire += totalUsage + consume;
            amountRequired += totalRequire-ava.getTotalAvailable();
            //ahora calcular cuando en cuantos dias se gasta por consumo
            if (ava.getTotalAvailable() >= amountRequired){
                continue;
            }
            //ahora calcular cuando en cuantos dias se gasta por consumo
            int days = Double.valueOf(Math.floor(ava.getTotalAvailable()/consume)).intValue();
            System.out.println(days);
            supplier = SupplierService.getInstance().getSupplierByDeliberydate(ava.getId(), days);
            if (supplier == null){
                supplier = SupplierService.getInstance().getSupplierByLess(ava.getId());

            }
            if (days == 1){
                dateGenerated = LocalDate.now();
            }else {
                dateGenerated = localDate.minusDays(supplier.getDeliveryDate()+1);
            }
            System.out.println(dateGenerated);
            SoldProduct soldProduct = new SoldProduct();
            soldProduct.setProduct(ProductService.getInstance().getProductById(ava.getId()));
            soldProduct.setQuantity(amountRequired);
            soldProduct.setPrice(supplier.getPrice());
            soldProduct.setDateGenerated(dateGenerated);
            soldProduct.setSupplier(supplier);
            soldProducts.add(soldProduct);
            datesReserve.add(dateGenerated);
            suppliers.add(supplier);
        }

        for (LocalDate l: datesReserve) {
            OrderDate orderDate = new OrderDate();
            orderDate.setDate(l);
            for (Supplier su: suppliers) {
                for (SoldProduct s : soldProducts) {
                    if (s.getDateGenerated().equals(l) && su.getId() == s.getSupplier().getId()) {
                        orderDate.setSupplier(su);
                        orderDate.getProducts().add(s);
                    }


                }
            }
            orderDates.add(orderDate);
        }
        return orderDates;
    }
    public List<SellOrder> getSellOrders(){
        MongoCursor<Document> allOrdersResult =  orderCollection.find().iterator();
        ArrayList<SellOrder> orders = new ArrayList<>();
        while (allOrdersResult.hasNext()){
            Document document = allOrdersResult.next();
            SellOrder sellOrder = new SellOrder();
            sellOrder.setId(Integer.parseInt(document.get("id").toString()));
            sellOrder.setDate(document.getDate("date"));
            sellOrder.setSupplierId(document.getInteger("supplierId"));
            Set<SoldProduct> soldProducts = new HashSet<>();
            List<Document> documentList = (List<Document>) document.get("soldProducts");
            for (Document d: documentList) {
                SoldProductDto soldParse = new SoldProductDto();
                soldParse = new Gson().fromJson(d.toJson(), SoldProductDto.class);
                SoldProduct soldProduct = new SoldProduct();
                soldProduct.setProduct(ProductService.getInstance().getProductById(soldParse.getProductId()));
                soldProduct.setPrice(soldParse.getPrice());
                soldProduct.setQuantity(soldParse.getQuantity());
                soldProducts.add(soldProduct);
            }
            sellOrder.setSoldProducts(soldProducts);
            sellOrder.setTotalAmount(Float.parseFloat(document.getDouble("total").toString()));
            orders.add(sellOrder);
        }
        return orders;
    }



    public SellOrder generateReceipt(Supplier supplier, Set<SoldProduct> soldProduct, Date orderDate) {
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
        order.put("soldProducts", productsOrder);
        order.put("total", totalAmount);
        orderCollection.insertOne(order);
        sellOrders.setId(Integer.parseInt(order.get("id").toString()));
        sellOrders.setDate(orderDate);
        sellOrders.setSoldProducts(soldProduct);
        sellOrders.setSupplierId(supplier.getId());
        return sellOrders;

    }
}
