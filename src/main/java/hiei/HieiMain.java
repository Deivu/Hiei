package hiei;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class HieiMain {
    public static void main(String[] args) throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        System.setProperty("vertx.disableDnsResolver", "true");
        new HieiServer()
                .printWelcome()
                .buildRest()
                .startServer()
                .scheduleTasks();
    }
}
