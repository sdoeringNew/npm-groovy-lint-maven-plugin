# npm-groovy-lint-maven-plugin

[![Java CI with Maven](https://github.com/sdoeringNew/npm-groovy-lint-maven-plugin/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/sdoeringNew/npm-groovy-lint-maven-plugin/actions/workflows/maven.yml)

A maven plugin that uses and bundles the [npm-groovy-lint](https://github.com/nvuillam/npm-groovy-lint) module.

This plugin can reformat your Groovy code from the command line via Maven.

No installed Node.js is required, so you can use this plugin in your CI pipelines.
The plugin will handle the download and installation of the required Node.js itself.

### Minimal config example

Format all Groovy files recursively in ``${project.basedir}``, but fail if there's an error found.

```xml
<plugin>
    <groupId>com.github.sdoering</groupId>
    <artifactId>npm-groovy-lint-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <executions>
        <execution>
            <goals>
                <goal>format</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### More complex example

Format all .groovy files recursively once in ``src/test/groovy``, automatically fix them if possible and fail already in case of found warnings.

```xml
<plugin>
    <groupId>com.github.sdoering</groupId>
    <artifactId>npm-groovy-lint-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <inherited>false</inherited>
    <configuration>
        <failOn>warning</failOn> <!-- possible values: none, error, warning, info -->
        <fix>true</fix> <!-- possible values: true, false -->
        <targets>${project.basedir}/**/src/test/groovy/**/*.groovy</targets> <!-- space separated list of files or directories -->
    </configuration>
    <executions>
        <execution>
            <phase>validate</phase>
            <goals>
                <goal>format</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Format from the command line example

Call format for the current and all inherited submodules.

```bash
mvn com.github.sdoering:npm-groovy-lint-plugin:format
```

Use the ``-pl :___INSERT_MODULE_NAME___`` option to format a specific module.

## Building

```bash
mvn clean install
```