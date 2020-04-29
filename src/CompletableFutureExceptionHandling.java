import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.activeCount;
import static java.lang.Thread.sleep;

public class CompletableFutureExceptionHandling {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /*Handle exceptions using exceptionally() callback*/
        Integer age = 10;
        CompletableFuture<String> maturityFuture = CompletableFuture.supplyAsync(() -> {
            if(age < 0) {
                throw new IllegalArgumentException("Age can not be negative");
            }
            if(age > 18) {
                return "Adult";
            } else {
                return "Child";
            }
        }).thenApply(agegroup -> {
            System.out.println("Hi " + agegroup);
            return agegroup;
        })
            .exceptionally(ex -> {
                System.out.println("Oops! We have an exception - " + ex.getMessage());
                return "Unknown!";
            });
        System.out.println("Maturity : " + maturityFuture.get());


        /*Handle exceptions using the generic handle() method and this method is called whether or not an exception occurs.*/
        Integer age1 = -1;
        CompletableFuture<String> maturityFuture1 = CompletableFuture.supplyAsync(() -> {
            if(age1 < 0) {
                throw new IllegalArgumentException("Age can not be negative");
            }
            if(age1 > 18) {
                return "Adult";
            } else {
                return "Child";
            }
        }).handle((res, ex) -> {
            if(ex != null) {
                System.out.println("Oops! We have an exception - " + ex.getMessage());
                return "Unknown!";
            }
            return res;
        });
        System.out.println("Maturity : " + maturityFuture1.get());

        CompletableFuture<Integer> futureObj = new CompletableFuture<>();
        //futureObj.completeOnTimeout(200, 3, TimeUnit.SECONDS);
        futureObj.orTimeout(3, TimeUnit.SECONDS);
        sleep(2000);
        process(futureObj);
        futureObj.complete(2);
        //futureObj.completeExceptionally(new RuntimeException("Something went wrong"));
        sleep(5000);
    }

    private static void process(CompletableFuture<Integer> futureObj) {
        futureObj.exceptionally(throwable -> handle(throwable))
                .thenApply(data ->data * 2)
                .thenApply(data->data+1)
                .thenAccept(System.out::println);
    }

    private static Integer handle(Throwable throwable) {
        System.out.println("Error:"+throwable);
        //return 100;
        throw new RuntimeException("this is beyond repair");
    }
}
