import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinVelocity;
import org.eclipse.jetty.server.*;
public class Main {
    public static void main(String[] args){

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
            config.registerPlugin(new RouteOverviewPlugin("/routes"));
            config.enableCorsForAllOrigins();
            JavalinRenderer.register(JavalinVelocity.INSTANCE, ".vm");
        });
    }
}
