import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CombiningCompletableFuture {

    static String userId = "abc";

    static CompletableFuture<String> getUserDetail(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            //return UserService.getUserDetails(userId);
            return "User details-User 1";
        });
    }

    static CompletableFuture<Double> getCreditRating(String userDetails) {
        return CompletableFuture.supplyAsync(() -> {
            //return CreditRatingService.getCreditRating(userDetails);
            return 40000.0;
        });
    }
    static CompletableFuture<String> downloadWebPage(Integer webPageNumber) {
        return CompletableFuture.supplyAsync(() -> "webPage" + webPageNumber + (webPageNumber % 2 == 0 ?" CompletableFuture": " "));
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /*Combine two dependent futures using thenCompose() */
        CompletableFuture<CompletableFuture<Double>> result = getUserDetail(userId)
                .thenApply(user -> getCreditRating(userId));
        //the final result in the above case is a nested CompletableFuture.
        //To flatten the result from the CompletableFuture chain, then use thenCompose().
        CompletableFuture<Double> result1 = getUserDetail(userId)
                .thenCompose(user -> getCreditRating(user));
        System.out.println(result1.get());

        /*Combine two independent futures using thenCombine()*/
        System.out.println("Retrieving weight.");
        CompletableFuture<Double> weightInKgFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return 65.0;
        });
        System.out.println("Retrieving height.");
        CompletableFuture<Double> heightInCmFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return 177.8;
        });
        System.out.println("Calculating BMI.");
        CompletableFuture<Double> combinedFuture = weightInKgFuture
                .thenCombine(heightInCmFuture, (weightInKg, heightInCm) -> {
                    Double heightInMeter = heightInCm/100;
                    return weightInKg/(heightInMeter*heightInMeter);
                });
        System.out.println("Your BMI is - " + combinedFuture.get());
        //The callback function passed to thenCombine() will be called when both the Futures are complete.


        /*Combining multiple CompletableFutures together*/
        //CompletableFuture.allOf is used to run a List of independent futures in parallel and do something after all of them are complete
        List<CompletableFuture<String>> pageContentFutures = IntStream.rangeClosed(1,100)
                .mapToObj(webPageNumber -> downloadWebPage(webPageNumber))
                .collect(Collectors.toList());
        // Create a combined Future using allOf()
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                pageContentFutures.toArray(new CompletableFuture[pageContentFutures.size()])
        );
        // When all the Futures are completed, call `future.join()` to get their results and collect the results in a list -
        CompletableFuture<List<String>> allPageContentsFuture = allFutures.thenApply(v -> {
            return pageContentFutures.stream()
                    .map(pageContentFuture -> pageContentFuture.join())
                    .collect(Collectors.toList());
        });
        // Count the number of web pages having the "CompletableFuture" keyword.
        CompletableFuture<Long> countFuture = allPageContentsFuture.thenApply(pageContents -> {
            return pageContents.stream()
                    .filter(pageContent -> pageContent.contains("CompletableFuture"))
                    .count();
        });
        System.out.println("Number of Web Pages having CompletableFuture keyword - " +
                countFuture.get());



        /*CompletableFuture.anyOf() returns a new CompletableFuture which is completed when any of the given CompletableFutures complete, with the same result.*/
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Result of Future 1";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Result of Future 2";
        });
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Result of Future 3";
        });
        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(future1, future2, future3);
        System.out.println(anyOfFuture.get()); // Result of Future 2
    }
}
