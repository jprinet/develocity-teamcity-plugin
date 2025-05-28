Build the custom Develocity extension
=====================================

Run the following command to build the custom Develocity extension:
```bash
mvn -f custom-develocity-extension clean package clean package
```


Build the TeamCity plugin
=========================

Run the following command to build the TeamCity plugin:
```bash
./gradlew serverPlugin
```

Installation
============

In the Admin => Plugins section of TeamCity:
- Disable and unload the previous version of the plugin *Develocity integration for Gradle and Maven builds*
- Delete the plugin *Develocity integration for Gradle and Maven builds*
- Upload plugin zip from the build directory `build/distributions/develocity-teamcity-plugin-*.zip`
- Enable uploaded plugin

Configuration
============

In the Project settings:
- **Delete previous Develocity connection**
- Add a new connection of type *Develocity*
- Configure the connection with the following parameters:
  - Develocity Server URL
  - Develocity Access Key (format host=key)
  - Enable Develocity auto-injection
  - Configure Maven Repository URL where the custom Develocity extension is located (ie. `https://binary-manager.com/maven-repo`)
  - Configure Maven Repository Username if required
  - Configure Maven Repository Password if required
  - Configure custom Develocity extension coordinates (ie. `com.myorg:convention-develocity-maven-extension:1.0-SNAPSHOT`)

Build parameters
================

- Enable debug logging for the plugin:
`develocityPlugin.develocity.extension.logger.enabled=true`
- Configure full download URL of the custom extension:  
`develocityPlugin.develocity.extension.downloadUrl=https://foo.bar/extension.jar`
- Force extension file overwrite if present
`develocityPlugin.develocity.extension.overwrite=true`