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
    public List<String> getSubBreeds(String breed) {
        // Null breeds are never cached; defer to underlying fetcher so tests can observe the call.
        if (breed == null) {
            callsMade++;
            // Let the underlying fetcher decide how to handle null (likely throws BreedNotFoundException).
            return new ArrayList<>(fetcher.getSubBreeds(null));
        }

        String key = breed.trim().toLowerCase(Locale.ROOT);

        // Cache hit: return a defensive copy to protect internal cache.
        List<String> cached = cache.get(key);
        if (cached != null) {
            return new ArrayList<>(cached);
        }

        // Cache miss: delegate to underlying fetcher and record the call.
        callsMade++;
        List<String> result = fetcher.getSubBreeds(breed); // may throw BreedNotFoundException

        // Store an unmodifiable copy to prevent accidental external mutation through references.
        cache.put(key, Collections.unmodifiableList(new ArrayList<>(result)));

        // Return a fresh copy to callers.
        return new ArrayList<>(result);
    }

    public int getCallsMade() {
        return callsMade;
    }
}
