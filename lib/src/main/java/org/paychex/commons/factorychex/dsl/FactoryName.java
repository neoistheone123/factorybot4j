package org.paychex.commons.factorychex.dsl;

/**
 * DSL to indicate a factory name
 */
@FunctionalInterface
public interface FactoryName {
    String fetch();
}
