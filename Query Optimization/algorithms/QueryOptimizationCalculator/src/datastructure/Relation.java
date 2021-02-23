package datastructure;
import java.math.*;
public class Relation {
    public BigDecimal cardinality;
    public BigDecimal cost;
    public String name;

    public Relation(BigDecimal cardinality, String name){
        this.name = name;
        this.cardinality = cardinality;
        this.cost = new BigDecimal("0");
        System.out.println("New Relation:" + name + ", with cardinality:" + cardinality);
    }

    public Relation(BigDecimal cardinality, String name, BigDecimal cost){
        this.name = name;
        this.cardinality = cardinality;
        this.cost = cost;
    }
}
