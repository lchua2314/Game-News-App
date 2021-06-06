# Game News App

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
1. [Schema](#Schema)

## Overview
### Description
Scrolls through various video articles and movie news articles, the latest articles reviews, top articles reviews, and random videos provided by the [GameSpot API](https://www.gamespot.com/api/).

### App Evaluation
- **Category:** Video Games / Movies
- **Story:** Displays the latest articles based on video articles and movies, and the top and latest articles reviews. The user can decide what articles, reviews, or videos to view in more detail.
- **Market:** Any individual could use this app, but the majority of users will most likely be gamers.
- **Habit:** This app could be used as often or unoften as the user wanted depending on how often their interest in the latest articles articles and reviews.
- **Scope:** The app will attempt to be placed on the Google Play Store for non-commercial use, however after that the app will not receive updates due to GameSpot's API terms of use denying users of making a competing product.

### Demo
#### (5/6/21) User Stories Completed
<img src="walkthroughUserStoriesCompleted.gif">

## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [X] User scrolls through the latest GameSpot articles and can view articles that are selected.
- [X] User has a navigation bar to transition to the latest GameSpot articles reviews, top reviews, and random videos (and can navigate back to articles).
- [X] User scrolls through the latest GameSpot reviews and can view reviews that are selected.
- [X] User scrolls through the top GameSpot reviews and can view reviews that are selected.
- [X] User scrolls through random videos and can watch videos that are selected.

**Optional Nice-to-have Stories**

- [X] User can drag down at the top of one of the scrolls to refresh to check for more articles/reviews/videos.
- [X] Infinite pagination
- [X] User can select different quality videos in the Videos Screen.
- [X] Combine the "Latest Reviews" and "Top Reviews" tabs into one and allow user to have a dropdown to sort the reviews.
- [ ] Search bar for each category.
- [ ] Favorites tab that can store any articles/reviews/videos that the user can store on their device or a backend database.
- [ ] Settings tab that the user can set certain settings such as light/dark mode, text font, themes, etc.

### 2. Screen Archetypes

* Articles Feed Screen
   * Calls a GET request to obtain and display the latest 20 articles' title, description/deck, author name, and time ago since publish time.
   * Loads the article image when user scrolls it into view.
* Articles Detail Screen
   * Loads and displays the HD image, title, description/deck, author name, publish date and time based on timezone, and body.
   * User can press a button to open a web browser and goes to the GameSpot article.
* Latest Game Reviews Feed Screen
   * Calls a GET request to obtain and display the latest 20 articles reviews' title, description/deck, author name, time ago since publish time and score.
   * Loads the article image when user scrolls it into view.
* Latest Game Reviews Detail Screen
   * Loads and displays the HD image, title, description/deck, author name, publish date and time based on timezone, body and score.
   * User can press a button to open a web browser and goes to the GameSpot review.
* Top Rated Game Reviews Feed Screen
   * Calls a GET request to obtain and display the highest scored top 20 articles reviews' title, description/deck, author name, time ago since publish time and score.
   * Loads the article image when user scrolls it into view.
* Top Rated Game Reviews Detail Screen
   * Loads and displays the HD image, title, description/deck, author name, publish date and time based on timezone, body and score.
   * User can press a button to open a web browser and goes to the GameSpot review.
* Videos Feed Screen
   * Calls a GET request to obtain and display the latest 20 videos' title, description/deck, and time ago since publish time.
   * Loads the article image when user scrolls it into view.
* Videos Detail Screen
   * Loads and displays the high quality video, title, description/deck, publish date and time based on timezone, and body.
   * User can press a button to open a web browser and goes to the GameSpot's source of the displayed video.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Article Selection
* Latest Game Reviews Selection
* Top Rated Game Reviews Selection
* Videos Selection

Optional:
* Favorites
* Settings

**Flow Navigation** (Screen to Screen)
* Initial Starting Screen, Article Selection -> Article Detail View -> Open web browser to GameSpot article
* Latest Game Reviews Selection -> Latest Game Reviews Detail View -> Open web browser to GameSpot reivew
* Top Rated Game Reviews Selection -> Top Rated Game Reviews Detail View -> Open web browser to GameSpot reivew
* Videos Selection -> Videos Detail View -> Open web browser to GameSpot article/review

## Wireframes
### Digital Wireframes & Mockups
#### [View Wireframe on Figma](https://www.figma.com/file/Ngljge013iCNdjGYGnfkyh/Game-News-App?node-id=0%3A1)
<img src="wireframeArticles.png"><br>
<img src="wireframeLatestReviews.png"><br>
<img src="wireframeTopReviews.png"><br>
<img src="wireframeVideos.png"><br>

### Interactive Prototype
#### [View Wireframe on Figma](https://www.figma.com/proto/Ngljge013iCNdjGYGnfkyh/Game-News-App?scaling=scale-down&page-id=0%3A1&node-id=1%3A2)
<img src="walkthroughInteractiveWireFrame.gif">

## Schema
### Networking
#### List of network requests by screen
   - Articles Feed Screen
      - (Read/GET) Query 20 articles where publish date is descending
         ```java
        public static final String GAME_NEWS_URL = "https://www.gamespot.com/api/articles/?api_key=" 
                                     + consumerKey + "&format=json&limit=20&sort=publish_date:desc";
        ...
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(GAME_NEWS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    articles.addAll(Articles.fromJsonArray(results));
                    articlesAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure " + statusCode);
            }
        });
         ```
   - Latest Reviews Feed Screen
      - (Read/GET) Query 20 reviews where update date is descending
   - Top Reviews Feed Screen
      - (Read/GET) Query 20 articles where score is descending and update date is descending
   - Videos Feed Screen
      - (Read/GET) Query 20 videos where publish date is descending
#### Existing API Endpoints
##### GameSpot API
- Base URL - [https://www.gamespot.com/api/](https://www.gamespot.com/api/)

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /articles/&sort=publish_date:desc | get all articles sorted by latest publish date
    `GET`    | /reviews/&sort=update_date:desc | get all reviews sorted by latest update date
    `GET`    | /reviews/&sort=score:desc,update_date:desc | get all reviews sorted by highest score and latest update date
    `GET`    | /videos/&sort=publish_date:desc | get all videos sorted by latest publish date
