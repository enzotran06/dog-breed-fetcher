package dogapi;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String breed = "hound";
        BreedFetcher breedFetcher = new CachingBreedFetcher(new BreedFetcherForLocalTesting());
        try {
            int result = getNumberOfSubBreeds(breed, breedFetcher);
            System.out.println(breed + " has " + result + " sub breeds");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        breed = "cat";
        try {
            int result2 = getNumberOfSubBreeds(breed, breedFetcher);
            System.out.println(breed + " has " + result2 + " sub breeds");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Return the number of sub breeds that the given dog breed has according to the
     * provided fetcher.
     * @param breed the name of the dog breed
     * @param breedFetcher the breedFetcher to use
     * @return the number of sub breeds. Zero should be returned if there are no sub breeds
     * returned by the fetcher
     */
    public static int getNumberOfSubBreeds(String breed, BreedFetcher breedFetcher) {
        if (breedFetcher == null) {
            throw new IllegalArgumentException("breedFetcher must not be null");
        }
        // Delegate to the provided fetcher and interpret the result per the docs.
        List<String> subBreeds;
        try {
            subBreeds = breedFetcher.getSubBreeds(breed);
        } catch (BreedFetcher.BreedNotFoundException e) {
            // For Main.getNumberOfSubBreeds, convert checked exception into an unchecked one
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        if (subBreeds == null) {
            return 0;
        }
        return subBreeds.size();
    }
}