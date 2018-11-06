package nl.esciencecenter.praline.network.constellation;

import ibis.constellation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;

public class SimpleConstellationRunner<A extends Serializable,B extends Serializable> extends Thread {

    public ArrayList<B> res;


    public SimpleConstellationRunner(){
        res = null;
    }

    public void run(Constellation c, Function<A, B> compute, ArrayList<A> in){
            Context ctxt = new Context("MSA");
                res = new SimpleConstellationScheduler<A,B>().
                        mapConstellation(c, ctxt, 2, compute , in);


    }
}
