package Controllers;

import Models.Dto.OrderDate;
import Models.Dto.ProductCart;
import Models.Product;
import Models.SellOrder;
import Services.ProductService;
import Services.SellOrderService;
import io.javalin.Javalin;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static io.javalin.apibuilder.ApiBuilder.*;
public class OrderController {
    private final Javalin app;

    public OrderController(Javalin app) {
        this.app = app;
    }

    public void applyRoutes(){
        app.routes(()->{
           path("/order", () ->{
               get("/eliminate/:id", ctx->{
                   String id = ctx.pathParam("id",String.class).get();
                   ProductService.getCart().removeIf(productCart -> productCart.getProduct().getId().equals(id));
                   ctx.redirect("/product/cart/view");
               });
               post("/genOrder",ctx -> {
                   String date = ctx.formParam("requiredDate",String.class).get();
                   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                   Date mydate = format.parse(date);
                   LocalDate localDate = LocalDate.parse(date);

                   List<OrderDate> orderDates = SellOrderService.getInstance().buildOrders(localDate,ProductService.getCart());
                   List<SellOrder> sellOrders = new ArrayList<>();
                   for (OrderDate orderDate: orderDates) {
                       Date date1 = format.parse(orderDate.getDate().toString());
                       SellOrderService.getInstance().generateReceipt(orderDate.getSupplier(), orderDate.getProducts(),date1);
                   }
                   ctx.redirect("/order/genOrder/view");
               });
               get("/genOrder/view", ctx -> {
                   Map<String, Object> model = new HashMap<>();
                   model.put("sellOrders",SellOrderService.getInstance().getSellOrders());

                   ctx.render("/public/SellOrder.vm", model);
               });
           });
        });
    }
}
