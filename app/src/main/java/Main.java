import Models.*;
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
import org.joda.time.LocalDate;

import java.security.ProtectionDomain;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {

            config.enableCorsForAllOrigins();
            JavalinRenderer.register(JavalinVelocity.INSTANCE, ".vm");
        });

       Product product = ProductService.getInstance().getProductById("MOU-001");
    }
}
