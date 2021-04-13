package algorithms;
import java.math.BigDecimal;
import java.util.*;

import input.*;
import datastructure.*;

public class GreedyJoinOrdering {
    
    public void performJoinOrdering1(QueryGraph queryGraph) {
        while(!queryGraph.relations.isEmpty()){
            Relation minRelation = null;
            for(String relation : queryGraph.relations.keySet()){
                Relation tmp = queryGraph.relations.get(relation);
                if(minRelation == null){
                    minRelation = tmp;
                } else if (minRelation.cardinality.compareTo(tmp.cardinality) > 0){
                    minRelation = tmp;
                } else if (minRelation.cardinality.compareTo(tmp.cardinality) == 0){
                    if(!ReadConsole.keepRelation(minRelation.name, tmp.name, minRelation.cardinality)){
                        minRelation = tmp;
                    }
                }
            }
            System.out.println("Choosing Relation:" + minRelation.name);
            queryGraph.relations.remove(minRelation.name);
        }
    }

    public BigDecimal performJoinOrdering2(QueryGraph queryGraph, Relation startRelation) {
        Set<String> alreadyInserted = new HashSet<>();
        alreadyInserted.add(startRelation.name);
        BigDecimal joinSize = startRelation.cardinality;
        BigDecimal cost = joinSize;
        System.out.println("Adding Relation:" + startRelation.name);
        while(alreadyInserted.size() < queryGraph.relations.size()){
            Edge minJoin = null;
            for(int i = 0; i < queryGraph.edges.size(); i++){
                Edge tmp = queryGraph.edges.get(i);
                if(alreadyInserted.contains(tmp.relation1.name) ^ alreadyInserted.contains(tmp.relation2.name)){
                    System.out.println(tmp.relation1.name + "," + tmp.relation2.name);
                    if(minJoin == null){
                        minJoin = tmp;
                    } else if (minJoin.selectivity.compareTo(tmp.selectivity) > 0){
                        minJoin = tmp;
                    } else if (minJoin.selectivity.compareTo(tmp.selectivity) == 0){
                        if(!ReadConsole.keepJoin(minJoin.returnJoinName(), tmp.returnJoinName(), minJoin.selectivity)){
                            minJoin = tmp;
                        }
                    }
                }
            }
            if(minJoin == null){
                System.out.println("Disconnected Graph couldn't find any edges!");
                return cost;
            }
            if(alreadyInserted.contains(minJoin.relation1.name)){
                alreadyInserted.add(minJoin.relation2.name);
                joinSize = joinSize.multiply(minJoin.selectivity).multiply(minJoin.relation2.cardinality);
                cost = cost.add(joinSize);
                System.out.println("Joining Relation:" + minJoin.relation2.name + ", with selectivity:" + minJoin.selectivity.toString() + ", and cout:" + cost.toString());
            } else {
                alreadyInserted.add(minJoin.relation1.name);
                joinSize = joinSize.multiply(minJoin.selectivity).multiply(minJoin.relation1.cardinality);
                cost = cost.add(joinSize);
                System.out.println("Joining Relation:" + minJoin.relation1.name + ", with selectivity:" + minJoin.selectivity.toString() + ", and cout:" + cost.toString());
            }
        }
        return cost;
    }

    public void performJoinOrdering3(QueryGraph queryGraph) {
        BigDecimal min = null;
        Relation best = null;
        for(String relation : queryGraph.relations.keySet()){
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Trying the next relation:" + relation);
            BigDecimal tmpcost = performJoinOrdering2(queryGraph, queryGraph.relations.get(relation));
            System.out.println("--------------------------------------------------------------------");
            if(min == null || min.compareTo(tmpcost) > 0){
                min = tmpcost;
                best = queryGraph.relations.get(relation);
            }
        }

        System.out.println("The best one was:" + best.name + ", with cost:" + min.toString());
    }
}
