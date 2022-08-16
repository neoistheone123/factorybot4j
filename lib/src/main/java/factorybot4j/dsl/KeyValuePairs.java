package factorybot4j.dsl;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * Aggregate of key/value pairs
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class KeyValuePairs {

    private final List<KeyValuePair> pairs = new LinkedList<>();

    public KeyValuePairs with(String name, Object value){
        pairs.add(new KeyValuePair(name, value));
        return this;
    }
}
