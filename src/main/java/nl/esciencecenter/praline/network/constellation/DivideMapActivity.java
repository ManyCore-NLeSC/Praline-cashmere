package nl.esciencecenter.praline.network.constellation;

import ibis.constellation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;
import java.util.function.Function;

public class DivideMapActivity<A,B> extends Activity{

    final static Logger logger = LoggerFactory.getLogger(DivideMapActivity.class);
    final ArrayList<A> inputs;
    final ArrayList<B> output;
    final Function<A,B> compute;
    final Context context;
    final ActivityIdentifier parent;
    final Side side;
    boolean leftDone, rightDone;
    final int threshold;

    DivideMapActivity(int threshold, ActivityIdentifier parent, Side side, Context c, Function<A,B> comp, ArrayList<A> inputs){
        super(c, inputs.size() < 10000, inputs.size() > threshold);
        this.inputs = inputs;
        this.compute = comp;
        this.output = new ArrayList<>(inputs.size());
        for(int i = 0 ; i < inputs.size() ; i++){
            output.add(null);
        }
        this.context = c;
        this.parent = parent;
        this.side = side;
        leftDone = rightDone = false;
        this.threshold = threshold;
    }


    @Override
    public int initialize(Constellation c) {
        logger.info("got size {}", inputs.size());
        if(inputs.size() <= threshold){
            for(int i = 0 ; i < inputs.size() ; i++){
                output.set(i,compute.apply(inputs.get(i)));
            }
            return FINISH;
        } else {
            try {
                int sizeL = inputs.size()/2;
                ArrayList<A> leftHalf = new ArrayList<>(sizeL);

                for(int i = 0 ; i < sizeL ; i++ ){
                    leftHalf.add(inputs.get(i));
                }

                c.submit(new DivideMapActivity<A,B>(threshold,identifier(),Side.LEFT, context, compute,leftHalf));
                ArrayList<A> rightHalf = new ArrayList<>(inputs.size() - sizeL);
                for(int i = sizeL ; i < inputs.size(); i++){
                    rightHalf.add(inputs.get(i));
                }
                c.submit(new DivideMapActivity<A,B>(threshold,identifier(),Side.RIGHT,context,  compute,rightHalf));
                return SUSPEND;
            } catch (NoSuitableExecutorException e) {
                e.printStackTrace();
                throw new Error(e);
            }

        }
    }

    @Override
    public int process(Constellation c, Event event) {
        ResWithSide<ArrayList<B>> res = (ResWithSide)event.getData();
        int sizeL = inputs.size()/2;
        switch(res.side) {
            case LEFT:
                for(int i = 0 ; i < sizeL ; i++){
                    output.set(i,res.a.get(i));
                }
                leftDone = true;
                break;
            case RIGHT:
                for(int i = sizeL ; i < inputs.size() ; i++){
                    output.set(i,res.a.get(i - sizeL));
                }
                rightDone = true;
                break;
        }
        if(leftDone && rightDone){
            return FINISH;
        } else {
            return SUSPEND;
        }

    }

    @Override
    public void cleanup(Constellation c) {
        logger.info("Sending up {} {}", inputs.size(), parent);
        c.send(new Event(identifier(), parent, new ResWithSide( side, output)));

    }
}
