import input.*;

import algorithms.*;
import datastructure.*;
/**
 * Main Application, where all algorithms are described
 */
public class App {

    public static void main(String[] args) {

        String algorithm = ReadConsole.enterAlgorithm();
        
        if(algorithm.equals("GOO")){
            String type = ReadConsole.enterCostFunction();
            QueryGraph queryGraph = ReadFile.readFileQueryGraph(type);
            GreedyOperatorOrdering goo = new GreedyOperatorOrdering(); 
            
            goo.performGOO(queryGraph);
        } else if(algorithm.equals("GJO1")){
            String type = ReadConsole.enterCostFunction();
            QueryGraph queryGraph = ReadFile.readFileQueryGraph(type);
            GreedyJoinOrdering goo = new GreedyJoinOrdering(); 
            
            goo.performJoinOrdering1(queryGraph);
        } else if(algorithm.equals("GJO2")){
            String type = ReadConsole.enterCostFunction();
            QueryGraph queryGraph = ReadFile.readFileQueryGraph(type);
            GreedyJoinOrdering goo = new GreedyJoinOrdering(); 
            
            goo.performJoinOrdering2(queryGraph, queryGraph.relations.get(ReadConsole.enterRelation()));
        } else if(algorithm.equals("GJO3")){
            String type = ReadConsole.enterCostFunction();
            QueryGraph queryGraph = ReadFile.readFileQueryGraph(type);
            GreedyJoinOrdering goo = new GreedyJoinOrdering(); 
            
            goo.performJoinOrdering3(queryGraph);
        } else if(algorithm.equals("IKKBZ")){
            PrecedenceNode root = ReadFile.readFilePrecedenceNode();
            IKKBZ ikkbz = new IKKBZ();
            
            ikkbz.performIKKBZ(root);
        } else if(algorithm.equals("DPsize")){
            String type = ReadConsole.enterCostFunction();
            QueryGraph queryGraph = ReadFile.readFileQueryGraph(type);
            DynamicProgramming dp = new DynamicProgramming(); 
            Boolean allowCP = ReadConsole.computeCP();

            dp.performDPsize(queryGraph, allowCP);
        } else if(algorithm.equals("DPsub")){
            String type = ReadConsole.enterCostFunction();
            QueryGraph queryGraph = ReadFile.readFileQueryGraph(type);
            DynamicProgramming dp = new DynamicProgramming(); 
            Boolean allowCP = ReadConsole.computeCP();

            dp.performDPsub(queryGraph, allowCP);
        } else if(algorithm.equals("DPccp")){
            QueryGraph queryGraph = ReadFile.readFileQueryGraph("cOut");
            DynamicProgramming dp = new DynamicProgramming();

            dp.performDPccp(queryGraph);
        } 
    }
}
