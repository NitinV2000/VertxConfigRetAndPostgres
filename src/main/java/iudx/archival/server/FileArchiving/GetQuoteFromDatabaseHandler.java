package iudx.archival.server.FileArchiving;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;

public class GetQuoteFromDatabaseHandler implements Handler<RoutingContext> {

  private final Pool db;

  public GetQuoteFromDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {

Map<String, Object> parameters = Collections.singletonMap("id", 3);

SqlTemplate
  .forUpdate(db, "DELETE FROM public.records WHERE id=#{id}")
  .execute(parameters)
  .onSuccess(v -> {
    System.out.println("Successful delete");
  });

//     Map<String, Object> parameters = Collections.singletonMap("id", 1);

// SqlTemplate
//   .forQuery(db, "SELECT * FROM public.records WHERE id=#{id}")
//   .execute(parameters)
//   .onSuccess(users -> {
//     users.forEach(row -> {
//       System.out.println(row.getInteger("archievalInterval") + " " + row.getString("storageType"));
//     });
//   });

// Map<String, Object> parameters = new HashMap<>();
// parameters.put("id", 3);
// parameters.put("archievalInterval", 6);
// parameters.put("storageType", "S3");

// SqlTemplate
//   .forUpdate(db, "INSERT INTO public.records VALUES (#{id},#{archievalInterval},#{storageType})")
//   .execute(parameters)
//   .onSuccess(v -> {
//     System.out.println("Successful update");
//   });

  }
}
