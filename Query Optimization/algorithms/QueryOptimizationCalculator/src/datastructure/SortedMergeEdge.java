package datastructure;
import java.math.*;
import helper.*;

public class SortedMergeEdge extends Edge{

    public SortedMergeEdge(Relation r1, Relation r2, BigDecimal sel){
        super(r1, r2, sel);
    }

    @Override
    public Relation join(){
        return new Relation(this.joinSize, this.returnJoinName(), CostFunctions.cSortedMerge(relation1.cardinality, relation2.cardinality, relation1.cost, relation2.cost));
    }
    
    @Override
    public SortedMergeEdge newEdge(Relation left, Relation right, BigDecimal selectivity){
        return new SortedMergeEdge(left, right, selectivity);
    }
}
