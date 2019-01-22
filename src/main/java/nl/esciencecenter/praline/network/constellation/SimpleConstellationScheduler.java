package nl.esciencecenter.praline.network.constellation;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Vector;
import java.util.function.Function;

import com.sun.org.apache.regexp.internal.RE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.constellation.*;
import ibis.constellation.util.SingleEventCollector;

public class SimpleConstellationScheduler<A,B>  {

    final static Logger logger = LoggerFactory.getLogger(SimpleConstellationScheduler.class);
    
    ArrayList<B> mapConstellation(Constellation c, Context ctx, int threshhold, Function<A, B> compute, ArrayList<A> in){
        try {
            logger.debug("Got input size: " + in.size());
	    
            SingleEventCollector eventCollector = new SingleEventCollector(ctx);
            ActivityIdentifier id = c.submit(eventCollector);
            c.submit(new DivideMapActivity<A,B>(threshhold,id,Side.LEFT,ctx,compute,in));
	    
            logger.debug("Waiting for toplevel event {}", id);
            Event a = eventCollector.waitForEvent();
            logger.debug("Got toplevel event");

	    ResWithSide<ArrayList<B>> res = (ResWithSide)a.getData();
            return res.a;
        } catch (NoSuitableExecutorException e) {
            e.printStackTrace();
            throw new Error("Cannot find suitable exectutor");
        }
    }
}
