package nl.esciencecenter.praline.network;

import org.junit.Test;

import static org.junit.Assert.*;

public class WebServerTest {
    private final int nrThreads = 2;
    private WebServer server;

    public WebServerTest() {
        server = new WebServer(nrThreads);
        server.run();
    }
}