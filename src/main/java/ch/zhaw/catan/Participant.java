package ch.zhaw.catan;

import ch.zhaw.catan.Config.Resource;

import java.util.Map;

/**
 * This class represents participants of the game. Participants are all instances which have a stock of {@link Resource}.
 * This class is a super class of {@link Player} and {@link Bank}.
 *
 * @author StackOverflow
 * @version 1.0
 */
public abstract class Participant {

    /**
     * This map represents the resources of a participant. Every subclass of Participant has can access their resources.
     */
    protected final Map<Resource, Integer> resources;

    /**
     * This method initializes the  Participant class.
     *
     * @param resources the resources a participant already has from the start.
     */
    public Participant(Map<Resource, Integer> resources) {
        this.resources = resources;
    }

    /**
     * This function adds a resource back to the stock.
     *
     * @param resource resource that is added back to the stock.
     * @param count    amount of the resource given back.
     */
    public void addResource(Resource resource, int count) {
        int current = resources.get(resource);
        resources.put(resource, current + count);
    }

    /**
     * Removes a resource from the stock.
     *
     * @param resource resource to remove from the stock.
     * @param count    amount of this resource type to be removed.
     * @return True if the resource was removed, false if the resource stock is empty or doesn't have enough resources.
     */
    public boolean removeResource(Resource resource, int count) {
        int current = resources.get(resource);
        if (current < count) {
            return false;
        }
        resources.put(resource, current - count);
        return true;
    }

    public Map<Resource, Integer> getResources() {
        return resources;
    }

    /**
     * Returns the count of the given resource.
     *
     * @param resource The type of resource for which the count should be returned.
     * @return Count of the given resource.
     */
    public int getResourceCount(Resource resource) {
        return resources.get(resource);
    }

    /**
     * Returns the total resource count.
     *
     * @return the total resources available.
     */
    public int getTotalResourceCount() {
        int total = 0;
        for (Resource resource : resources.keySet()) {
            total += resources.get(resource);
        }
        return total;
    }

    /**
     * This method evaluates whether the participant has any resources.
     *
     * @return true, when the participant has at least one resource.
     */
    public boolean hasResources() {
        return getTotalResourceCount() > 0;
    }
}