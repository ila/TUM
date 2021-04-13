package algorithms;

import datastructure.*;

public class IKKBZ {

    public void performIKKBZ(PrecedenceNode root){
        //todo
        root.printSubTree();

        PrecedenceNode lNC = root.lowestNonChain(root, null);
        while(lNC != null){
            System.out.println("New Lowest Node with only chains as children:" + lNC.root.name);
            System.out.println("Normalizing:" + lNC.root.name);
            lNC.normalize(lNC, lNC);
            System.out.println("Merging Subtrees:" + lNC.root.name);
            lNC.mergeTrees();
            lNC = root.lowestNonChain(root, null);
        }
    }
}
