package datastructure;
import java.math.*;

import helper.*;

public class Edge {
    public Relation relation1;
    public Relation relation2;
    
    public BigDecimal selectivity;
    public BigDecimal joinSize;

    public Edge(Relation r1, Relation r2, BigDecimal sel){
        this.selectivity = sel;
        this.relation1 = r1;
        this.relation2 = r2;
        this.joinSize = relation1.cardinality.multiply(sel).multiply(relation2.cardinality);

        System.out.println("New Edge:" + relation1.name + "|><|" + relation2.name + ", with selectivity:" + selectivity);
    }

    public Relation join(){
        return new Relation(this.joinSize, this.returnJoinName(), CostFunctions.cOut(relation1.cost, relation2.cost, joinSize));
    }

    public String returnJoinName(){
        return "(" + relation1.name + "|><|" + relation2.name + ")";
    }

    public String ifRelation(String name){
        if(name.equals(relation1.name)){
            return relation2.name;
        } else if(name.equals(relation2.name)){
            return relation1.name;
        } else {
            return "";
        }
    }

    public void updateSelectivity(BigDecimal sel) {
        this.selectivity = sel;
        this.joinSize = relation1.cardinality.multiply(sel).multiply(relation2.cardinality);
    }

    public Relation returnOtherRelation(String relation){
        if(this.relation1.name.equals(relation)){
            return this.relation2;
        } else if(this.relation2.name.equals(relation)) {
            return this.relation1;
        } else {
            return null;
        }
    }

    public Edge newEdge(Relation left, Relation right, BigDecimal selectivity){
        return new Edge(left, right, selectivity);
    }

}
