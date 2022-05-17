package iudx.archival.server.FileArchiving;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.SqlTemplate;

public class SchedulingVerticle extends AbstractVerticle{

  private final Pool db;

  public SchedulingVerticle(final Pool db) {
    this.db = db;
  }
  
  @Override
  public void start() throws Exception {
        String path_to_config = System.getProperty("fileapi.config", "configs/config-file.json");
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
            .setType("file")
            .setFormat("json")
            .setConfig(new JsonObject().put("path", path_to_config));
        
        ConfigRetriever retriever = ConfigRetriever.create(vertx, 
                new ConfigRetrieverOptions().addStore(fileStore));
        retriever.getConfig(
            config -> {
                if (config.failed()) {
                    System.out.println("Fail");
                } else {
                    JsonObject json = config.result();
                    JsonArray jsonArray = json.getJsonArray("records");
                    for(int i=0;i<jsonArray.size();i++){
                        int id = jsonArray.getJsonObject(i).getInteger("id");
                        int archievalInterval = jsonArray.getJsonObject(i).getInteger("archieval-Interval");
                        String storageType = jsonArray.getJsonObject(i).getString("storage-type");
                        System.out.println(id+" "+archievalInterval+" "+storageType);
                    }
                }
            }
        );

        retriever.listen(change -> {
          JsonObject previous = change.getPreviousConfiguration();
          HashSet<Integer> h = new HashSet<>();
          JsonArray jsonArrayPrev = previous.getJsonArray("records");
                    for(int i=0;i<jsonArrayPrev.size();i++){
                        int id = jsonArrayPrev.getJsonObject(i).getInteger("id");
                        h.add(id);
                    }
          JsonObject conf = change.getNewConfiguration();
          JsonArray jsonArrayNew = conf.getJsonArray("records");
                    for(int i=0;i<jsonArrayNew.size();i++){
                        int id = jsonArrayNew.getJsonObject(i).getInteger("id");
                        int archievalInterval = jsonArrayNew.getJsonObject(i).getInteger("archieval-Interval");
                        String storageType = jsonArrayNew.getJsonObject(i).getString("storage-type");
                        if(h.contains(id)==false){
                            Map<String, Object> parameters = new HashMap<>();
parameters.put("id", id);
parameters.put("archievalInterval", archievalInterval);
parameters.put("storageType", storageType);

SqlTemplate
  .forUpdate(db, "INSERT INTO public.records VALUES (#{id},#{archievalInterval},#{storageType})")
  .execute(parameters)
  .onSuccess(v -> {
    System.out.println("Successful update");
  });
                        }
                        else if(h.contains(id)){
                            h.remove(id);
                        }
                    }
                    Iterator<Integer> it = h.iterator();
        while (it.hasNext()) {
            Map<String, Object> parameters = Collections.singletonMap("id", it.next());

SqlTemplate
  .forUpdate(db, "DELETE FROM public.records WHERE id=#{id}")
  .execute(parameters)
  .onSuccess(v -> {
    System.out.println("Successful delete");
  });
        }
      });
  }
}
