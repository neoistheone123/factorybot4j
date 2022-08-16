package factorybot4j.factories;

import factorybot4j.domain.Post;
import factorybot4j.domain.User;
import factorybot4j.dsl.FactoryBot;

import static factorybot4j.dsl.DefineFactoryWithNameAndClass.factory;
import static factorybot4j.dsl.DefineFactoryWithNameOnly.factory;
import static factorybot4j.dsl.FactoryChexOperation.BUILD;
import static factorybot4j.dsl.FactoryChexOperation.CREATE;
import static factorybot4j.dsl.SpecifyFactoryName.factoryName;
import static factorybot4j.dsl.StartKeyValuePairs.with;

/**
 * Factory definitions MUST be in a static block
 */
public class DefineFactories {

    static {
        FactoryBot.define(
            factory("user", User.class)
                .key("firstName").value("John")
                .key("lastName").value("Doe")
                .key("email").value("test@example.com")
        );

        FactoryBot.define(
                factory("author", User.class)
                    .key("firstName").value("John")
                    .key("lastName").value("Doe")
                    .key("email").value("test@example.com")
        );


        FactoryBot.define(
            factory("otherclass")
                .key("mykey").value("myvalue")
                .key("mykey2").value("myvalue2")
        );

        FactoryBot.define(
                factory("post")
                    .association("author")
        );

        FactoryBot.define(
                factory("postByFactory", Post.class)
                    .association("author", factoryName("user"), with("lastName", "Writely"))
        );

        FactoryBot.define(
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

        FactoryBot.define(
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

        FactoryBot.define(
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

        FactoryBot.define(
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
