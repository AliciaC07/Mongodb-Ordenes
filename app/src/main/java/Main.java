import Models.Dto.OrderDate;
import Models.Product;
import Services.ProductService;
import Services.SellOrderService;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinVelocity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {

            config.enableCorsForAllOrigins();
            JavalinRenderer.register(JavalinVelocity.INSTANCE, ".vm");
        });

       Product product = ProductService.getInstance().getProductById("MON-001");
       Product p = ProductService.getInstance().getProductById("MON-002");
       Product p1 = ProductService.getInstance().getProductById("MOUS-001");
       Product product1 = ProductService.getInstance().getProductById("MON-003");
       List<Product> products = new ArrayList<>();
       products.add(product);
       products.add(p);
        products.add(p1);
       products.add(product1);
       List<OrderDate> orderDates = new ArrayList<>();
       LocalDate date = LocalDate.now().plusDays(6);
       orderDates = SellOrderService.getInstance().buildOrders(date, products);
        for (OrderDate o: orderDates) {
            SellOrderService.getInstance().generateReceipt(o.getSupplier(), o.getProducts(), Date.from(o.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }





    }
}
