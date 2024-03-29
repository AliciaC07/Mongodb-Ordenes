import Controllers.OrderController;
import Controllers.ProductController;
import Models.*;
import Models.Dto.AvailableDays;
import Models.Dto.OrderDate;
import Models.Dto.ProductCart;
import Services.InventoryMovementService;
import Services.ProductService;
import Services.SellOrderService;
import Services.SupplierService;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinVelocity;
import org.eclipse.jetty.server.*;

import java.security.ProtectionDomain;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static List<ProductCart> cart;
    public static void main(String[] args) {
        cart = new ArrayList<>();

        Javalin app = Javalin.create(config -> {
            config.registerPlugin(new RouteOverviewPlugin("/public"));
            config.addStaticFiles("/public");
            config.enableCorsForAllOrigins();
            JavalinRenderer.register(JavalinVelocity.INSTANCE, ".vm");
        }).start(7001);
        new ProductController(app).applyRoutes();
        new OrderController(app).applyRoutes();
        app.get("/",ctx->{
           ctx.redirect("product");
        });

       /*Product product = ProductService.getInstance().getProductById("MON-001");
       Product p = ProductService.getInstance().getProductById("MON-002");
       Product p1 = ProductService.getInstance().getProductById("MOUS-001");
       List<Product> products = new ArrayList<>();
       products.add(product);
       products.add(p);
        products.add(p1);
       List<OrderDate> orderDates = new ArrayList<>();
       LocalDate date = LocalDate.now().plusDays(6);
       orderDates = SellOrderService.getInstance().buildOrders(date, products);
        for (OrderDate o: orderDates) {
            SellOrderService.getInstance().generateReceipt(o.getSupplier(), o.getProducts(), Date.from(o.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }*/





    }
}
