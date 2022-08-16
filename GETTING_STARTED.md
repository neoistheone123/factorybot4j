# Getting Started

## Configure JUnit 5
___
1. Register a custom extension(factorybot4j.junit5.LoadFactoryBotExtension) supplying its fully qualified class name in a file named org.junit.jupiter.api.extension.Extension within the /META-INF/services folder in the test module. See this project's test module for an example.
2. Create the JUnit Platform configuration file: a file named junit-platform.properties in the root of the class path that follows the syntax rules for a Java Properties file.
3. Add this to junit-platform.properties:
   junit.jupiter.extensions.autodetection.enabled=true

## Defining factories
___
Each factory has a name and a set of attributes. The name is used to guess the class of the object by default, but it's possible to explicitly specify it:

```
// This will guess the User class
FactoryBot.define(
   factory("user")
      .key("firstName").value("John")
      .key("lastName").value("Doe")
)
```

```
// This will use the User class (Admin would have been guessed)
FactoryBot.define(
   factory("admin", User.class)
      .key("firstName").value("Admin")
      .key("lastName").value("User")
      .key("admin").value(true)
)
```

It is highly recommended that you have one factory for each class that provides the simplest set of attributes necessary to create an instance of that class.

Attempting to define multiple factories with the same name will raise an error.

Factories can be defined anywhere, but will be automatically loaded after calling FactoryBot.findDefinitions if factories are defined in files at the following locations:

`
test/java/factorybot4j/factories/**/*.java
`

## Using factories
___
factorybot4j supports the following different build strategies: build, create

```
// Returns a User instance that's not saved
user = build("user")
```

```
// Returns a saved User instance
SaveStrategy saveStrategy = (entity) -> ...
user = create("user", saveStrategy)
```

No matter which strategy is used, it's possible to override the defined attributes by passing a hash:


```
// Build a User instance and override the first_name property
user = build("user", with("firstName", "Joe"))
user.getFirstName()
// => "Joe" 
```

### Transient Attributes
___
There may be times where your code can be DRYed up by passing in transient attributes to factories.

```
FactoryBot.define(
   factory("user", User.class)
      .key("firstName").value("John")
      .key("lastName").value("Doe")
      .key("email").value("test@example.com")
      .and()
      .asTransient(
         with("upcased", false)
      )
      .and()
      .after(BUILD, (product, evaluator) -> {
         if(evaluator.get("upcased")) product.setFirstName(product.getFirstName().toUpperCase());
      })
)
```

### Associations
It's possible to set up associations within factories. If the factory name is the same as the association name, the factory name can be left out.

```
FactoryBot.define(
   factory("post")
      .association("author", factoryName("user"), with("lastName", "Writely")
)
```

### Callbacks
___
factorybot4j makes available four callbacks for injecting some code:
* after(BUILD) - called after a factory is built (via FactoryBot.build, FactoryBot.create)
* after(CREATE) - called after a factory is saved (via FactoryBot.create)

Examples:

```
FactoryBot.define(
   factory("user")
      .key("firstName").value("John")
      .after(BUILD, (product, evaluator) -> {
           product.setFirstName(product.getFirstName().toLowerCase());
       })
)
```

You can also define multiple types of callbacks on the same factory:

```
FactoryBot.define(
   factory("user")
      .key("firstName").value("John")
      .key("lastName").value("Doe")
      .after(BUILD, (product, evaluator) -> {
           product.setFirstName(product.getFirstName().toLowerCase());
       })
       .after(CREATE, (product, evaluator) -> {
           product.setLastName(product.getLastName().toLowerCase());
       })
)
```

Factories can also define any number of the same kind of callback. These callbacks will be executed in the order they are specified:

```
FactoryBot.define(
   factory("user")
      .key("firstName").value("John")
      .key("lastName").value("Doe")
      .after(CREATE, (product, evaluator) -> {
           product.setFirstName(product.getFirstName().toLowerCase());
       })
       .after(CREATE, (product, evaluator) -> {
           product.setLastName(product.getLastName().toLowerCase());
       })
)
```

Calling create will invoke both after(BUILD) and after(CREATE) callbacks.