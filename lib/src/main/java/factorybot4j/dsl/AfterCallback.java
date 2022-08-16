package factorybot4j.dsl;

/**
 * Called after an object is built/created
 * @param <ObjectType> type of object
 */
public interface AfterCallback<ObjectType> {
    void call(ObjectType product, Factory<ObjectType> factory);
}
