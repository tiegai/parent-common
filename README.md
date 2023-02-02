# Common Library for One NCP

Common Library is a collection of bean and service useful within applications built within the
NCP service.

## Versioning
ncp-common uses [Semantic Versioning](http://semver.org) to convey information about changes.  For a detailed list of
changes in each release, please consult the [Version History](CHANGES.md).


## Building The Library
The service can be built with the following commands:

```
$ ./gradlew clean build
```

## Publishing to Maven nexus
Publish rootProject and all subProjects to local Maven repo to test it out, first
```sh
$ ./gradlew clean publishToMavenLocal
```
then publish them to the remote Maven nexus
```sh
$ ./gradlew clean publish
```

## Include the Library in Your Project
If you just want to include the library in your project, you can add this to your gradle script:

```
implementation("com.nike.gc.ncp:ncp-common:1.0.x.x")
```

## Local MongoDb docker
Include below block in your docker-compose.yml
```
  mongo:
    image: mongo:4
    ports:
      - "27017:27017"
```

# Required properties
Include correct properties in your project.

- application-local.properties
- application-prod.properties
- application-test.properties

```
# jackson timestamp
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.date-format=yyyy-MM-dd'T'HH:mm:ss'Z'

# mongo
spring.data.mongodb.uri=mongodb://localhost:27017/ncp
```
####
Enable mongo ssl connection,If your local environment doesn't need ssl connection, you can turn it off and set it to false.
```
spring.data.mongodb.ssl.open = true
```

#### Enable mongo repositories scan
```
...
@ComponentScan(basePackages = {"com.nike"})
@EnableMongoRepositories(basePackages = "com.nike.ncp.journeybuilder")
...
public class Application {
```

# Exception example
ApiExceptionHandler will throw generic error response automatically.
```
public class YourService {

    public void serviceMethodName(YourModel model) {
        // 404
        throw ApiExceptions.itemNotFound();
        // 400
        throw ApiExceptions.invalidRequest()
                   .with(1234, "Your error description");
                   .with(2345, "Another error description");
        // 403
        throw ApiExceptions.accessDenied();
        // 500
        throw new RuntimeException(xxx);
    }
}
```
# MongoDB CRUD example
Define entity JPA repository class
```
package com.nike.ncp.journeybuilder.repository;

public interface JourneyRepository extends MongoRepository<Journey, String> {
}
```
Scan repository package in Application
```
@EnableMongoRepositories(basePackages = "com.nike.ncp.journeybuilder")
public class Application {
```
Inject mongo template or JPA repository class
```
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final MongoTemplate mongoTemplate;
```
Get an entity
```
Optional<Journey> journey = journeyRepository.findById(journeyId);
if (journey.isEmpty()) {
    throw ApiExceptions.itemNotFound();
} else {
    return journey.get();
}
```
Create
```
journey.setCreatedBy(user.getUserId());
journey.setCreatedByName(user.getUserName());
journey.setCreatedTime(Date.from(Instant.now()));
return journeyRepository.save(journey);

```
Update
```
journey.setUpdatedBy(user.getUserId());
journey.setUpdatedByName(user.getUserName());
journey.setUpdatedTime(Date.from(Instant.now()));
return journeyRepository.save(journey);

```
Delete
```
journeyRepository.deleteById(journey.getId());
```