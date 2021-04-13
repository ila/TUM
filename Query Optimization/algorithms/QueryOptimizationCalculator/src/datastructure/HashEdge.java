package datastructure;
import java.math.*;

import helper.*;
public class HashEdge extends Edge {
    
    public HashEdge(Relation r1, Relation r2, BigDecimal sel){
        super(r1, r2, sel);
    }

    @Override
    public Relation join(){
        return new Relation(this.joinSize, this.returnJoinName(), CostFunctions.cHash(relation1.cardinality, relation2.cardinality, relation1.cost, relation2.cost));
    }

    @Override
    public String returnJoinName(){
        if(relation1.cardinality.compareTo(relation2.cardinality) < 0){
            return "(" + relation1.name + "|><|" + relation2.name + ")";
        } else {
            return "(" + relation2.name + "|><|" + relation1.name + ")";
        }
    }

    @Override 
    public HashEdge newEdge(Relation left, Relation right, BigDecimal selectivity){
        return new HashEdge(left, right, selectivity);
    }
}
