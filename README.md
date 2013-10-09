Omnisens
===========================

A simple Android app that can be used to disseminate news based on location. This is team Hastagqq's entry for Webgeek Devcup 2013. By itself, it's not a useful app because:

* It lacks features.
* I killed the server.
* No user accounts.

But it's useful as reference for the following topics:

* Android location API (v1)
* Tornado REST webservice
* Tornado with SQLAlchemy
* Consuming REST webservices in Android
* Proper marshalling of JSON to Java Objects using GSON
* Android SQLite caching. Well, not really, we just load the results into the ListView directly from the response because we ran out of time.
* Google Cloud Messaging
* AsyncTask + ResultReceiver pattern for consuming web services.

Features that are currently working are:

* Posting a news article.
* News feeds.
* When another user enters the area where a news article was posted, that user will be notified of new news items.
* When a user posts a news article in the same area as another user, the other user will be notified of a new news item.
* Cities as location boundaries.
