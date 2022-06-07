package factorygirl4j.dsl;

import lombok.Data;
import lombok.NonNull;

/**
 * Represents a key/value pair
 */
@Data
class KeyValuePair {
    @NonNull private final String name;
    Object value;

    public KeyValuePair(String name, Object value){
        this.name=name;
        this.value=value;
    }
}
