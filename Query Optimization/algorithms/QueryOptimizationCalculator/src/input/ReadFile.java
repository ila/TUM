package input;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

import datastructure.*;

import java.math.*;


/*
* This portion is a fit of the code found on w3schools;Opened at: 13.February.2020 10:30;https://www.w3schools.com/java/java_files_read.asp; Java Read Files
*/

import java.util.Map;
import java.util.HashMap;

public class ReadFile {

  public static BigDecimal getBigDecimal(String[] edge){
    if(edge[2].contains("D")){
      return new BigDecimal(edge[3]);
    } else if(edge[2].contains("F")){
      return new BigDecimal(edge[3]).divide(new BigDecimal(edge[4]), 100, RoundingMode.HALF_UP);
    } else {
      return new BigDecimal("0");
    }
  }

  public static PrecedenceNode readFilePrecedenceNode() {

    Map<String, PrecedenceNode> nodes = new HashMap<>();
    String data;
    PrecedenceNode root = null;

    try {
        File myObj = new File("src/inputfiles/PrecedenceGraph.txt");
        Scanner myReader = new Scanner(myObj);
        data = myReader.nextLine();
        root = new PrecedenceNode(new Relation(new BigDecimal("1"), data), new BigDecimal("1"), 0);
        nodes.put(data, root);
        while(!data.equals("END")){
          PrecedenceNode current = nodes.get(data);
          data = myReader.nextLine();
          while(!data.equals("END")){
            String[] tmpNode = data.split(",");
            PrecedenceNode tmp;
            if(tmpNode[2].equals("D")){
              tmp = new PrecedenceNode(new Relation(new BigDecimal(tmpNode[1]), tmpNode[0]), new BigDecimal(tmpNode[3]), current.level+1);
            } else {
              tmp = new PrecedenceNode(new Relation(new BigDecimal(tmpNode[1]), tmpNode[0]), new BigDecimal(tmpNode[3]).divide(new BigDecimal(tmpNode[4]), 100, RoundingMode.HALF_UP), current.level+1);
            }
            nodes.put(tmpNode[0], tmp);
            current.addChildren(tmp);
            data = myReader.nextLine();
          }
          data = myReader.nextLine();
        }
        myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }

    return root;
  }

  public static QueryGraph readFileQueryGraph(String type) {

    QueryGraph queryGraph = new QueryGraph();
    String data;

    try {
        File myObj = new File("src/inputfiles/QueryGraph.txt");
        Scanner myReader = new Scanner(myObj);
        while(true){
            data = myReader.nextLine();
            if(data.equals("END")){
                data = myReader.nextLine();
                break;
            } else {
                String[] relation = data.split(",");
                queryGraph.relations.put(relation[0], new Relation(new BigDecimal(relation[1]), relation[0]));
                queryGraph.orderedRelations.add(relation[0]);
            }
        }
        if(type.equals("cOut")){
          while (!data.equals("END")) {
            String[] edge = data.split(",");
            queryGraph.edges.add(new Edge(queryGraph.relations.get(edge[0]), queryGraph.relations.get(edge[1]), getBigDecimal(edge)));
            data = myReader.nextLine();
          }
        } else if(type.equals("cHash")){
            while (!data.equals("END")) {
              String[] edge = data.split(",");
              queryGraph.edges.add(new HashEdge(queryGraph.relations.get(edge[0]), queryGraph.relations.get(edge[1]), getBigDecimal(edge)));
              data = myReader.nextLine();
            }
          } else if(type.equals("cNestedLoop")){
            while (!data.equals("END")) {
              String[] edge = data.split(",");
              queryGraph.edges.add(new NestedLoopEdge(queryGraph.relations.get(edge[0]), queryGraph.relations.get(edge[1]), getBigDecimal(edge)));
              data = myReader.nextLine();
            }
          } else if(type.equals("cSortedMerge")){
            while (!data.equals("END")) {
              String[] edge = data.split(",");
              queryGraph.edges.add(new SortedMergeEdge(queryGraph.relations.get(edge[0]), queryGraph.relations.get(edge[1]), getBigDecimal(edge)));
              data = myReader.nextLine();
            }
          }
        
        myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }

    return queryGraph;
  }

}
