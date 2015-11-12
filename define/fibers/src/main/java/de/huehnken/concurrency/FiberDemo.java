package de.huehnken.concurrency;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.httpclient.FiberHttpClientBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


class FiberDemo {

    /* The store hash table is shared mutable state! */
    private final Map<String, String> store = new ConcurrentHashMap<>();
    private final Map<String, String> sources;

    public FiberDemo() throws MalformedURLException {
        Config conf = ConfigFactory.load();
        final String merriamWebsterApiKey = conf.getString("mw.key");
        sources = new HashMap<>();
        sources.put("Merriam Webster", "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/{0}?key=" + merriamWebsterApiKey);
        sources.put("Wiktionary", "https://en.wiktionary.org/w/api.php?format=xml&action=query&rvprop=content&prop=revisions&redirects=1&titles={0}");
        sources.put("Urban Dictionary", "http://api.urbandictionary.com/v0/define?term={0}");

    }

    public void define(final String searchTerm) {
        for (String currentKey : sources.keySet()) {
            new RetrieveInfo(currentKey, sources.get(currentKey), searchTerm).start();
        }
    }

    /**
     * Here we access the shared "store" hash map.
     * Since it's a concurrent hash map, and we don't have other shared state,
     * we refrain from making it "synchronized" - but if you where to use non thread-safe
     * structures, you'd have to.
     * This method is working on shared mutual state - this is what we look to avoid!
     * @param key
     * @param result
     */
    void addResult(final String key, final String result) {
        store.put(key, result);
        if (store.size() == sources.size()) {
            for (String currentKey : store.keySet()) {
                System.out.println("=========================================");
                System.out.println(currentKey);
                System.out.println("=========================================");
                System.out.println(store.get(currentKey));
                System.out.println("=========================================");
            }
            System.exit(0);
        }
    }

    class RetrieveInfo extends Fiber<Void> {
        private final String key;
        private final String url;
        private final String searchTerm;

        RetrieveInfo(final String key, String url, String searchTerm) {
            this.key = key;
            this.url = url;
            this.searchTerm = searchTerm;
        }

        @Override
        public Void run() {
            final CloseableHttpClient client = FiberHttpClientBuilder.create().build();
            try {
                String response = client.execute(new HttpGet(MessageFormat.format(url, searchTerm)), new BasicResponseHandler());
                addResult(key, StringUtils.abbreviate(response, 1000));
            } catch (IOException iox) {
                iox.printStackTrace();
            }
            return null;
        }
    }


    public static void main(String[] argv) throws Exception {
        new FiberDemo().define("Nerd");
    }

}
