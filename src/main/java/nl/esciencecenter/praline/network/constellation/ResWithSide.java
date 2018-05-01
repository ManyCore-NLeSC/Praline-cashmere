package nl.esciencecenter.praline.network.constellation;

import java.io.Serializable;

public class ResWithSide<A> implements Serializable{

    final Side side;
    final A a;

    ResWithSide(Side side, A a){
        this.side = side;
        this.a = a;
    }
}
