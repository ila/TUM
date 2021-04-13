package helper;
import java.math.*;
import input.*;

public class CostFunctions {
    
    public static BigDecimal cOut(BigDecimal costLeftTree, BigDecimal costRightTree, BigDecimal outputSize){
        return costLeftTree.add(costRightTree.add(outputSize));
    }

    public static BigDecimal cHash(BigDecimal cardinalityLeftTree, BigDecimal cardinalityRightTree, BigDecimal costLeftTree, BigDecimal costRightTree){
        if(cardinalityLeftTree.compareTo(cardinalityRightTree) < 0){
            // cardinality of left tree is smaller
            return cardinalityLeftTree.multiply(new BigDecimal("1.2")).add(costLeftTree).add(costRightTree);
        } else {
            // cardinality of right tree is smaller or equal
            return cardinalityLeftTree.multiply(new BigDecimal("1.2")).add(costLeftTree).add(costRightTree);
        }
    }

    public static BigDecimal cNestedLoop(BigDecimal cardinalityLeftTree, BigDecimal cardinalityRightTree, BigDecimal costLeftTree, BigDecimal costRightTree){
        return cardinalityLeftTree.multiply(cardinalityRightTree).add(costLeftTree).add(costRightTree);
    }

    public static BigDecimal cSortedMerge(BigDecimal cardinalityLeftTree, BigDecimal cardinalityRightTree, BigDecimal costLeftTree, BigDecimal costRightTree){
        // no log implementation
        BigDecimal logCardLeftTree = ReadConsole.calculateLog(cardinalityLeftTree);
        BigDecimal logCardRightTree = ReadConsole.calculateLog(cardinalityRightTree);
        return cardinalityLeftTree.multiply(logCardLeftTree).multiply(cardinalityRightTree.multiply(logCardRightTree)).add(costLeftTree).add(costRightTree);
    }
}
