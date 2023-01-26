package factorybot4j.dsl;

import com.google.common.reflect.ClassPath;
import factorybot4j.exception.FactoryBotException;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Namespace for FactoryGirl. Contains factory definitions. Used to build/create objects.
 */
@Value
public class FactoryBot {

    private static final String FACTORY_ALREADY_EXISTS_ERROR_MSG_TEMPLATE = "factory named %s already exists";
    private static final String FACTORY_DOES_NOT_EXIST_ERROR_MSG_TEMPLATE = "factory named %s does not exist";
    private static final String FACTORY_NAME_IS_BLANK_ERROR_MSG = "factory name is blank";
    private static final String BASE_PACKAGE_FOR_FACTORIES = "factorybot4j.factories";
    private static final Map<String, Factory> factories = new ConcurrentHashMap<>();

    /**
     * Define a factory for lookup
     * @param factory factory definition
     */
    public static void define(Factory factory){
        try {
            if(factories.containsKey(factory.getName()))
                throw new IllegalStateException(String.format(FACTORY_ALREADY_EXISTS_ERROR_MSG_TEMPLATE,
                        factory.getName()));

            factories.put(factory.getName(), factory);
        }
        catch (Exception exception){
            throw new FactoryBotException(exception);
        }
    }

    /**
     * Build(i.e. instantiate and initialize) an object
     * @param name factory name
     * @return an object of type ObjectType
     * @param <ObjectType> type of object
     */
    @SuppressWarnings("unchecked")
    public static <ObjectType> ObjectType build(String name){
        try {
            return (ObjectType)findFactory(name).build();
        }
        catch (Exception exception){
            throw new FactoryBotException(exception);
        }
    }

    /**
     * Build(i.e. instantiate and initialize) an object and override properties
     * @param name
     * @param keyValuePairs
     * @return
     * @param <ObjectType>
     */
    @SuppressWarnings("unchecked")
    public static <ObjectType> ObjectType build(String name, KeyValuePairs keyValuePairs){
        try {
            return (ObjectType)findFactory(name).build(keyValuePairs);
        }
        catch (Exception ex){
            throw new FactoryBotException(ex);
        }
    }

    /**
     * Create(i.e. build and then save) an object
     * @param name factory name
     * @param saveStrategy user-specified strategy to save the object
     * @return an object of type ObjectType
     * @param <ObjectType> type of object
     */
    @SuppressWarnings("unchecked")
    public static <ObjectType> ObjectType create(String name, SaveStrategy saveStrategy){
        try {
            return (ObjectType)findFactory(name).create(saveStrategy);
        }
        catch (Exception ex){
            throw new FactoryBotException(ex);
        }
    }

    /**
     * Returns a saved object
     *
     * @param name factory name
     * @param keyValuePairs key/value pairs to override the default key/value pairs in the factory definition
     * @param saveStrategy user-specified save strategy to save the object before it's returned
     * @return an object of type ObjectType
     * @param <ObjectType> type of object
     */
    @SuppressWarnings("unchecked")
    public static <ObjectType> ObjectType create(String name,
                                                 SaveStrategy saveStrategy, KeyValuePairs keyValuePairs){
        try {
            return (ObjectType)findFactory(name).create(saveStrategy, keyValuePairs);
        }
        catch (Exception ex){
            throw new FactoryBotException(ex);
        }
    }


    /**
     * Find factory by name
     * @param name factory name
     * @return Factory definition
     * @throws IllegalArgumentException when name is blank or name is not found
     */
    static Factory findFactory(String name){
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException(FACTORY_NAME_IS_BLANK_ERROR_MSG);

        else if (!factories.containsKey(name))
            throw new IllegalArgumentException(String.format(FACTORY_DOES_NOT_EXIST_ERROR_MSG_TEMPLATE, name));

        return factories.get(name);
    }

    /**
     * Find and initialize factories
     * @throws FactoryBotException
     */
    public static void findDefinitions() {
        try {
            final List<Class> clazzes = ClassPath.from(FactoryBot.class.getClassLoader())
                    .getTopLevelClassesRecursive(BASE_PACKAGE_FOR_FACTORIES)
                    .stream()
                    .map(info -> info.load()).collect(Collectors.toList());

            for(Class clazz: clazzes) clazz.newInstance();
        }
        catch (Exception ex){
            throw new FactoryBotException(ex);
        }
    }
}
