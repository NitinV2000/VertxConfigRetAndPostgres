package iudx.archival.server.FileArchiving;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class DBPool {
    public static Pool createPgPool(final Vertx vertx) {
        final var connectOptions = new PgConnectOptions()
        .setPort(5432)
        .setHost("localhost")
        .setDatabase("archival-data")
        .setUser("postgres")
        .setPassword("secret");
    
        var poolOptions = new PoolOptions()
          .setMaxSize(4);
    
        return PgPool.pool(vertx, connectOptions, poolOptions);
      }
}
