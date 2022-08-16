package factorybot4j.dsl;

/**
 * User-specified save strategy
 * @param <ObjectType> type of object
 */
@FunctionalInterface
public interface SaveStrategy<ObjectType> {

    /**
     * Save an object
     * @param object object to save
     * @return the saved object
     */
    ObjectType save(ObjectType object);
}
