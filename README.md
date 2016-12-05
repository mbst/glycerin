glycerin
========

A Java HTTP client for Nitro

Usage
-----

## Add a maven dependency:
```
       <dependency>
          <groupId>com.metabroadcast.atlas.glycerin</groupId>
          <artifactId>glycerin</artifactId>
          <version>2.1.4</version>
      </dependency>
 ```
You'll need the MetaBroadcast repository: `http://mvn.metabroadcast.com/all`

```
 <repositories>   
        <repository>
            <id>Nitro</id>
            <url>http://mvn.metabroadcast.com/all</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
</repositories>
 ```


## Create a Glycerin instance: 
```java
Glycerin glycerin = XmlGlycerin.builder(apiKey).build();

//with specified host
Glycerin glycerin = XmlGlycerin.builder(apiKey)
                .withHost(HostSpecifier.fromValid(host))
                .build();

```

## Execute a query: 
```java
GlycerinResponse<Broadcast> broadcasts = glycerin.execute(BroadcastQuery.builder() .withDescendantsOf("b039gr8y").build());
```

### ProgrammesQuery
```java

//find all programs available
GlycerinResponse<Programme> programmes = glycerin.execute(ProgrammesQuery.builder().
                                         withDescendantsOf(pid).
                                         withAvailability(AvailabilityOption.AVAILABLE).build());
                                         
//sort all available programs by scheduled start descending
GlycerinResponse<Programme> programmes = glycerin.execute(ProgrammesQuery.builder().
                                         withDescendantsOf(pid).
                                         withAvailability(AvailabilityOption.AVAILABLE).
                                         sortBy(ProgrammesSort.SCHEDULED_START, ProgrammesSortDirection.DESCENDING).build());
                                         
```

Compiling
---------

Glycerin is built with [gradle](http://gradle.org "Gradle"). In the glycerin directory:

* `gradle compileJava` will generate and compile glycerin.
* `gradle install` will install maven artifacts into a local mvn repo.

Code is generated from:

* the XML schema via XJC. The bindings are regenerated using the `generateXmlSource` task from the `nitro-schema.xsd`.  
* From the API description using the query generator in the _src/queries/java_ package. The query sources are generated using the `generateQueries` task using `api.xml`. The description can be updated via the `fetchApiDescription` task with an API key.

Updating
--------

When you need to update to newer versions

 * The XSD schema can be found under `/nitro/api/schema`; fetch the new one into `nitro-schema.xsd`
 * The api query schema can be under `/nitro/api`; fetch the new one into `api.xml`
