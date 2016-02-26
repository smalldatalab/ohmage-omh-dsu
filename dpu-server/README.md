# DPU Server

This is a Spring Batch application that does the data processing to generate Content Items.

## Getting started

You can run the application locally with the following, and it requires a connection to MongoDB.

```
./gradlew :dpu-server:bootRun 
```

This will run the app continuously, and schedule the jobs in the "channel_scheduled_job" collection in the Mongo 
database. It will receive on-demand job requests at the HTTP endpoint `/api/jobs` with data of JSON `userId` and `jobName`.

A sample curl command to kickoff an on-demand job:

```
curl -H "Content-Type: application/json" \
-X POST \
-d '{"userId":"5696b569e4b0203a7f5f10be","jobName":"fitbitWeeklyStatsJob"}' \
http://localhost:8081/api/jobs
```

## Debugging and hot-swap code

The DPU app is configured so a remote debugger can attach on port 5006, if started with `bootRun`. It also uses 
the `springloaded` library, so files that java and groovy files that are compiled thru the IDE will be hot-swapped 
into the running app. 

To debug, start the app as described above.  In IntelliJ, go to Run > Edit Configurations, and add a new `Remote` 
configuration.  Set host to 'localhost' and change port to '5006'.  You can then run this configuration, and it should
attach.  You can add breakpoints to verify.

For hot-swapping, once you have modified a java or groovy file, go to Build > Compile File.java, so it compiles just 
the modified file.  These changes should then be reflected in the running app.
