package factorybot4j.dsl;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class Factory<ObjectType> {
    private static final String MULTIPLE_CLASES_FOUND_ERROR_MSG_TEMPLATE = "found multiple classes for name %s";
    private static final String NO_CLASSES_FOUND_ERROR_MSG_TEMPLATE = "could not find any classes for name %s";
    private final @NonNull String name;

    private static final String ROOT_PKG_NAME = "factorybot4j";

    private Optional<Class> objectType = Optional.empty();

    public ObjectType build() throws IllegalAccessException, InstantiationException, IOException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
       final ObjectType product = objectType.isPresent() ? buildWithType(objectType.get()) : buildWithName();

       return invokeAfterCallbacks(FactoryBotOperation.BUILD, product);
    }
    
    @Getter(AccessLevel.PRIVATE)
    private final Map<String, KeyValuePair> objectFields = new HashMap<>();

    @Getter(AccessLevel.PRIVATE)
    private final Map<String, KeyValuePairs> propertyOverrides = new HashMap<>();

    @Getter(AccessLevel.PRIVATE)
    private final Map<String, KeyValuePair> transientFields = new HashMap<>();

    @Getter(AccessLevel.PRIVATE)
    private final Multimap<FactoryBotOperation, AfterCallback> afterCallbacks = LinkedListMultimap.create();

    /**
     * Used for chaining the DSL
     * @return
     */
    public Factory<ObjectType> and(){
        return this;
    }

    /**
     *
     * @param name
     * @return
     * @throws IOException
     */
    protected Set<Class> findClasses(String name) throws IOException {
        return ClassPath.from(getClass().getClassLoader()).getTopLevelClassesRecursive(ROOT_PKG_NAME)
                .stream().filter(info -> info.getSimpleName().equalsIgnoreCase(name))
                .map(info -> info.load())
                .collect(Collectors.toSet());
    }

    /**
     *
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws NoSuchFieldException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected ObjectType buildWithName() throws IllegalAccessException, InstantiationException,
            IOException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
        final Set<Class> classes = findClasses(name);

        if(classes.size() > 1)
            throw new IllegalArgumentException(String.format(MULTIPLE_CLASES_FOUND_ERROR_MSG_TEMPLATE, name));

        else if(classes.isEmpty())
            throw new IllegalArgumentException(String.format(NO_CLASSES_FOUND_ERROR_MSG_TEMPLATE, name));

        return buildWithType(classes.stream().findFirst().get());
    }

    /**
     *
     * @param type
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws NoSuchFieldException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    @SuppressWarnings("unchecked")
    ObjectType buildWithType(Class type) throws IllegalAccessException, InstantiationException,
            IOException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
        if(!objectType.isPresent()) objectType = Optional.of(type);
        return populateObject((ObjectType) type.newInstance());
    }

    /**
     * Populate an object
     * @param target
     * @return an object of type ObjectType
     * @throws IllegalAccessException
     * @throws IOException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected ObjectType populateObject(ObjectType target) throws IllegalAccessException, IOException,
            InstantiationException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
        for(KeyValuePair pair: getObjectFields().values()) {
            if(pair.getValue() instanceof Factory) populatePropertyUsingFactory(target, pair);
            else populateSimpleProperty(target, pair);
        }

        for (Map.Entry<String, KeyValuePairs> entry: getPropertyOverrides().entrySet())
            overrideNestedObjectProperty(target, entry.getKey(), entry.getValue());

        return target;
    }

    protected ObjectType populateSimpleProperty(ObjectType target, KeyValuePair pair) throws IllegalAccessException {
        FieldUtils.writeField(target, pair.getName(), pair.getValue(), true);
        return target;
    }

    protected ObjectType populatePropertyUsingFactory(ObjectType target, KeyValuePair pair)
            throws IllegalAccessException, IOException, InstantiationException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
        final Object computedValue = ((Factory)pair.getValue()).build();

        FieldUtils.writeField(target, pair.getName(), computedValue, true);
        return target;
    }

    /**
     *
     * @param target product
     * @param propertyName overridden property in nested object
     * @param keyValuePairs aggregate of key/value pairs
     * @return product
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected ObjectType overrideNestedObjectProperty(ObjectType target, String propertyName,
                                                      KeyValuePairs keyValuePairs)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object nestedObject = PropertyUtils.getProperty(target, propertyName);

        for(KeyValuePair pair: keyValuePairs.getPairs())
            PropertyUtils.setProperty(nestedObject, pair.getName(), pair.getValue());

        return target;
    }

    Factory<ObjectType> add(KeyValuePair pair){
        objectFields.put(pair.getName(), pair);
        return this;
    }

    public ValueExpected<ObjectType> key(String name){
        final KeyValuePair pair = new KeyValuePair(name, this);

        return new ValueExpected<>(this, pair);
    }

    public Factory<ObjectType> association(String propertyName){
        objectFields.put(propertyName, new KeyValuePair(propertyName, FactoryBot.findFactory(propertyName)));
        return this;
    }

    public Factory<ObjectType> association(String propertyName, FactoryName factoryName){
        objectFields.put(propertyName, new KeyValuePair(propertyName,
                FactoryBot.findFactory(factoryName.fetch())));

        return this;
    }

    public Factory<ObjectType> association(String propertyName, FactoryName factoryName, KeyValuePairs keyValuePairs){
        association(propertyName, factoryName);
        propertyOverrides.put(propertyName, keyValuePairs);
        return this;
    }

    public Factory<ObjectType> asTransient(KeyValuePairs keyValuePairs){
        for(KeyValuePair pair: keyValuePairs.getPairs()){
            if(objectFields.containsKey(pair.getName()))
                throw new IllegalArgumentException(String.format("property already exists with name %s as an " +
                        "object property", pair.getName()));

            transientFields.put(pair.getName(), pair);
        }

        return this;
    }

    public Factory<ObjectType> after(FactoryBotOperation operation, AfterCallback<ObjectType> callback){
        afterCallbacks.put(operation, callback);
        return this;
    }

    Factory<ObjectType> updateProperty(String propertyName, Object value){
        if(objectFields.containsKey(propertyName))
            objectFields.put(propertyName, new KeyValuePair(propertyName, value));
        else if(transientFields.containsKey(propertyName))
            transientFields.put(propertyName, new KeyValuePair(propertyName, value));
        else
            throw new IllegalArgumentException(String.format("unknown property name %s when attempting to update " +
                    "an object definition", propertyName));

        return this;
    }

    @SuppressWarnings("unchecked")
    public <ReturnType> ReturnType get(String propertyName){
        if(objectFields.containsKey(propertyName))  return (ReturnType) objectFields.get(propertyName).getValue();

        else if(transientFields.containsKey(propertyName))
            return (ReturnType) transientFields.get(propertyName).getValue();

        else throw new UnsupportedOperationException(String.format("property not found with name %s", propertyName));
    }

    public boolean isTransient(String propertyName){
        return transientFields.containsKey(propertyName);
    }

    @SuppressWarnings("unchecked")
    ObjectType invokeAfterCallbacks(FactoryBotOperation operation, ObjectType product){
        afterCallbacks.get(operation).forEach(callback -> callback.call(product, this));
        return product;
    }


    /**
     * Build an object that's not saved
     *
     * @param keyValuePairs key/value pairs to override the default key/value pairs in the factory definition
     * @return an object of type ObjectType
     */
    @SuppressWarnings("unchecked")
    ObjectType build(KeyValuePairs keyValuePairs) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, IOException, NoSuchFieldException, InstantiationException {
        final ObjectType product = this.build();

        for (KeyValuePair pair : keyValuePairs.getPairs()) {
            if (pair.getValue() instanceof Factory)
                throw new UnsupportedOperationException("build expects constant values");

            else if (!this.isTransient(pair.getName()))
                PropertyUtils.setProperty(product, pair.getName(), pair.getValue());

            else this.updateProperty(pair.getName(), pair.getValue());
        }

        return product;
    }

    ObjectType create(SaveStrategy<ObjectType> saveStrategy, KeyValuePairs keyValuePairs)
            throws IOException, NoSuchFieldException, InvocationTargetException, IllegalAccessException,
            InstantiationException, NoSuchMethodException {
        return this.invokeAfterCallbacks(FactoryBotOperation.CREATE, saveStrategy.save(this.build(keyValuePairs)));
    }

    ObjectType create(SaveStrategy<ObjectType> saveStrategy) throws IOException, NoSuchFieldException,
            InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        return this.invokeAfterCallbacks(FactoryBotOperation.CREATE, saveStrategy.save(this.build()));
    }
}
