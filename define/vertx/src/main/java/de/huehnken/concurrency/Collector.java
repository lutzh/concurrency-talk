package de.huehnken.concurrency;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A stateful verticle, that you only deploy once, or refer to
 * by identity
 */
public class Collector extends AbstractVerticle {

    private final Map<String, String> store = new HashMap<>();

    @Override
    public void start(Future<Void> startFuture) {
        EventBus eventBus = vertx.eventBus();

        MessageConsumer<JsonObject> consumer = eventBus.consumer("collector");
        consumer.handler(message -> {
            final int numResults = message.body().getInteger("numResults");

            store.put(message.body().getString("key"), message.body().getString("definition"));

            if (store.size() == numResults) {
                for (String currentKey : store.keySet()) {
                    System.out.println("=========================================");
                    System.out.println(currentKey);
                    System.out.println("=========================================");
                    System.out.println(store.get(currentKey));
                    System.out.println("=========================================");
                }
                vertx.close();
            }
        });

        startFuture.complete();
    }
}