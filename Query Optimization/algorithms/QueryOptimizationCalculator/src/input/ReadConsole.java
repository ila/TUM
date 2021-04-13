package input;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import java.util.*;
import datastructure.*;
/*
* This portion is a fit of the code from:
* 0xCursor and athspk;Opened at: 13.February.2020, 12:30;https://stackoverflow.com/questions/4644415/java-how-to-get-input-from-system-console, First Awnser; Java: How to get input from System.console()
*/

public class ReadConsole {
    public static Boolean keepJoin(String join1, String join2, BigDecimal cost) { 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Two joins have a resulting joinSize:" + cost.toString() + ",enter y to perform the join:" + join1 + ", first or n to choose join:" + join2 + ", first");
            String s = br.readLine();
            return s.equals("y");
        }  catch( IOException ie) {
            ie.printStackTrace();
            return true;
        }
    }

    public static Boolean keepRelation(String relation1, String relation2, BigDecimal cardinality) { 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Two relations have the same cardinality:" + cardinality.toString() + ",enter y to keep:" + relation1 + ", first or n to choose relation:" + relation2 + ", first");
            String s = br.readLine();
            return s.equals("y");
        }  catch( IOException ie) {
            ie.printStackTrace();
            return true;
        }
    }

    public static Boolean computeCP() { 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Do you want to compute CrossProductes?(y,n)");
            String s = br.readLine();
            return s.equals("y");
        }  catch( IOException ie) {
            ie.printStackTrace();
            return true;
        }
    }

    public static Boolean printEmptySets() { 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Do you want to see Empty Sets?(y,n)");
            String s = br.readLine();
            return s.equals("y");
        }  catch( IOException ie) {
            ie.printStackTrace();
            return true;
        }
    }

    public static PrecedenceNode keepNode(PrecedenceNode node1, PrecedenceNode node2) { 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Two nodes are on the same level:" + node1.level + ",enter y to keep:" + node1.root.name + ", first or n to choose relation:" + node2.root.name + ", first");
            String s = br.readLine();
            if(s.equals("y")){
                return node1;
            } else {
                return node2;
            }
        }  catch( IOException ie) {
            ie.printStackTrace();
            return node1;
        }
    }

    public static String enterAlgorithm() { 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("What Algorithm do you want? (GJO1, GJO2, GJO3, GOO, IKKBZ, DPsize, DPsub)");
            return br.readLine();
        }  catch( IOException ie) {
            ie.printStackTrace();
            return "";
        }
    }

    public static String enterRelation() { 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("What Relation do you want to start with?");
            return br.readLine();
        }  catch( IOException ie) {
            ie.printStackTrace();
            return "";
        }
    }

    public static String enterCostFunction() { 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("What CostFunction do you want? (cOut, cHash, cNestedLoop, cSortedMerge)");
            return br.readLine();
        }  catch( IOException ie) {
            ie.printStackTrace();
            return "";
        }
    }

    public static BigDecimal calculateLog(BigDecimal input) { 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please calculate the Log10 of:" + input.toString());
            return new BigDecimal(br.readLine());
        }  catch( IOException ie) {
            ie.printStackTrace();
            return input;
        }
    }

    
}