package ch.zhaw.catan;

import ch.zhaw.catan.Config.*;

import java.util.HashMap;
import java.util.List;

/**
 * This class represents the bank and manages its stock.
 *
 * @author StackOverflow
 * @version 1.0
 */
public class Bank extends Participant {

    /**
     * This constructor initializes the bank. The resource stock is initialized with {@link Config#INITIAL_RESOURCE_CARDS_BANK}.
     */
    public Bank() {
        super(new HashMap<>(Config.INITIAL_RESOURCE_CARDS_BANK));
    }

    /**
     * This method adds resources according to the cost of a structure.
     *
     * @param structure the structure of which we want to add the cost.
     */
    public void addResourcesForStructure(Structure structure) {
        List<Resource> costs = structure.getCosts();
        for (Resource resource : costs) {
            addResource(resource, 1);
        }
    }

    /**
     * Removes resources for the payout of a given structure from the bank stock.
     *
     * @param resourceType Type of resource being removed from stock.
     * @param structure    Type of structure being removed back to stock.
     * @return True if the resource was removed, false if the resource stock is empty.
     */
    public boolean removePayoutForStructure(Resource resourceType, Structure structure) {
        int resourceCount = resources.get(resourceType);

        int needed = 1;
        if (structure == Structure.CITY) {
            needed = 2;
        }

        if (resourceCount < needed) {
            return false;
        }
        resources.put(resourceType, resourceCount - needed);
        return true;
    }
}