package iudx.archival.server.FileArchiving;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;

public class GetAssetsFromDatabaseHandler implements Handler<RoutingContext> {

    private final Pool db;
  
    public GetAssetsFromDatabaseHandler(final Pool db) {
      this.db = db;
    }
  
    @Override
    public void handle(final RoutingContext context) {
    db.query("SELECT * FROM public.records").execute()
                        .onSuccess(rows -> {
                            for (Row row : rows) {
                                System.out.println("row = " + row.toJson());
                            }});
  
  }
}
