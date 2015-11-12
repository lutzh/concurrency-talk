package de.huehnken.concurrency;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class VertxApp {


    public static void main(String[] args) {
        final String searchTerm = "Geek";
        final Map<String, VertxURL> sources;

        Config conf = ConfigFactory.load();
        final String merriamWebsterApiKey = conf.getString("mw.key");
        sources = new HashMap<>();
        sources.put("Merriam Webster", new VertxURL("www.dictionaryapi.com", "/api/v1/references/collegiate/xml/{0}?key=" + merriamWebsterApiKey));
        sources.put("Wiktionary", new VertxURL("en.wiktionary.org", "/w/api.php?format=xml&action=query&rvprop=content&prop=revisions&redirects=1&titles={0}"));
        sources.put("Urban Dictionary", new VertxURL("api.urbandictionary.com", "/v0/define?term={0}"));

        Vertx vertx = Vertx.vertx();
        EventBus eventBus = vertx.eventBus();
        DeploymentOptions options = new DeploymentOptions().setInstances(3);

        vertx.deployVerticle("de.huehnken.concurrency.Collector",
                completionMsg1 -> {
                    vertx.deployVerticle("de.huehnken.concurrency.Task", options,
                            completionMsg2 -> {
                                for (String currentKey : sources.keySet()) {
                                    eventBus.send("definer.task",
                                            new JsonObject()
                                                    .put("numResults", sources.size())
                                                    .put("key", currentKey)
                                                    .put("host", sources.get(currentKey).host)
                                                    .put("path", MessageFormat.format(sources.get(currentKey).path, searchTerm)));
                                }
                            }
                    );
                });
    }
}
