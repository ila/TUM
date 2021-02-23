package algorithms;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import input.*;
import datastructure.*;

public class GreedyOperatorOrdering {
    
    public void performGOO(QueryGraph queryGraph){
        List<Edge> joins = queryGraph.edges;

        // loop over joins until everything is joined doubleo one tree
        while(joins.size() > 1){
            Edge minjoin = joins.get(0);
            int mindex = 0;

            // loop over all joins
            for(int i = 1; i < joins.size(); i++){
                // if a new smallest join is found replace minjoin 
                if(joins.get(i).joinSize.compareTo(minjoin.joinSize) < 0){
                    minjoin = joins.get(i);
                    mindex = i;
                }
            }

            for(int i = 1; i < joins.size(); i++){
                if(joins.get(i).joinSize.compareTo(minjoin.joinSize) == 0 && !joins.get(i).returnJoinName().equals(minjoin.returnJoinName())){
                    if(!ReadConsole.keepJoin(minjoin.returnJoinName(), joins.get(i).returnJoinName(), minjoin.joinSize)){
                        minjoin = joins.get(i);
                        mindex = i;
                    }
                }
            }

            String r1 = minjoin.relation1.name;
            String r2 = minjoin.relation2.name;
            Relation joined = minjoin.join();
            System.out.println("Found new cheapast join: " + joined.name + ", with join size: " + joined.cardinality  + ", cost: " + joined.cost);
            joins.remove(mindex);

            // loop over all joins
            Map<String, BigDecimal> joinSelectivity = new HashMap();
            Map<String, Edge> tmpEdge = new HashMap();
            List<Edge> newJoins = new ArrayList();
            for(int i = 0; i < joins.size(); i++){
                String otherPartner = joins.get(i).ifRelation(r1);
                if(otherPartner.length() > 0){
                    if(joinSelectivity.containsKey(otherPartner)){
                        joinSelectivity.put(otherPartner, joinSelectivity.get(otherPartner).multiply(joins.get(i).selectivity));
                        System.out.println("Merging Edges to: " + otherPartner + ", new sel: " + joinSelectivity.get(otherPartner));
                    } else {
                        joinSelectivity.put(otherPartner, joins.get(i).selectivity);
                        newJoins.add(joins.get(i));
                        tmpEdge.put(otherPartner, joins.get(i));
                    }
                } else {
                    otherPartner = joins.get(i).ifRelation(r2);
                    if(otherPartner.length() > 0){
                        if(joinSelectivity.containsKey(otherPartner)){
                            joinSelectivity.put(otherPartner, joinSelectivity.get(otherPartner).multiply(joins.get(i).selectivity));
                            System.out.println("Merging Edges to: " + otherPartner + ", new sel: " + joinSelectivity.get(otherPartner));
                        } else {
                            joinSelectivity.put(otherPartner, joins.get(i).selectivity);
                            newJoins.add(joins.get(i));
                            tmpEdge.put(otherPartner, joins.get(i));
                        }
                    } else {
                        newJoins.add(joins.get(i));
                    }
                }
            }

            tmpEdge.forEach((key, value) -> {
                if(value.relation1.name.equals(key)){
                    value.relation2 = joined;
                } else {
                    value.relation1 = joined;
                }
                value.updateSelectivity(joinSelectivity.get(key));
            });
            joins = newJoins;
        }
        Relation finalJoin = joins.get(0).join();
        System.out.println(finalJoin.name + ", join size:" + finalJoin.cardinality + ", cost: " + finalJoin.cost);
    }

}
