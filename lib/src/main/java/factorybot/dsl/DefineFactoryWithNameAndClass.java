package factorybot4j.dsl;

import java.util.Optional;

/**
 * DSL to define a factory using a name and type
 */
public interface DefineFactoryWithNameAndClass {
    static <ObjectType> Factory<ObjectType> factory(String name, Class<ObjectType> clazz){
       return new Factory<ObjectType>(name).setObjectType(Optional.ofNullable(clazz));
    }
}
