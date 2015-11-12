package de.huehnken.concurrency;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;

/**
 * A stateless verticle that can be deployed multiple times,
 * without identity
 */
public class Task extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {
        EventBus eventBus = vertx.eventBus();
        HttpClient httpClient = vertx.createHttpClient();

        MessageConsumer<JsonObject> consumer = eventBus.consumer("definer.task");
        consumer.handler(message -> {
            httpClient.getNow(message.body().getString("host"),
                    message.body().getString("path"), httpClientResponse ->
            {
                httpClientResponse.bodyHandler(buffer -> {
                    eventBus.send("collector",
                            new JsonObject()
                                    .put("numResults", message.body().getInteger("numResults"))
                                    .put("key", message.body().getString("key"))
                                    .put("definition", buffer.getString(0, buffer.length())));
                });
            });
        });

        startFuture.complete();
    }

}
