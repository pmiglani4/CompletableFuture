import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("In asynctask, Thread is: " + Thread.currentThread());
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Some Result";
        }).thenApply(result -> {
            /*
              Executed in the same thread where the supplyAsync() task is executed
              or in the main thread If the supplyAsync() task completes immediately (Remove sleep() call to verify)
            */
            System.out.println("In Callback func, Thread is: " + Thread.currentThread());
            return "Processed Result";
        });
        System.out.println(future.get());
    }
}
