package nl.esciencecenter.praline.network.constellation;

import ibis.constellation.*;

import java.util.ArrayList;
import java.util.function.Function;

public class SimpleConstellationRunner<A,B> extends Thread {

    public ArrayList<B> res;


    public SimpleConstellationRunner(){
        res = null;
    }

    public void run(Function<A, B> compute, ArrayList<A> in,int threads){
        try {
            Context ctxt = new Context("MSA");
            ConstellationConfiguration[] cons = new ConstellationConfiguration[threads];
            for(int i = 0 ; i < threads; i++){
                cons[i] = new ConstellationConfiguration(ctxt);
            }
            Constellation c = ConstellationFactory.createConstellation(cons);
            c.activate();
            if(c.isMaster()){
                res = new SimpleConstellationScheduler<A,B>().
                        mapConstellation(c, ctxt, 2, compute , in);
            }
            c.done();
        } catch (ConstellationCreationException e) {
            e.printStackTrace();
        }
    }
}
