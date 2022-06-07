package factorygirl4j.dsl;

/**
 * DSL to start the indication of a factory name
 */
public interface SpecifyFactoryName {
    static FactoryName factoryName(String name){
        return () -> name;
    }
}
