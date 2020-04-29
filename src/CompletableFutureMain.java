import java.util.concurrent.*;

public class CompletableFutureMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /*Transforming and acting on a CompletableFuture*/
        /*The CompletableFuture.get() method is blocking. For building asynchronous systems we should be able
         to attach a callback to the CompletableFuture which should automatically get called when the Future completes.
         Use thenApply(), thenAccept() and thenRun() methods to attach a callback to the CompletableFuture
         */

        //thenApply().  It takes a Function<T,R> as an argument
        CompletableFuture<String> welcomeText = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Rajeev";
        }).thenApply(name -> {
            return "Hello " + name;
        }).thenApply(greeting -> {
            return greeting + ", Welcome to the CalliCoder Blog";
        });
        System.out.println(welcomeText.get());

        /*thenAccept() takes a Consumer<T> and returns CompletableFuture<Void>.
        It has access to the result of the CompletableFuture on which it is attached.*/
        CompletableFuture.supplyAsync(() -> {
            return "{\"product_name\":\"ABC\"}";
        }).thenAccept(productJsonStr -> {
            System.out.println("Got product detail from remote service " + productJsonStr);
        });

        /*thenRun() takes a Runnable and returns CompletableFuture<Void>.
        It doesn’t even have access to the Future’s result.*/
        CompletableFuture.supplyAsync(() -> {
            return "{\"product_name\":\"ABC\"}";
        }).thenRun(() -> {
            System.out.println("Got product detail from remote service but not having access to result");
        });

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

        /*pass an Executor to the thenApplyAsync() callback then the task will be executed in a thread obtained from the Executor’s thread pool.*/

        Executor executor = Executors.newFixedThreadPool(2);
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("In asynctask, Thread is: " + Thread.currentThread());
            return "Some result";
        }).thenApplyAsync(result -> {
            // Executed in a thread obtained from the executor
            System.out.println("In Callback func, Thread is: " + Thread.currentThread());
            return "Processed Result";
        }, executor);
        System.out.println(future1.get());

    }
}
