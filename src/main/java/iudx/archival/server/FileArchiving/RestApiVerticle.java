package iudx.archival.server.FileArchiving;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

public class RestApiVerticle extends AbstractVerticle{

    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RestApiVerticle());
    }



    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        vertx.createHttpServer()
             .requestHandler(router)
             .listen(8091);
        final Pool db = DBPool.createPgPool(vertx);
        vertx.deployVerticle(new SchedulingVerticle(db));
        router.get("/pg/assets").handler(new GetAssetsFromDatabaseHandler(db));
        router.get("/pg/quotes").handler(new GetQuoteFromDatabaseHandler(db));
    }
    
}
