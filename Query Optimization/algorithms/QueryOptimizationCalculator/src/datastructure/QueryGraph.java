package datastructure;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;
public class QueryGraph {
    public List<Edge> edges;
    public Map<String, Relation> relations;
    public List<Edge> tmpContainer;
    public List<String> orderedRelations;

    public QueryGraph(){
        this.edges = new ArrayList<>();
        this.tmpContainer = new ArrayList<>();
        this.relations = new HashMap<>();
        this.orderedRelations = new ArrayList<>();
    }

    public QueryGraph(List<Edge> edges, Map<String, Relation> relations){
        this.edges = edges;
        this.relations = relations;
    }

    public void emptyContainer(){
        while(!tmpContainer.isEmpty()){
            this.edges.add(this.tmpContainer.get(0));
            this.tmpContainer.remove(0);
        }
    }

    public Edge returnEdge(String r1, String r2){
        return this.returnEdge(this.relations.get(r1), this.relations.get(r2));
    }

    public Edge returnEdge(Relation r1, Relation r2){
        for(int i = 0; i < this.edges.size(); i++){
            Edge tmp = this.edges.get(i);
            if(r1.name.equals(tmp.relation1.name) || r1.name.equals(tmp.relation2.name)){
                if(r2.name.equals(tmp.relation1.name) || r2.name.equals(tmp.relation2.name)){
                    return tmp;
                }
            }
        }
        return null;
    }

    public List<Edge> returnEdge(Relation r1){
        List<Edge> relevantEdges = new ArrayList<>();
        for(int i = 0; i < this.edges.size(); i++){
            Edge tmp = this.edges.get(i);
            if(r1.name.equals(tmp.relation1.name) || r1.name.equals(tmp.relation2.name)){
                relevantEdges.add(tmp);
            }
        }
        return relevantEdges;
    }
}
