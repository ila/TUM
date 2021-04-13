package datastructure;

import java.util.*;

public class Bitvector {
    public List<Integer> bits;
    public Boolean overflow;

    public Bitvector(int size){
        this.bits = new ArrayList<>();
        for(int i = 0; i < size - 1; i++){
            this.bits.add(0);
        } 
        this.bits.add(1);
        this.overflow = false;
    }

    public void add(){
        for(int i = bits.size() - 1; i >= 0; i--){
            if(bits.get(i) == 1){
                bits.set(i, 0);
                if(i == 0){
                    this.overflow = true;
                }
            } else {
                bits.set(i, 1);
                break;
            }
        }
    }

    public boolean halftime(){
        for(int i = 1; i < bits.size(); i++){
            if(bits.get(i) == 1){
                return false;
            }
        }
        return bits.get(0) == 1;
    }

    public boolean fulltime(){
        for(int i = 0; i < bits.size(); i++){
            if(bits.get(i) != 1){
                return false;
            }
        }
        return true;
    }

    public void reset(){
        for(int i = 0; i < bits.size() - 1; i++){
            this.bits.add(0);
        } 
        this.bits.add(1);
        this.overflow = false;
    }

    public List<String> returnSet(List<String> orderedRelations){
        List<String> result = new ArrayList<>();
        for(int i = bits.size()-1; i >= 0; i--){
            if(bits.get(i) == 1){
                result.add(orderedRelations.get(i));
            }
        } 
        return result;
    }
}
