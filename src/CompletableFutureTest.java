import java.util.concurrent.*;

public class CompletableFutureTest {

    public static void main(String args[]) throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<String>();

        /*The get() method blocks until the Future is complete.
        So, the below call will block forever because the Future is never completed.*/
        //System.out.println(completableFuture.get());

        /*CompletableFuture.complete() method to manually complete a Future*/
        completableFuture.complete("Future's Result");
        System.out.println(completableFuture.get());
        /* Subsequent calls to completableFuture.complete() will be ignored.*/

        /*Running asynchronous computation using runAsync().
        It takes a Runnable object and returns CompletableFuture<Void>*/
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            // Simulate a long-running Job
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.out.println("I'll run in a separate thread than the main thread.");
        });
        System.out.println(future.get());

        /*Run a task asynchronously and return the result using supplyAsync().
        It takes a Supplier<T> and returns CompletableFuture<T>*/
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Current Thread: "+Thread.currentThread());
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Result of the asynchronous computation";
        });
        String result = future1.get();
        System.out.println(result);

        /* CompletableFuture executes these tasks in a thread obtained from the global ForkJoinPool.commonPool().
        Create a Thread Pool and pass it to runAsync() and supplyAsync() methods to let them execute their tasks in a thread obtained from your thread pool.*/
        // static CompletableFuture<Void>  runAsync(Runnable runnable, Executor executor)
        // static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
        Executor executor = Executors.newFixedThreadPool(10);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Current Thread: "+Thread.currentThread());
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Result of the asynchronous computation";
        }, executor);
        System.out.println(future2.get());
    }
}
