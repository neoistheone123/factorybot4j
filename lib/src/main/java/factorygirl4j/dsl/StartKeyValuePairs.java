package factorygirl4j.dsl;

/**
 * DSL to start the chain of key/value pairs
 */
public interface StartKeyValuePairs {

    /**
     * Return an aggregate of key/value pairs
     * @param name key name
     * @param value key value
     * @return KeyValuePairs aggregate
     */
    static KeyValuePairs with(String name, Object value){
        return new KeyValuePairs().with(name, value);
    }
}
