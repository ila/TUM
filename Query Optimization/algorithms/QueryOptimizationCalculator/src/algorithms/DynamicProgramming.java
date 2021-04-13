package algorithms;

import datastructure.*;
import input.ReadConsole;

import java.math.BigDecimal;
import java.util.*;


public class DynamicProgramming {

    public Boolean allowCP;

    public final static String LINES = "-----------------------------------------------";
    
    public void performDPsize(QueryGraph queryGraph, Boolean allowCP){
        this.allowCP = allowCP;
        // print the order of DP size
        Map<Integer, Map<String, Relation>> dpTable = new HashMap<>();
        dpTable.put(1, queryGraph.relations);
        printTableEntry(dpTable.get(1), 1);
        int size = 2;
        while(size <= queryGraph.orderedRelations.size()){
            dpTable.put(size, new HashMap<>());
            List<String> relevantRelations = new ArrayList<>();
            loopThroughPermutations(0, size, queryGraph.orderedRelations, relevantRelations, dpTable, queryGraph);
            printTableEntry(dpTable.get(size), size);
            size++;
        }
    }

    public void performDPsub(QueryGraph queryGraph, Boolean allowCP){
        // print the order of DP sub
        this.allowCP = allowCP;
        // print the order of DP size
        Map<Integer, Map<String, Relation>> dpTable = new HashMap<>();
        dpTable.put(1, queryGraph.relations);
        printTableEntry(dpTable.get(1), 1);
        int size = 2;
        while(size <= queryGraph.relations.size()){
            dpTable.put(size, new HashMap<>());
            size++;
        }
        subPermutations(dpTable, queryGraph, queryGraph.orderedRelations);
    }

    public void performDPccp(QueryGraph queryGraph){
        // print the order of DP connected component complement pairs
        QueueMap queueMap = new QueueMap(queryGraph);
        while(!queueMap.csg.empty()){
            String csg = queueMap.csg.pop();
            queueMap.findComplementPairs(csg, "");
        }
    }

    public void performDPhyp(){
        // print the order of DP hyperedge
    }

    public void performDPLinear(){
        // print the order of DP linear
    }

    public void loopThroughPermutations(int current, int size, List<String> orderedRelationName, List<String> relevantRelations, Map<Integer, Map<String, Relation>> dpTable, QueryGraph queryGraph){
        if(relevantRelations.size() == size){
            int halfSize = size/2;
            for(int i = 1; i <= halfSize; i++){
                //System.out.println("Starting with finalSize:" + i);
                loopThroughSet(relevantRelations, dpTable, queryGraph, new ArrayList<>(), i ,0);
            }
        } else if(current < orderedRelationName.size()){
            for(int i = current; i < orderedRelationName.size(); i++){
                //System.out.println("Get:" + i + ",at depth:" + current);
                relevantRelations.add(orderedRelationName.get(i));
                loopThroughPermutations(i+1, size, orderedRelationName, relevantRelations, dpTable, queryGraph);
                relevantRelations.remove(relevantRelations.size()-1);
            }
        }
    }

    public void subPermutations(Map<Integer, Map<String, Relation>> dpTable, QueryGraph queryGraph, List<String> orderedRelationName){
        Bitvector relations = new Bitvector(orderedRelationName.size());
        while(!relations.overflow){
            List<String> relevantRelations = relations.returnSet(orderedRelationName);
            int finalSize = relevantRelations.size()/2;
            for(int i = 1; i <= finalSize; i++){
                //System.out.println("Starting with finalSize:" + i);
                loopThroughSet(relevantRelations, dpTable, queryGraph, new ArrayList<>(), i ,0);
            }
            String key = returnSetKey(0, relevantRelations.size(), relevantRelations);
            printSet(key, dpTable.get(relevantRelations.size()).get(key));
            relations.add();
        }
    }

    public Edge loopThroughSet(List<String> relevantRelations, Map<Integer, Map<String, Relation>> dpTable, QueryGraph queryGraph, List<String> currentSet, Integer finalSize, int current){
        //System.out.println("Starting at current:" + current);
        if(currentSet.size() == finalSize){
            //done with computation
            String leftName = "";
            String rightName = "";
            int counter = 0;
            for(int i = 0; i < relevantRelations.size(); i++){
                if(currentSet.size() > counter && relevantRelations.get(i).equals(currentSet.get(counter))){
                    leftName = leftName + relevantRelations.get(i) + ",";
                    counter++;
                } else {
                    rightName = rightName + relevantRelations.get(i) + ",";
                }
            }
            leftName = leftName.substring(0, leftName.length()-1);
            rightName = rightName.substring(0, rightName.length()-1);
            //System.out.println("Left" + leftName + ",right" + rightName);

            Edge result = calculatePermutation(dpTable.get(currentSet.size()).get(leftName), dpTable.get(relevantRelations.size() - currentSet.size()).get(rightName), queryGraph);
            System.out.println("Set:{" + returnSetKey(0, relevantRelations.size(), relevantRelations) + "}, plan:(" + result.returnJoinName() + "), card:" + result.join().cardinality + ", cost:" + result.join().cost);
            return result;
        } else if(current < relevantRelations.size()){
            Relation min = null;
            Edge minEdge = null;
            for(int i = current; i < relevantRelations.size(); i++){
                //System.out.println("Adding i:" + i + ",at current:" + current);
                currentSet.add(relevantRelations.get(i));
                Edge resultEdge = loopThroughSet(relevantRelations, dpTable, queryGraph, currentSet, finalSize, i+1);
                if(resultEdge != null){
                    if(min == null || min.cost.compareTo(resultEdge.join().cost) > 0){
                        min = resultEdge.join();
                        minEdge = resultEdge;
                    }
                }
                currentSet.remove(currentSet.size()-1);
            }

            if(min != null){
                //System.out.println("Optimal Join:" + min.name + ", with card=" + min.cardinality + ", and cost:" + min.cost);
                dpTable.get(relevantRelations.size()).put(returnSetKey(0, relevantRelations.size(), relevantRelations), min);
                updateEdges(minEdge, min, queryGraph);
            }
            return minEdge;
        } else {
            return null;
        }
    }
 

    public Edge calculatePermutation(Relation left, Relation right, QueryGraph queryGraph){
        Relation result = null;
        Edge resultEdge = null;
        if(left != null && right != null){
            //System.out.println("Testing combination:" + left.name +",and:" + right.name);
            Edge tmpEdge = queryGraph.returnEdge(left, right);
            if(tmpEdge != null){
                Relation tmp = tmpEdge.join();
                //System.out.println("Join:" + tmp.name + ", with card=" + tmp.cardinality + ", and cost:" + tmp.cost);
                if(result == null || tmp.cost.compareTo(result.cost) < 0){
                    result = tmp;
                    resultEdge = tmpEdge;
                } else if(tmp.cost.compareTo(result.cost) == 0){
                    if(!ReadConsole.keepRelation(result.name, tmp.name, result.cost)){
                        result = tmp;
                        resultEdge = tmpEdge;
                    }
                }
            } else if(allowCP){
                tmpEdge = new Edge(left, right, new BigDecimal("1"));
                Relation tmp = tmpEdge.join(); 
                //System.out.println("Join as CP:" + tmp.name + ", with card=" + tmp.cardinality + ", and cost:" + tmp.cost);
                if(result == null || tmp.cost.compareTo(result.cost) < 0){
                    result = tmp;
                    resultEdge = tmpEdge;
                } else if(tmp.cost.compareTo(result.cost) == 0){
                    if(!ReadConsole.keepRelation(result.name, tmp.name, result.cost)){
                        result = tmp;
                        resultEdge = tmpEdge;
                    }
                }
            }
        }
        return resultEdge;
    }

    public void updateEdges(Edge edge, Relation joinedEdge, QueryGraph queryGraph){
        //System.out.println("Updating Edges for join");
        Relation left = edge.relation1;
        Relation right = edge.relation2;
        List<Edge> leftEdges = queryGraph.returnEdge(left);
        Set<String> alreadyChanged = new HashSet<>();
        alreadyChanged.add(left.name);
        alreadyChanged.add(right.name);
        for(int i = 0; i < leftEdges.size(); i++){
            Edge tmpEdge = leftEdges.get(i);
            Relation tmpRight = tmpEdge.returnOtherRelation(left.name);
            Edge toBeMerged = queryGraph.returnEdge(right, tmpRight);
            if(!alreadyChanged.contains(tmpRight.name)){
                if(toBeMerged != null){
                    BigDecimal newselectivity = tmpEdge.selectivity.multiply(toBeMerged.selectivity);
                    //System.out.println("Merging edge from:" + left.name + ",and from:" + right.name + ", each to:" + tmpRight.name + ", with new selectivity:" + newselectivity.toString());
                    queryGraph.edges.add(tmpEdge.newEdge(joinedEdge, tmpRight, newselectivity));
                } else {
                    //System.out.println("Updating edge from:" + left.name + ", to:" + tmpRight.name);
                    queryGraph.edges.add(tmpEdge.newEdge(joinedEdge, tmpRight, tmpEdge.selectivity));
                }
                alreadyChanged.add(tmpRight.name);
            }
        }
        List<Edge> rightEdges = queryGraph.returnEdge(right);
        for(int i = 0; i < rightEdges.size(); i++){
            Edge tmpEdge = rightEdges.get(i);
            Relation tmpLeft = tmpEdge.returnOtherRelation(right.name);
            if(!alreadyChanged.contains(tmpLeft.name)){
                //System.out.println("Updating edge from:" + right.name + ", to:" + tmpLeft.name);
                queryGraph.edges.add(tmpEdge.newEdge(joinedEdge, tmpLeft, tmpEdge.selectivity));
            }
        }
    }

    public String returnSetKey(int start, int end, List<String> relevantRelations){
        String key = "";
        for(int i = start; i < end; i++){
            key = key + relevantRelations.get(i) + ",";
        }
        key = key.substring(0, key.length() -1);
        return key;
    }

    public void printTableEntry(Map<String, Relation> printTableEntry, Integer size){
        System.out.println(LINES);
        System.out.println("Calculating Permutations of new size:" + size);
        for(String key: printTableEntry.keySet()){
            Relation tmp = printTableEntry.get(key);
            System.out.println("set: {" + key + "}, plan:" + tmp.name + ", card:" + tmp.cardinality + ", cost:" + tmp.cost);
        }
        System.out.println(LINES);
    }

    public void printSet(String key, Relation tmp){
        System.out.println(LINES);
        System.out.println("set: {" + key + "}, plan:" + tmp.name + ", card:" + tmp.cardinality + ", cost:" + tmp.cost);
        System.out.println(LINES);
    }

    public void printCsgCmp(String csg, String cmp){
        System.out.println("|csg:" + csg + "|cmp: " + cmp + "|");
    }

    public void printCsgCmp(String csg){
        System.out.println("|csg:" + csg + "|cmp: \u2205|");
    }
}
