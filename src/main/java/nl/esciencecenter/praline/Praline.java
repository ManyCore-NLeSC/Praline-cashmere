package nl.esciencecenter.praline;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import ibis.constellation.*;
import ibis.cashmere.constellation.Cashmere;

import nl.esciencecenter.praline.network.WebServer;


public class Praline {

    static String HOSTNAME = "localhost";

    static void setHostName(){
        try{
            Runtime r = Runtime.getRuntime();
            Process p = r.exec("hostname");
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            HOSTNAME = b.readLine();
            b.close();
        } catch (IOException | InterruptedException e) {
            throw new Error("Could not obtain hostname! " + e.getMessage());
        }
    }
    final static int threads = 1;
    final static Logger logger = LoggerFactory.getLogger(Praline.class);
    public static void main(String [] args) throws InterruptedException {
        try {
            setHostName();
            // Command line arguments
            CommandLineArguments arguments = new CommandLineArguments();
            JCommander.newBuilder().addObject(arguments).build().parse(args);
            Context ctxt = new Context("MSA");
            ConstellationConfiguration[] cons = new ConstellationConfiguration[threads];
            for(int i = 0 ; i < threads; i++){
                cons[i] = new ConstellationConfiguration(ctxt);
            }

	    Cashmere.initialize(cons);
            Constellation c = Cashmere.getConstellation();
            c.activate();
            if (c.isMaster()) {
		String dir = System.getenv("PRALINE_CASHMERE_DIR");
                File f = new File(dir + "/" + "bowbeforeme");
                f.createNewFile();
                PrintWriter out = new PrintWriter(f);
                out.print(HOSTNAME);
                out.close();
                logger.info("I am the Master! Bow before me and face {}",HOSTNAME);
                WebServer server;
                // Initialize web server
                server = new WebServer(arguments.getNrServerThreads(),c);

                server.run();

                // Wait until termination
                synchronized (server) {
                    server.wait();
                }
                logger.info("DONE!");
		// The actual c.done is done in SimpleConstellationRunner
                // c.done();
                // Clean up
                server.close();
            } else {
                logger.info("I am a slave! {}",HOSTNAME);
                c.done();
            }


        } catch (IOException |  ConstellationCreationException e) {
            e.printStackTrace();
        }




        System.exit(0);
    }
}
