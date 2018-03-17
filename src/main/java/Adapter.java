import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Adapter {
    ExecutorService executors;

    public Adapter() {
        executors = Executors.newFixedThreadPool(6);
    }
}
