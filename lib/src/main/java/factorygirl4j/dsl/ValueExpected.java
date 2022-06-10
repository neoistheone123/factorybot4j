package factorygirl4j.dsl;

import lombok.Value;

/**
 * DSL to specify a key value
 * @param <ObjectType> type of object
 */
@Value
public class ValueExpected<ObjectType> {

    Factory<ObjectType> objectDefinition;
    KeyValuePair keyValuePair;

    /**
     * Provide a key value
     * @param value key value
     * @return Factory&lt;ObjectType&gt;
     */
    public Factory<ObjectType> value(Object value){
        keyValuePair.setValue(value);
        objectDefinition.add(keyValuePair);
        return objectDefinition;
    }
}
