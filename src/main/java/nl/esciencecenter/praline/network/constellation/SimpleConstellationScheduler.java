package nl.esciencecenter.praline.network.constellation;

import com.sun.org.apache.regexp.internal.RE;
import ibis.constellation.*;
import ibis.constellation.util.SingleEventCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Vector;
import java.util.function.Function;

public class SimpleConstellationScheduler<A,B>  {

    final static Logger logger = LoggerFactory.getLogger(DivideMapActivity.class);

    ArrayList<B> mapConstellation(Constellation c, Context ctx, int threshhold, Function<A, B> compute, ArrayList<A> in){

        try {
            SingleEventCollector eventCollector = new SingleEventCollector(ctx);
            ActivityIdentifier id = c.submit(eventCollector);
            logger.info(" " + in.size());
            c.submit(new DivideMapActivity<>(threshhold,id,Side.LEFT,ctx,compute,in));
            logger.info("Waiting for toplevel event {}", id);
            Event a = eventCollector.waitForEvent();
            logger.info("Got toplevel event ");
            ResWithSide<ArrayList<B>> res = (ResWithSide)a.getData();
            return res.a;

        } catch (NoSuitableExecutorException e) {
            e.printStackTrace();
            throw new Error("Cannot find suitable exectutor");
        }

    }


}
