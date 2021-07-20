package Controllers;

import Models.Dto.AvailableDays;
import Models.Dto.ProductCart;
import Models.Product;
import Models.Warehouse;
import Services.ProductService;
import com.google.gson.Gson;
import io.javalin.Javalin;

import java.util.*;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ProductController {
    private final Javalin app;

    public ProductController(Javalin app) {
        this.app = app;
    }

    public void applyRoutes(){
        app.routes(() ->{
           path("/product", ()-> {
               get("/", ctx->{
                   List<AvailableDays> availableProducts = new ArrayList<>();
                   List<Product> products = new ArrayList<>();
                   Map<String, Object> model = new HashMap<>();
                   products = ProductService.getInstance().getAll();
                   for (Product product: products) {
                       AvailableDays ava = ProductService.getInstance().getAvailableDays(product.getId(), new Date());
                       availableProducts.add(ava);
                   }

                   model.put("products",availableProducts);
                   ctx.render("/public/mainPage.vm",model);
               });
               get("/:id", ctx ->{
                   Product product = ProductService.getInstance().getProductById(ctx.pathParam("id",String.class).get());
                   Map<String,Object> model = new HashMap<>();
                   model.put("product",product);
                   ctx.render("/public/addToCart.vm", model);
               });
               post("add/:productId/:warehouseId", ctx->{
                   Product product = ProductService.getInstance().getProductById(ctx.pathParam("productId",String.class).get());
                   ProductCart productCart = new ProductCart();
                   productCart.setProduct(product);
                   productCart.setQuantity(ctx.formParam("quantity",Integer.class).get());
                   for (Warehouse wh: product.getAvailability()) {
                       if(wh.getIdWarehouse() == ctx.pathParam("warehouseId",Integer.class).get()){
                           productCart.setIdWarehouse(wh.getIdWarehouse());
                       }
                   }
                   ProductService.getCart().add(productCart);
                   //System.out.println(new Gson().toJson(ProductService.getCart().get(0)));
                   ctx.redirect("/");
               });
               get("/cart/view",ctx->{
                   Map<String,Object> model = new HashMap<>();
                   model.put("products",ProductService.getCart());
                   ctx.render("/public/Order.vm",model);
               });
            });
        });
    }
}
