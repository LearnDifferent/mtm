# mtm
Version |  Update Time  | Status | Author |  Description
---|---|---|---|---
v2023-02-21 19:39:16|2023-02-21 19:39:16|auto|@zhou|Created by smart-doc



## Authentication
### Login
**URL:** http://127.0.0.1/login

**Type:** POST

**Author:** zhou

**Content-Type:** application/json; charset=utf-8

**Description:** Login

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
token|string|             token for verification code|true|-
code|string|              verification code|true|-
isAdmin|boolean|           check whether the user is the administrator if {@code isAdmin} is true|false|-

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
userName|string|Username|false|-
password|string|Password|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-
└─tokenName|string|No comments found.|-
└─tokenValue|string|No comments found.|-
└─isLogin|boolean|No comments found.|-
└─loginId|object|No comments found.|-
└─loginKey|string|No comments found.|-
└─tokenTimeout|int64|No comments found.|-
└─sessionTimeout|int64|No comments found.|-
└─tokenSessionTimeout|int64|No comments found.|-
└─tokenActivityTimeout|int64|No comments found.|-
└─loginDevice|string|No comments found.|-


### Logout
**URL:** http://127.0.0.1/logout

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Logout

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


## Bookmark Controller
### Bookmark a new web page
**URL:** http://127.0.0.1/bookmark/

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Bookmark a new web page

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
url|string|    URL of the web page to bookmark|true|-
privacy|enum|PUBLIC -(public,true)<br/>PRIVATE -(private,false)<br/>|true|-
mode|enum|ADD_TO_DATABASE -(0,addToDatabase,add_to_database)<br/>ADD_TO_DATABASE_AND_ELASTICSEARCH -(1,addToDatabaseAndElasticsearch,add_to_database_and_elasticsearch)<br/>|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
hasSavedToDatabase|boolean|True if the data was successfully saved to Database|-
hasSavedToElasticsearch|boolean|True if Elasticsearch saved the data successfully.<br>False if Elasticsearch can't save the data.<br>Null if the data does not need to be saved to Elasticsearch.|-


### Add a website to the bookmarks
**URL:** http://127.0.0.1/bookmark/

**Type:** POST

**Author:** zhou

**Content-Type:** application/json; charset=utf-8

**Description:** Add a website to the bookmarks

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
title|string|Title|false|-
url|string|Url|false|-
img|string|Image|false|-
desc|string|Description|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Delete a bookmark
**URL:** http://127.0.0.1/bookmark/

**Type:** DELETE

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Delete a bookmark

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|int32|ID of the bookmark|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Make the bookmark private if it's publicand make it public if it's private.
**URL:** http://127.0.0.1/bookmark/privacy

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Make the bookmark private if it's public
and make it public if it's private.

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|int32|ID of the bookmark|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Get a bookmark
**URL:** http://127.0.0.1/bookmark/get

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get a bookmark

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|int32|ID of the bookmark|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
id|int32|ID of the bookmark|-
userName|string|name of the user who bookmarked the website|-
title|string|title of the bookmarked website|-
url|string|URL of the bookmarked website|-
img|string|Image of the bookmarked website|-
desc|string|Description of the bookmarked website|-
createTime|object|Creation time|-
└─seconds|int64|No comments found.|-
└─nanos|int32|No comments found.|-
isPublic|boolean|True if this is a public bookmark|-


### Get paginated bookmarks of the user currently logged in
**URL:** http://127.0.0.1/bookmark/get/user

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get paginated bookmarks of the user currently logged in

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
bookmarks|array|Paginated bookmarks|-
└─id|int32|ID of the bookmark|-
└─userName|string|name of the user who bookmarked the website|-
└─title|string|title of the bookmarked website|-
└─url|string|URL of the bookmarked website|-
└─img|string|Image of the bookmarked website|-
└─desc|string|Description of the bookmarked website|-
└─createTime|object|Creation time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-
└─isPublic|boolean|True if this is a public bookmark|-
totalPages|int32|Total pages|-


### Get paginated public bookmarks of a user
**URL:** http://127.0.0.1/bookmark/get/user/{username}

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get paginated public bookmarks of a user

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
username|string|username of the user whose public bookmarks is being requested|true|-

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
bookmarks|array|Paginated bookmarks|-
└─id|int32|ID of the bookmark|-
└─userName|string|name of the user who bookmarked the website|-
└─title|string|title of the bookmarked website|-
└─url|string|URL of the bookmarked website|-
└─img|string|Image of the bookmarked website|-
└─desc|string|Description of the bookmarked website|-
└─createTime|object|Creation time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-
└─isPublic|boolean|True if this is a public bookmark|-
totalPages|int32|Total pages|-


### Get visited bookmarks
**URL:** http://127.0.0.1/bookmark/visited-bookmarks

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get visited bookmarks

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
userName|string|Username|-
title|string|Title|-
url|string|Url|-
bookmarkId|int32|ID of the bookmark|-
isPublic|boolean|True if this is a public bookmark|-
views|int32|The number of views of this bookmark|-


## Get, delete and send notifications
### Get reply notifications
**URL:** http://127.0.0.1/notification/

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get reply notifications

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
lastIndex|int32|index of the last element of the reply notification list|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
creationTime|object|Creation time|-
└─seconds|int64|No comments found.|-
└─nanos|int32|No comments found.|-
receiveUsername|string|Name of the user, who is about to receive the notification.<br>If {@link #getReplyToCommentId()} is null,<br>then the user is the owner of the website data.<br>If {@link #getReplyToCommentId()} is not null,<br>then the user is the author of the comment being replied to.|-
sendUsername|string|Name of the user who sent the reply (or comment)|-
commentId|int32|ID of the comment|-
bookmarkId|int32|ID of the bookmark|-
replyToCommentId|int32|The ID of the comment being replied to<br><p>Not reply to any comment if null, which means this is a bookmark comment</p>|-
message|string|Extend parameter: reply message<br><p>Null if the bookmark, comment or reply has been deleted</p>|-


### Delete a reply notification
**URL:** http://127.0.0.1/notification/

**Type:** POST

**Author:** zhou

**Content-Type:** application/json; charset=utf-8

**Description:** Delete a reply notification

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
creationTime|object|Creation time|false|-
└─seconds|int64|No comments found.|false|-
└─nanos|int32|No comments found.|false|-
receiveUsername|string|Name of the user, who is about to receive the notification.<br>If {@link #getReplyToCommentId()} is null,<br>then the user is the owner of the website data.<br>If {@link #getReplyToCommentId()} is not null,<br>then the user is the author of the comment being replied to.|false|-
sendUsername|string|Name of the user who sent the reply (or comment)|false|-
commentId|int32|ID of the comment|false|-
bookmarkId|int32|ID of the bookmark|false|-
replyToCommentId|int32|The ID of the comment being replied to<br><p>Not reply to any comment if null, which means this is a bookmark comment</p>|false|-



### Count the number of new reply notifications for the current user
**URL:** http://127.0.0.1/notification/count

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Count the number of new reply notifications for the current user

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Get current user's role change notification
**URL:** http://127.0.0.1/notification/role-changed

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get current user's role change notification

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Delete role change notification for current user
**URL:** http://127.0.0.1/notification/role-changed

**Type:** DELETE

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Delete role change notification for current user



### Check if the user currently logged in has turned off notifications
**URL:** http://127.0.0.1/notification/mute

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Check if the user currently logged in has turned off notifications

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Turn on notifications if the current user turned off notifications andturn off notifications if the current user turned on notifications
**URL:** http://127.0.0.1/notification/mute/switch

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Turn on notifications if the current user turned off notifications and
turn off notifications if the current user turned on notifications



## Tag Controller
### Apply a tag
**URL:** http://127.0.0.1/tag/apply

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Apply a tag

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
bookmarkId|int32|ID of the bookmark that the user currently logged in wants to apply the tag to|true|-
tag|string|   the tag to apply|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Get tags by the ID of the bookmark&lt;p&gt;Get all tags if the parameter {@code bookmarkId} is missing.&lt;/p&gt;
**URL:** http://127.0.0.1/tag/

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get tags by the ID of the bookmark
<p>
Get all tags if the parameter {@code bookmarkId} is missing.
</p>

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
bookmarkId|int32|ID of the bookmark|false|-
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|array|Data|-


### Get a tag of a bookmark, or return empty string if the bookmark has no tags
**URL:** http://127.0.0.1/tag/one

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get a tag of a bookmark, or return empty string if the bookmark has no tags

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
bookmarkId|int32|ID of the bookmark|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Search bookmarks by a certain tag.&lt;p&gt;If some bookmarks is not public and the user currently logged inis not the owner, then those bookmarks will not be shown.&lt;/p&gt;
**URL:** http://127.0.0.1/tag/search

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Search bookmarks by a certain tag.
<p>
If some bookmarks is not public and the user currently logged in
is not the owner, then those bookmarks will not be shown.
</p>

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
tagName|string| name of the tag to search for|true|-
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
id|int32|ID of the bookmark|-
userName|string|name of the user who bookmarked the website|-
title|string|title of the bookmarked website|-
url|string|URL of the bookmarked website|-
img|string|Image of the bookmarked website|-
desc|string|Description of the bookmarked website|-
createTime|object|Creation time|-
└─seconds|int64|No comments found.|-
└─nanos|int32|No comments found.|-
isPublic|boolean|True if this is a public bookmark|-


### Search bookmarks by a certain tag and get total pages.&lt;p&gt;If some bookmarks is not public and the user currently logged inis not the owner, then those bookmarks will not be shown.&lt;/p&gt;
**URL:** http://127.0.0.1/tag/search/{tagName}

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Search bookmarks by a certain tag and get total pages.
<p>
If some bookmarks is not public and the user currently logged in
is not the owner, then those bookmarks will not be shown.
</p>

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
tagName|string| name of the tag to search for|true|-

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
bookmarks|array|Paginated bookmarks associated with the chosen tag|-
└─id|int32|ID of the bookmark|-
└─userName|string|name of the user who bookmarked the website|-
└─title|string|title of the bookmarked website|-
└─url|string|URL of the bookmarked website|-
└─img|string|Image of the bookmarked website|-
└─desc|string|Description of the bookmarked website|-
└─createTime|object|Creation time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-
└─isPublic|boolean|True if this is a public bookmark|-
totalPages|int32|Total pages|-


### Get popular tags.
**URL:** http://127.0.0.1/tag/popular

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get popular tags.

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|array|Data|-
└─tag|string|Tag|-
└─number|int32|Count the number of bookmarks of this tag|-


### Delete a tag
**URL:** http://127.0.0.1/tag/

**Type:** DELETE

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Delete a tag

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
bookmarkId|int32|ID of the bookmarked website data that the tag applied to|true|-
tagName|string|   name of the tag to be deleted|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


## Get, create, update and delete comments
### Get a comment
**URL:** http://127.0.0.1/comment/

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get a comment

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|int32| ID of the comment.
                  <p>Return {@link ResultCode#FAILED} if {@code commentId} is null.</p>|false|-
bookmarkId|int32|ID of the bookmark|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-
└─id|int32|ID of the comment|-
└─comment|string|Content|-
└─bookmarkId|int32|ID of the bookmark|-
└─username|string|Username|-
└─creationTime|object|Creation time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-
└─replyToCommentId|int32|ID of the comment to reply<br><p><br>Null if this is not a reply<br></p>|-
└─history|array|Edit history of the comment<br><p>If the comment has not been edited, this will be an empty list</p>|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─comment|string|Comment|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─creationTime|object|Creation time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-


### Get comment data of a bookmark
**URL:** http://127.0.0.1/comment/bookmark

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get comment data of a bookmark

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|int32|      ID of the bookmark|true|-
replyToCommentId|int32|ID of the comment to reply
                        <p>
                        Null if this is not a reply
                        </p>|false|-
load|int32|            Amount of data to load|true|-
order|enum|ASC -(asc,false)<br/>DESC -(desc,true)<br/>|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|array|Data|-
└─id|int32|ID of the comment|-
└─comment|string|Content|-
└─username|string|Username|-
└─creationTime|object|Creation time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-
└─replyToCommentId|int32|ID of the comment to reply<br><p><br>Null if this is not a reply<br></p>|-
└─repliesCount|int32|Count of the replies from this comment|-
└─history|array|Edit history of the comment<br><p>If the comment has not been edited, this will be an empty list</p>|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─comment|string|Comment|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─creationTime|object|Creation time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-


### Get the number of comments (exclude replies) of a bookmark
**URL:** http://127.0.0.1/comment/bookmark/{id}

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get the number of comments (exclude replies) of a bookmark

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|int32|ID of the bookmark|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Create a comment and send a notification to the user who is about to receive it
**URL:** http://127.0.0.1/comment/create

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Create a comment and send a notification to the user who is about to receive it

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
comment|string|         Comment|true|-
bookmarkId|int32|      ID of the bookmark|true|-
replyToCommentId|int32|ID of the comment to reply
                        <p>
                        Null if this is not a reply
                        </p>|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Edit a comment
**URL:** http://127.0.0.1/comment/

**Type:** POST

**Author:** zhou

**Content-Type:** application/json; charset=utf-8

**Description:** Edit a comment

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|int32|ID of the comment|false|-
comment|string|New comment|false|-
bookmarkId|int32|ID of the bookmark|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Delete a comment
**URL:** http://127.0.0.1/comment/

**Type:** DELETE

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Delete a comment

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|int32|ID of the comment|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


## View Counter Controller
### Increase the number of views of a bookmark
**URL:** http://127.0.0.1/view/

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Increase the number of views of a bookmark

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
bookmarkId|int32|ID of the bookmark|true|-



### Count the number of views of a bookmark
**URL:** http://127.0.0.1/view/count

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Count the number of views of a bookmark

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
bookmarkId|int32|ID of the bookmark|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Save the numbers of views from Redis to the database,or add the view data from database to Redis if the Redis has no view data
**URL:** http://127.0.0.1/view/update

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Save the numbers of views from Redis to the database,
or add the view data from database to Redis if the Redis has no view data

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|array|Data|-


## System Controller
### Get system notifications
**URL:** http://127.0.0.1/system/

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get system notifications

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Send a system notification when the user is admin&lt;p&gt;The notification will be a push notification if the {@code priority} is 0&lt;/p&gt;
**URL:** http://127.0.0.1/system/send

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Send a system notification when the user is admin
<p>
The notification will be a push notification if the {@code priority} is 0
</p>

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
message|string| the message to send|true|-
priority|int32|0 if the message has the highest priority|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Delete system notifications
**URL:** http://127.0.0.1/system/

**Type:** DELETE

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Delete system notifications

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Check whether the current user has read the latest system notification
**URL:** http://127.0.0.1/system/read

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Check whether the current user has read the latest system notification

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Get system logs from cache and database
**URL:** http://127.0.0.1/system/logs

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get system logs from cache and database

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|array|Data|-
└─title|string|Title|-
└─optType|string|CREATE,<br>READ,<br>UPDATE,<br>DELETE,<br>OTHERS|-
└─method|string|Method|-
└─msg|string|Message|-
└─status|string|Normal or Error|-
└─optTime|object|Creation time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-


### Get system logs from database directly
**URL:** http://127.0.0.1/system/logs/no-cache

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get system logs from database directly

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|array|Data|-
└─title|string|Title|-
└─optType|string|CREATE,<br>READ,<br>UPDATE,<br>DELETE,<br>OTHERS|-
└─method|string|Method|-
└─msg|string|Message|-
└─status|string|Normal or Error|-
└─optTime|object|Creation time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-


## Search Page Controller
### Search
**URL:** http://127.0.0.1/search/

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Search

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
mode|enum|WEB -(EsConstant.INDEX_WEB)<br/>TAG -(EsConstant.INDEX_TAG)<br/>USER -(EsConstant.INDEX_USER)<br/>BOOKMARK_MYSQL -(bookmark_mysql)<br/>TAG_MYSQL -(tag_mysql)<br/>USER_MYSQL -(user_mysql)<br/>|true|-
keyword|string|  keyword (accept empty string and null)|true|-
from|int32|From|false|-
size|int32|Size|false|-
rangeFrom|int32|lower range value for range query if the search mode is {@link SearchMode#TAG}. Null indicates
                 unbounded.|false|-
rangeTo|int32|  upper range value for range query if the search mode is {@link SearchMode#TAG}. Null indicates
                 unbounded.|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-
└─totalCount|int64|Total number of results|-
└─totalPage|int32|Total pages of results|-
└─paginatedResults|array|Paginated search results|-


### Check and delete data in Elasticsearch
**URL:** http://127.0.0.1/search/

**Type:** DELETE

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Check and delete data in Elasticsearch

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
mode|enum|WEB -(EsConstant.INDEX_WEB)<br/>TAG -(EsConstant.INDEX_TAG)<br/>USER -(EsConstant.INDEX_USER)<br/>BOOKMARK_MYSQL -(bookmark_mysql)<br/>TAG_MYSQL -(tag_mysql)<br/>USER_MYSQL -(user_mysql)<br/>|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Get trending searches, existent of bookmark data for search and the update information.
**URL:** http://127.0.0.1/search/load

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get trending searches, existent of bookmark data for search and the update information.

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
trendingList|array|Trending searches|-
dataStatus|boolean|True if search data is available|-
hasNewUpdate|boolean|True if the website data in database is different from the data in Elasticsearch|-


### Check the existent and changes of data
**URL:** http://127.0.0.1/search/status

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Check the existent and changes of data

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
mode|enum|WEB -(EsConstant.INDEX_WEB)<br/>TAG -(EsConstant.INDEX_TAG)<br/>USER -(EsConstant.INDEX_USER)<br/>BOOKMARK_MYSQL -(bookmark_mysql)<br/>TAG_MYSQL -(tag_mysql)<br/>USER_MYSQL -(user_mysql)<br/>|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
exists|boolean|Existence of data|-
hasChanges|boolean|Changes of data|-


### Data generation for Elasticsearch based on database
**URL:** http://127.0.0.1/search/build

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Data generation for Elasticsearch based on database

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
mode|enum|WEB -(EsConstant.INDEX_WEB)<br/>TAG -(EsConstant.INDEX_TAG)<br/>USER -(EsConstant.INDEX_USER)<br/>BOOKMARK_MYSQL -(bookmark_mysql)<br/>TAG_MYSQL -(tag_mysql)<br/>USER_MYSQL -(user_mysql)<br/>|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Delete a specific trending keyword (Guest does not have the permission)
**URL:** http://127.0.0.1/search/trending/{word}

**Type:** DELETE

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Delete a specific trending keyword (Guest does not have the permission)

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
word|string|keyword to delete|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Delete all trending keywords (Guest does not have the permission)
**URL:** http://127.0.0.1/search/trending

**Type:** DELETE

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Delete all trending keywords (Guest does not have the permission)

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


## Import and export HTML file
### Export user's bookmarks to a HTML file.&lt;p&gt;Export bookmarks belonging to the user that is currently logged inif the username is missing.&lt;/p&gt;
**URL:** http://127.0.0.1/file/

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Export user's bookmarks to a HTML file.
<p>Export bookmarks belonging to the user that is currently logged in
if the username is missing.</p>

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
username|string|username of the user whose data is being exported.|false|-



### Import a HTML file that contains bookmarks
**URL:** http://127.0.0.1/file/

**Type:** POST

**Author:** zhou

**Content-Type:** application/json; charset=utf-8

**Description:** Import a HTML file that contains bookmarks

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
htmlFile|file|a file that contains bookmarks in HTML format|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


## Send verification code and invitation code
### Get the verification code
**URL:** http://127.0.0.1/verification/code

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get the verification code

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
token|string|token for verification code|true|-



### Send invitation code
**URL:** http://127.0.0.1/verification/invitation-code

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Send invitation code

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
token|string|token for invitation code|true|-
email|string|Email|true|-



## Home Page Controller
### Get {@link HomePageVO} Data
**URL:** http://127.0.0.1/home/

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get {@link HomePageVO} Data

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
timeline|enum|LATEST -(latest)<br/>USER -(user)<br/>BLOCK -(block)<br/>|true|-
requestedUsername|string|username of the user whose data is being requested
                         <p>{@code requestedUsername} is not required</p>|false|-
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
currentUser|string|Username of the user that is currently logged in|-
bookmarksAndTotalPages|object|Paginated bookmarks and total pages|-
└─bookmarks|array|Paginated bookmarks|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|int32|ID of the bookmark|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─userName|string|name of the user who bookmarked the website|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─title|string|title of the bookmarked website|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─url|string|URL of the bookmarked website|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─img|string|Image of the bookmarked website|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─desc|string|Description of the bookmarked website|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─createTime|object|Creation time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─isPublic|boolean|True if this is a public bookmark|-
└─totalPages|int32|Total pages|-
requestedUsername|string|Username of the user whose data is being requested|-


### Filter public bookmarked sites&lt;p&gt;{@code fromTimestamp} and {@code toTimestamp} is used to query a specific range of time.&lt;/p&gt;&lt;li&gt;It will not query a range of time if both of them are null.&lt;/li&gt;&lt;li&gt;It will set the null value to the current time if one of them is null&lt;/li&gt;&lt;li&gt;It will swap the two values if necessary.&lt;/li&gt;
**URL:** http://127.0.0.1/home/filter

**Type:** POST

**Author:** zhou

**Content-Type:** application/json; charset=utf-8

**Description:** Filter public bookmarked sites
<p>
{@code fromTimestamp} and {@code toTimestamp} is used to query a specific range of time.
</p>
<li>It will not query a range of time if both of them are null.</li>
<li>It will set the null value to the current time if one of them is null</li>
<li>It will swap the two values if necessary.</li>

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
load|int32|         amount of data to load|true|-
fromTimestamp|string|filter by time|false|-
toTimestamp|string|  filter by time|false|-
orderField|enum|USER_NAME -(userName,user_name)<br/>CREATION_TIME -(creationTime,creation_time)<br/>|true|-
order|enum|ASC -(asc,false)<br/>DESC -(desc,true)<br/>|true|-

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
usernames|array|Usernames|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
id|int32|ID of the bookmark|-
userName|string|name of the user who bookmarked the website|-
title|string|title of the bookmarked website|-
url|string|URL of the bookmarked website|-
img|string|Image of the bookmarked website|-
desc|string|Description of the bookmarked website|-
createTime|object|Creation time|-
└─seconds|int64|No comments found.|-
└─nanos|int32|No comments found.|-
isPublic|boolean|True if this is a public bookmark|-


### Get popular bookmarks and total pages
**URL:** http://127.0.0.1/home/popular

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get popular bookmarks and total pages

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
bookmarks|array|Popular bookmarks|-
└─title|string|Title|-
└─url|string|Url|-
└─img|string|Image|-
└─desc|string|Description|-
└─count|int32|The number of users who bookmarked the website|-
totalPages|int32|Total pages|-


## User Controller
### Create a user and return the username
**URL:** http://127.0.0.1/user/

**Type:** POST

**Author:** zhou

**Content-Type:** application/json; charset=utf-8

**Description:** Create a user and return the username

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
code|string|              verification code|true|-
token|string|             token for verification Code|true|-
role|enum|USER -(user)<br/>GUEST -(guest)<br/>ADMIN -(admin)<br/>|true|-
invitationCode|string|    invitation code (the value will be ignored if the user role is not {@link
                          UserRole#ADMIN})|false|-
invitationToken|string|   token for invitation code
                          (the value will be ignored if the user role is not {@link UserRole#ADMIN})|false|-

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
userName|string|Username|false|-
password|string|Password|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Delete a user and all of the data associated with that user
**URL:** http://127.0.0.1/user/

**Type:** DELETE

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Delete a user and all of the data associated with that user

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
userName|string|The username of the user to be deleted|true|-
password|string|The password that the user entered|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Get personal information of current user
**URL:** http://127.0.0.1/user/

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get personal information of current user

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
user|object|User information|-
└─id|int32|ID|-
└─userName|string|Username|-
└─createTime|object|Creation Time|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─seconds|int64|No comments found.|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─nanos|int32|No comments found.|-
└─role|string|User Role|-
ip|string|IP Address|-
totalReplyNotifications|int64|Count the total number of reply notifications|-


### Get users
**URL:** http://127.0.0.1/user/all

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get users

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
from|int32|From|false|-
size|int32|Size|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
id|int32|ID|-
userName|string|Username|-
createTime|object|Creation Time|-
└─seconds|int64|No comments found.|-
└─nanos|int32|No comments found.|-
role|string|User Role|-


### Get usernames of the users and the total numbers of their public bookmarkssorted by the total number&lt;p&gt;Get all usernames in database if {@code usernames} is null or empty.&lt;/p&gt;
**URL:** http://127.0.0.1/user/usernames-and-bookmarks

**Type:** POST

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Get usernames of the users and the total numbers of their public bookmarks
sorted by the total number
<p>
Get all usernames in database if {@code usernames} is null or empty.
</p>

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
usernames|array|Usernames|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
userName|string|Username|-
bookmarkNumber|int32|Total number of the user's public bookmarks|-


### Change Password
**URL:** http://127.0.0.1/user/change-password

**Type:** POST

**Author:** zhou

**Content-Type:** application/json; charset=utf-8

**Description:** Change Password

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
userName|string|Username|false|-
oldPassword|string|Old password|false|-
newPassword|string|New password|false|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Change user role
**URL:** http://127.0.0.1/user/role

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Change user role

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|int32|     ID of the user|true|-
newRole|string|the new role of the user|true|-

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-


### Check if the user currently logged in is admin
**URL:** http://127.0.0.1/user/admin

**Type:** GET

**Author:** zhou

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** Check if the user currently logged in is admin

**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|int32|Result Code|-
msg|string|Message|-
data|object|Data|-



