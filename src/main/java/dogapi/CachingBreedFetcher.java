package dogapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {

    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        if (fetcher == null) {
            throw new IllegalArgumentException("Underlying BreedFetcher must not be null");
        }
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        if (breed == null) {
            // don't cache null keys; still count the delegation for visibility in tests
            callsMade++;
            return new ArrayList<>(fetcher.getSubBreeds(null));
        }

        String key = breed.trim().toLowerCase(Locale.ROOT);

        List<String> cached = cache.get(key);
        if (cached != null) {
            // return a defensive copy
            return new ArrayList<>(cached);
        }

        callsMade++;
        List<String> result = fetcher.getSubBreeds(breed); // may throw BreedNotFoundException

        // Cache successful results (store unmodifiable copy internally)
        cache.put(key, Collections.unmodifiableList(new ArrayList<>(result)));

        // Return a defensive copy
        return new ArrayList<>(result);
    }

    public int getCallsMade() {
        return callsMade;
    }
}
