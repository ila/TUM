package datastructure;

import java.math.BigDecimal;
import java.util.*;
import input.*;

import java.math.*;
import helper.*;

public class PrecedenceNode {
    public Relation root;
    public BigDecimal selectivity;
    public BigDecimal cost;
    public BigDecimal t;
    public BigDecimal rank;
    public List<PrecedenceNode> children;
    public int level;

    public PrecedenceNode(Relation root, BigDecimal selectivity, int level){
        this.root = root;
        this.children = new ArrayList<>();
        this.selectivity = selectivity;
        this.level = level;
        this.cost = root.cardinality.multiply(selectivity);
        this.t = cost;
        this.rank = t.subtract(new BigDecimal("1")).divide(cost, 100, RoundingMode.HALF_UP);
    }

    public void addChildren(PrecedenceNode child){
        this.children.add(child);
    }

    public boolean hasMultipleChildren(){
        return this.children.size() > 1;
    }

    public PrecedenceNode lowestNonChain(PrecedenceNode current, PrecedenceNode lowestNonChain){
        if(current.hasMultipleChildren()){
            lowestNonChain = current;
        }
        if(current.children.isEmpty()){
            return lowestNonChain;
        } else {
            for(int i = 0; i < current.children.size(); i++){
                PrecedenceNode tmp = lowestNonChain(current.children.get(i), lowestNonChain);
                if(tmp != null){
                    if(lowestNonChain == null || lowestNonChain.level < tmp.level){
                        lowestNonChain = tmp;
                    } else if(lowestNonChain.level == tmp.level && !lowestNonChain.root.name.equals(tmp.root.name)){
                        lowestNonChain = ReadConsole.keepNode(lowestNonChain, tmp);
                    }
                }
            }
            return lowestNonChain;
        }
    }

    public void printSubTree(){
        System.out.println("Root:" + this.root.name + ", n:" + this.root.cardinality + ",s:" + selectivity + ",C:" + cost + ",rank:" + rank + ", at level" + this.level);
        for(int i = 0; i < this.children.size(); i++){
            System.out.println("Child:" + this.children.get(i).root.name + ", with selectivity:" + this.children.get(i).selectivity.toString());
        }
        for(int i = 0; i < this.children.size(); i++){
            this.children.get(i).printSubTree();
        }
    }

    public void mergeTrees(){
        Comparator<PrecedenceNode> rankComparator = new RankComparator();
        PriorityQueue<PrecedenceNode> childrenLeft = new PriorityQueue<>(11, rankComparator);
        PrecedenceNode current = this;
        transferChildren(current, childrenLeft);
        while(!childrenLeft.isEmpty()){
            PrecedenceNode tmp = childrenLeft.poll();
            current.addChildren(tmp);
            System.out.println("Node:" + current.root.name + ", is now pointing to:" + tmp.root.name);
            current = tmp;
            transferChildren(tmp, childrenLeft);
        }
    }

    public void transferChildren(PrecedenceNode current, PriorityQueue<PrecedenceNode> childrenLeft){
        while(!current.children.isEmpty()){
            PrecedenceNode tmp = current.children.get(current.children.size()-1);
            childrenLeft.offer(tmp);
            current.children.remove(current.children.size()-1);
        }
    }

    public void normalize(PrecedenceNode parent, PrecedenceNode current){
        if(!current.children.isEmpty()){
            for(int i = 0; i < current.children.size(); i++){
                normalize(current, current.children.get(i));
            }
        }
        if(!parent.root.name.equals(this.root.name) && parent.rank.compareTo(current.rank) > 0){
            System.out.println("Found Contradiction:" + parent.root.name + ", with rank:" + parent.rank.toString() + ", is higher than rank:" + current.rank.toString() + ", of node:" + current.root.name);
            parent.root.name = parent.root.name + "," + current.root.name;
            parent.children = current.children;
            parent.cost = parent.cost.add(parent.t.multiply(current.cost));
            parent.t = parent.t.multiply(current.t);
            parent.rank = parent.t.subtract(new BigDecimal("1")).divide(parent.cost, 100, RoundingMode.HALF_UP);
            if(parent.children.size() == 1){
                System.out.println("MERGING, new name:" + parent.root.name + ", new child:" + parent.children.get(0).root.name + ", new t:" + parent.t.toString() + ", new C:" + parent.cost.toString() + ", new rank:" + parent.rank.toString());
            } else {
                System.out.println("MERGING, new name:" + parent.root.name + ", no new child, new t:" + parent.t.toString() + ", new C:" + parent.cost.toString() + ", new rank:" + parent.rank.toString());}
        } 
    }
}
