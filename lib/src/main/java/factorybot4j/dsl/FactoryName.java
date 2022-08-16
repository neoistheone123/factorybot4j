package factorybot4j.dsl;

/**
 * DSL to indicate a factory name
 */
@FunctionalInterface
public interface FactoryName {
    String fetch();
}
