# Common Library for One NCP

Common Library is a collection of bean and service useful within applications built within the
NCP service.

## Versioning
maxnotificationcommon uses [Semantic Versioning](http://semver.org) to convey information about changes.  For a detailed list of
changes in each release, please consult the [Version History](CHANGES.md).


## Building The Library
The service can be built with the following commands:

```
$ ./gradlew clean build
```


## Include the Library in Your Project
If you just want to include the library in your project, you can add this to your gradle script:

```
implementation("com.nike.gc.ncp:ncp-common:1.0.+")
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
# cerberus
cerberus.url=http://localhost:5006
cerberus.region=cn-north-1

# signalfx
nike.signalfx.registry.enableSilentModeForLocalDev=true
nike.signalfx.distributed-tracing.output-enabled=false
nike.signalfx.registry.registerJvmMetrics=false
nike.signalfx.registry.logMetricNames=false

# jackson timestamp
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.date-format=yyyy-MM-dd'T'HH:mm:ss'Z'

# mongo
mongo.connection.string=mongodb://localhost:27017/ncp
mongo.db.name=ncp
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
List entities with optional page, size and sorting
```
public JourneyList getJourneyList(int page, int size, String sort, String order,
                                      String campaignId, String subCampaignId, String name,
                                      String status, Date nextStartTimeBegin, Date nextStartTimeEnd,
                                      UserHeader user)
PageQueryBuilder query = PageQueryBuilder.of(page, size).sort(sort, order).exclude("excludedColumnName")
                .eq(CAMPAIGN_ID, campaignId)
                .eq(SUB_CAMPAIGN_ID, subCampaignId)
                .eq(STATUS, status)
                .like(NAME, name)
                .gte(NEXT_START_TIME, nextStartTimeBegin)
                .lte(NEXT_START_TIME, nextStartTimeEnd);
long total = mongoTemplate.count(query.getCountQuery(), Journey.class);
List<Journey> journeys = mongoTemplate.find(query.getQuery(), Journey.class);
JourneyList journeyList = JourneyList.builder()
        .page(Page.builder().size(size).total(total).current(page).build())
        .data(journeys).build();
```
List all entities
```
List<Journey> list = mongoTemplate.find(PageQueryBuilder.all().getQuery(), Journey.class);

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