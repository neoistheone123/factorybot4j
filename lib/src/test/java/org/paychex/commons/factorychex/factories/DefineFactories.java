package org.paychex.commons.factorychex.factories;

import org.paychex.commons.factorychex.domain.Post;
import org.paychex.commons.factorychex.domain.User;
import org.paychex.commons.factorychex.dsl.FactoryChex;

import static org.paychex.commons.factorychex.dsl.DefineFactoryWithNameAndClass.factory;
import static org.paychex.commons.factorychex.dsl.DefineFactoryWithNameOnly.factory;
import static org.paychex.commons.factorychex.dsl.FactoryChexOperation.BUILD;
import static org.paychex.commons.factorychex.dsl.FactoryChexOperation.CREATE;
import static org.paychex.commons.factorychex.dsl.SpecifyFactoryName.factoryName;
import static org.paychex.commons.factorychex.dsl.StartKeyValuePairs.with;

/**
 * Factory definitions MUST be in a static block
 */
public class DefineFactories {

    static {
        FactoryChex.define(
            factory("user", User.class)
                .key("firstName").value("John")
                .key("lastName").value("Doe")
                .key("email").value("test@example.com")
        );

        FactoryChex.define(
                factory("author", User.class)
                    .key("firstName").value("John")
                    .key("lastName").value("Doe")
                    .key("email").value("test@example.com")
        );


        FactoryChex.define(
            factory("otherclass")
                .key("mykey").value("myvalue")
                .key("mykey2").value("myvalue2")
        );

        FactoryChex.define(
                factory("post")
                    .association("author")
        );

        FactoryChex.define(
                factory("postByFactory", Post.class)
                    .association("author", factoryName("user"), with("lastName", "Writely"))
        );

        FactoryChex.define(
                factory("oneBuildCallback", User.class)
                        .key("firstName").value("John")
                        .and()
                        .asTransient(
                                with("upcased", true)
                        )
                        .after(BUILD, (product, evaluator) -> {

                            if(evaluator.get("upcased")){
                                product.setFirstName(product.getFirstName().toUpperCase());
                            }
                        })
        );

        FactoryChex.define(
                factory("twoBuildCallbacks", User.class)
                        .key("firstName").value("John")
                        .and()
                        .asTransient(
                                with("upcased", true)
                        )
                        .after(BUILD, (product, evaluator) -> {

                            if(evaluator.get("upcased")){
                                product.setFirstName(product.getFirstName().toUpperCase());
                            }
                        })
                        .after(BUILD, (product, evaluator) -> {
                            product.setLastName(product.getFirstName());
                        })
        );

        FactoryChex.define(
                factory("oneCreateCallback", User.class)
                        .key("firstName").value("John")
                        .and()
                        .asTransient(
                                with("upcased", true)
                        )
                        .after(CREATE, (product, evaluator) -> {

                            if(evaluator.get("upcased")){
                                product.setFirstName(product.getFirstName().toUpperCase());
                            }
                        })
        );

        FactoryChex.define(
                factory("mixedCallbacks", User.class)
                        .key("firstName").value("John")
                        .and()
                        .asTransient(
                                with("upcased", true)
                        )
                        .after(BUILD, (product, evaluator) -> {

                            if(evaluator.get("upcased")){
                                product.setFirstName(product.getFirstName().toUpperCase());
                            }
                        })
                        .after(CREATE, (product, evaluator) -> {
                            product.setLastName(product.getFirstName());
                        })
        );
    }
}
