package nl.esciencecenter.praline;

import com.beust.jcommander.Parameter;

public class CommandLineArguments {
    @Parameter(names = {"-server_threads"}, description = "Number of threads for the web server")
    private Integer serverThreads = 42;
    @Parameter(names = {"-expected_sequences"}, description = "Number of sequences to expect")
    private Integer expectedSequences = -1;

    public int getNrServerThreads() {
        return serverThreads;
    }

    public int getNrExpectedSequences() {
        return expectedSequences;
    }
}
