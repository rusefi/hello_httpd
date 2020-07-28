package com.rusefi;

import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.*;
import org.takes.rs.RsJson;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Hello {


    public static void main(String[] args) throws IOException {
        AtomicInteger counter = new AtomicInteger();

        int httpPort = 443;
        System.out.println("hello on port " + 443);


        Take forkTake = new TkFork(
                new FkRegex("/", "hello takes"),
                new FkRegex("/counter", (Take) req -> {
                    JsonObjectBuilder applicationObject = Json.createObjectBuilder();
                    applicationObject.add("counter", counter.incrementAndGet());
                    return new RsJson(applicationObject.build());
                })
        );

        // yes, I start insecure http on port 443, that's the place
        Front frontEnd = new FtBasic(new BkParallel(new BkSafe(new BkBasic(forkTake)), 4), httpPort);
        frontEnd.start(Exit.NEVER);
    }
}
