package org.workspace7.openwhisk;

import com.google.gson.JsonObject;
import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

import java.nio.charset.Charset;
import java.util.Base64;


public class InfinispanFeedAction {

  public static JsonObject main(JsonObject request) {
    JsonObject response = new JsonObject();
    //TODO validation
    try {
      InfinispanProvider provider = Feign.builder()
        .client(new OkHttpClient())
        .decoder(new GsonDecoder())
        .encoder(new GsonEncoder())
        .logger(new Slf4jLogger(InfinispanProvider.class))
        .target(InfinispanProvider.class, "http://infinispan-feed-provider.myproject:8080");
      response.addProperty("done", true);
      if (request.get("lifecycleEvent").getAsString().equalsIgnoreCase("CREATE")) {
        System.out.println("CREATE TRIGGER");
        response = provider.addListenerToTrigger(request);
        System.out.println("RESPONSE:" + response);
      } else if (request.get("lifecycleEvent").getAsString().equalsIgnoreCase("DELETE")) {
        System.out.println("DELETE TRIGGER:");
        String triggerName = new String(Base64.getEncoder().encode(request.get("triggerName")
          .getAsString().getBytes(Charset.forName("US-ASCII"))));
        response = provider.deleteListenerFromTrigger(triggerName);
      }
    } catch (Exception e) {
      response.addProperty("done", false);
      response.addProperty("error", e.getMessage());
    }
    return response;
  }


  interface InfinispanProvider {
    @RequestLine("POST /api/feed/listener")
    @Headers("Content-Type: application/json")
    JsonObject addListenerToTrigger(JsonObject request);

    @RequestLine("DELETE /api/feed/listener/{triggerName}")
    JsonObject deleteListenerFromTrigger(@Param("triggerName") String triggerName);
  }
}
