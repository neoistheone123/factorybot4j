package org.paychex.commons.factorychex.dsl;

/**
 * DSL to define a factory using name only
 */
public interface DefineFactoryWithNameOnly {
    static Factory factory(String name){
        return new Factory(name);
    }
}
