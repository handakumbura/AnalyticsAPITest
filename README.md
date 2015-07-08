# AnalyticsAPITest

If the test should start a das server and run the tests against it update daspack,packname, serverhost and set cleanup to true in pom file. Else set cleanup to false and set profile activeByDefault to false. 

linux dependencies,
cat, head, cut, xargs (tested on ubuntu 14.04)

admin service dependencies,
EventReceiverAdminService, EventStreamAdminService, EventStreamPersistenceAdminService as of DAS3.0 Alpha(18/06/2015)
