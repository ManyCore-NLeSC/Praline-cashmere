package nl.esciencecenter.praline.network.constellation;

import ibis.constellation.*;

import java.util.ArrayList;
import java.util.function.Function;

public class SimpleConstellationRunner<A,B> extends Thread {

    public ArrayList<B> res;

    public SimpleConstellationRunner(){
        res = new ArrayList<>();
    }

    public void run(Function<A, B> compute, ArrayList<A> in){
        try {
            Context ctxt = new Context("MSA");
            Constellation c = ConstellationFactory.createConstellation(new ConstellationConfiguration(ctxt));
            c.activate();
            if(c.isMaster()){
                res = new SimpleConstellationScheduler<A,B>().
                        mapConstellation(c, ctxt, 20, compute , in);
            }
            c.done();
        } catch (ConstellationCreationException e) {
            e.printStackTrace();
        }
    }
}
