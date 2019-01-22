package nl.esciencecenter.praline.network.constellation;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.constellation.*;

public class SimpleConstellationRunner<A,B> extends Thread {

    final static Logger logger = LoggerFactory.getLogger(SimpleConstellationRunner.class);
    
    public ArrayList<B> res;

    public SimpleConstellationRunner(){
        res = null;
    }

    public void run(Constellation c, Function<A, B> compute, ArrayList<A> in){
	logger.debug("Start running a Constellation task");
	
        Timer t = c.getOverallTimer();
        int i = t.start();

	Context ctxt = new Context("MSA");
	res = new SimpleConstellationScheduler<A,B>().
	    mapConstellation(c, ctxt, 1, compute , in);

	// TODO: why is this timer here and why is it inside the done()?
	// Does this make sense?
	t.stop(i);
        c.done();
    }
}
