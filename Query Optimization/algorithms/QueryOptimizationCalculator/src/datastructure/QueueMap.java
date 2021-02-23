package datastructure;

import java.util.*;


public class QueueMap {
    public Map<String, Queue<String>> map;
    public Stack<String> csg;
    public QueryGraph queryGraph;
    
    public QueueMap(QueryGraph queryGraph){
        this.csg = new Stack<>();
        this.map = new HashMap<>();
        this.queryGraph = queryGraph;

        for(int i = 0; i < queryGraph.orderedRelations.size(); i++){
            String tmp = queryGraph.orderedRelations.get(i);
            this.csg.push(tmp);
            Queue<String> tmpQueue = new LinkedList<>();
            for(int j = queryGraph.orderedRelations.size() - 1; j > i; j--){
                String joinPartner = queryGraph.orderedRelations.get(j);
                Edge tmpEdge = queryGraph.returnEdge(tmp, joinPartner);
                if(tmpEdge != null){
                    tmpQueue.offer(joinPartner);
                }
            }
            this.map.put(tmp, tmpQueue);
        }
    }

    public Set<String> stringToSet(String s1){
        return new HashSet<>(Arrays.asList(s1.split(",")));
    }

    public List<String> stringToArray(String s1){
        return Arrays.asList(s1.split(","));
    }

    public Queue<String> returnValidComplements(List<String> allowedRelations, Set<String> forbiddenRelations){
        Set<String> forbiddenCopy = new HashSet<>(forbiddenRelations);
        Queue<String> returnValidComplements = new LinkedList<>();
        for(int i = 0; i < allowedRelations.size(); i++){
            String csgElement = allowedRelations.get(i);
            if(csgElement.length() > 0){
                Queue<String> csgElementOutgoingEdges = new LinkedList<>(this.map.get(csgElement));
                while(!csgElementOutgoingEdges.isEmpty()){
                    String outgoingEdgeRelation = csgElementOutgoingEdges.poll();
                    if(!forbiddenCopy.contains(outgoingEdgeRelation)){
                        forbiddenCopy.add(outgoingEdgeRelation);
                        returnValidComplements.offer(outgoingEdgeRelation);
                    }
                }
            }
        }
        return returnValidComplements;
    }

    public Boolean correctOrder(String s1){
        List<String> order = stringToArray(s1);
        int mindex = -1;
        for(int i = 0; i < order.size(); i++){
            int tmp = returnIndex(order.get(i));
            if(mindex > tmp){
                return false;
            }
            mindex = tmp;
        }
        return true;
    }

    public Integer returnIndex(String s1){
        for(int j = 0; j < queryGraph.orderedRelations.size(); j++){
            if(queryGraph.orderedRelations.get(j).equals(s1)){
                return j;
            }
        }
        return 0;
    }

    public void findComplementPairs(String currentCsg, String currentCMP){
        Queue<String> bfsCMP = new LinkedList<>();

        String all = currentCMP.length() > 0? currentCsg + "," + currentCMP : currentCsg;
        Set<String> forbiddenRelations = stringToSet(all);
        List<String> allowedRelations = stringToArray(all);
        bfsCMP = returnValidComplements(allowedRelations, forbiddenRelations);
        while(!bfsCMP.isEmpty()){
            String tmpCMP = bfsCMP.poll();
            if(currentCMP.length() == 0){
                if(tmpCMP.length() != 0){
                    System.out.println("|csg:" + currentCsg + "|new cmp: " + tmpCMP + "|");
                }
                findComplementPairs(currentCsg, tmpCMP);
            } else if(correctOrder(currentCMP + "," + tmpCMP)){
                System.out.println("|csg:" + currentCsg + "|new cmp: " + currentCMP + "," + tmpCMP + "|");
                findComplementPairs(currentCsg, currentCMP + "," + tmpCMP);
            }
        }

        allowedRelations = stringToArray(currentCsg);
        bfsCMP = returnValidComplements(allowedRelations, forbiddenRelations);
        while(!bfsCMP.isEmpty()){
            String tmpCMP = bfsCMP.poll();
            if(correctOrder(tmpCMP)){
                if(currentCMP.length() != 0){
                    System.out.println("|csg:" + currentCsg + "," + tmpCMP + "|new cmp: " + currentCMP + "|");
                }
                findComplementPairs(currentCsg + "," + tmpCMP, currentCMP);
            }
        }
    }
}
