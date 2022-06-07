package factorygirl4j.factories;

import factorygirl4j.domain.Post;
import factorygirl4j.domain.User;
import factorygirl4j.dsl.FactoryGirl;

import static factorygirl4j.dsl.DefineFactoryWithNameAndClass.factory;
import static factorygirl4j.dsl.DefineFactoryWithNameOnly.factory;
import static factorygirl4j.dsl.FactoryGirlOperation.BUILD;
import static factorygirl4j.dsl.FactoryGirlOperation.CREATE;
import static factorygirl4j.dsl.SpecifyFactoryName.factoryName;
import static factorygirl4j.dsl.StartKeyValuePairs.with;

/**
 * Factory definitions MUST be in a static block
 */
public class DefineFactories {

    static {
        FactoryGirl.define(
            factory("user", User.class)
                .key("firstName").value("John")
                .key("lastName").value("Doe")
                .key("email").value("test@example.com")
        );

        FactoryGirl.define(
                factory("author", User.class)
                    .key("firstName").value("John")
                    .key("lastName").value("Doe")
                    .key("email").value("test@example.com")
        );


        FactoryGirl.define(
            factory("otherclass")
                .key("mykey").value("myvalue")
                .key("mykey2").value("myvalue2")
        );

        FactoryGirl.define(
                factory("post")
                    .association("author")
        );

        FactoryGirl.define(
                factory("postByFactory", Post.class)
                    .association("author", factoryName("user"), with("lastName", "Writely"))
        );

        FactoryGirl.define(
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

        FactoryGirl.define(
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

        FactoryGirl.define(
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

        FactoryGirl.define(
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
