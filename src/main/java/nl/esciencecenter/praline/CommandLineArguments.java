package nl.esciencecenter.praline;

import com.beust.jcommander.Parameter;

public class CommandLineArguments {
    @Parameter(names = {"-server_threads"}, description = "Number of threads for the web server")
    private Integer serverThreads = 42;

    public int getNrServerThreads() {
        return serverThreads;
    }
}
