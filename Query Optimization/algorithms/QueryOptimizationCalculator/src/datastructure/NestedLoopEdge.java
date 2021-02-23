package datastructure;
import java.math.*;

import helper.*;
public class NestedLoopEdge extends Edge{

    public NestedLoopEdge(Relation r1, Relation r2, BigDecimal sel){
        super(r1, r2, sel);
    }

    @Override
    public Relation join(){
        return new Relation(this.joinSize, this.returnJoinName(), CostFunctions.cNestedLoop(relation1.cardinality, relation2.cardinality, relation1.cost, relation2.cost));
    }   

    @Override
    public NestedLoopEdge newEdge(Relation left, Relation right, BigDecimal selectivity){
        return new NestedLoopEdge(left, right, selectivity);
    }
}
