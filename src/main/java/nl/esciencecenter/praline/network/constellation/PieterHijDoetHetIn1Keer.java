package nl.esciencecenter.praline.network.constellation;

import ibis.constellation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Vector;
import java.util.function.Function;

public class PieterHijDoetHetIn1Keer {

    final static Logger logger = LoggerFactory.getLogger(PieterHijDoetHetIn1Keer.class);

    public static void main(String[] argv){
        try {
            Context ctxt = new Context("MSA");
            Constellation c = ConstellationFactory.createConstellation(new ConstellationConfiguration(ctxt));
            c.activate();
            if(c.isMaster()){
                ArrayList<Integer> inputs = new ArrayList(100);
                for(int i = 0 ; i < 100; i++){
                    inputs.add(i);
                }
                ArrayList<Integer> outputs = new SimpleConstellationScheduler<Integer,Integer>().
                        mapConstellation(c, ctxt, 20, (x) -> x * x , inputs);
                for(int i = 0 ; i < 100; i++){
                    System.out.println(outputs.get(i));
                }
            }
            c.done();
        } catch (ConstellationCreationException e) {
            e.printStackTrace();
        }
    }
}
