Contributions
=============

# Build the custom extension

```bash
mvn -f custom-develocity-extension clean package clean package
```

# Build the TeamCity plugin

```bash
./gradlew serverPlugin
```

# Upload the plugin into TeamCity

Collect the plugin from the build directory:
```bash
build/distributions/develocity-teamcity-plugin-1.0-prerelease.zip
```

# Configure a TeamCity connection for the project

Fill the following sections
- Develocity Server URL
- Develocity Access Key (format host=key)

Tick
- Enable Develocity auto-injection
