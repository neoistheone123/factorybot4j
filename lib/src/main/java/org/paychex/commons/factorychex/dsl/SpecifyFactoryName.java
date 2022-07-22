package org.paychex.commons.factorychex.dsl;

/**
 * DSL to start the indication of a factory name
 */
public interface SpecifyFactoryName {
    static FactoryName factoryName(String name){
        return () -> name;
    }
}
