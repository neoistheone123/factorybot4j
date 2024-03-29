package factorybot4j.dsl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Operations on a factory
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum FactoryBotOperation {
    BUILD("build"), CREATE("create");

    private @NonNull final String label;
}
