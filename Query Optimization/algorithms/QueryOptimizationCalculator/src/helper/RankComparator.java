package helper;

import datastructure.*;
import java.util.Comparator;
import input.ReadConsole;

public class RankComparator implements Comparator<PrecedenceNode> {
    @Override
    public int compare(PrecedenceNode n1, PrecedenceNode n2){
        if(n1.rank.compareTo(n2.rank) == 0){
            if(n1.root.name.equals(ReadConsole.keepNode(n1, n2).root.name)){
                return -1;
            } else {
                return 1;
            }
        } else {
            return n1.rank.compareTo(n2.rank);
        }
    }
}
