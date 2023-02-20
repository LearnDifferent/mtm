<h1 align="center">
  <a href="./src/main/resources/static/favicon.ico">
    <img src="https://user-images.githubusercontent.com/44922555/164141600-8cefc798-207e-41cd-a5c7-3df6df4511ad.jpg" 
         alt="MTM" 
         width="25%">
  </a>
</h1>

<div align="center">
  <h4>
    <a href="https://github.com/LearnDifferent/mtm/commits/master">
      <img src="https://img.shields.io/github/commit-activity/y/LearnDifferent/mtm.svg?style=flat&colorB=e09e87"/>
    </a>
	<a href="https://github.com/LearnDifferent/mtm/commits/master">
      <img src="https://img.shields.io/github/last-commit/LearnDifferent/mtm.svg?style=flat&colorB=fedcba"/>
    </a>
    <a href="https://github.com/LearnDifferent/mtm/blob/master/LICENSE">
      <img src="https://img.shields.io/github/license/LearnDifferent/mtm.svg?style=flat&colorB=ffecda"/>
    </a>
  </h4>
</div>

<h3 align="center">
	<a name="tech">
		<img src="https://img.shields.io/badge/-Java-000?&logo=Java&logoColor=007396&style=flat-square"/>
		<img src="https://img.shields.io/badge/-Spring%20Boot-000?&logo=springboot&style=flat-square)"/>
		<img src="https://img.shields.io/badge/-Redis-000?&logo=Redis&style=flat-square)"/>
		<img src="https://img.shields.io/badge/-Mysql%20&%20MyBatis-000?&logo=mysql&style=flat-square)"/>
		<img src="https://img.shields.io/badge/-Elasticsearch-000?&logo=Elasticsearch&logoColor=007396&style=flat-square)"/>
		<img src="https://img.shields.io/badge/-JUnit%20&%20Mockito-000?&logo=JUnit5&style=flat-square)"/>
		<img src="https://img.shields.io/badge/-Maven-000?&logo=ApacheMaven&logoColor=e9546b&style=flat-square)"/>
		<img src="https://img.shields.io/badge/-Docker-000?&logo=Docker&style=flat-square)"/>
	</a>
</h3>

# MTM

MTM is a social bookmarking site built with Spring Boot, MySQL, MyBatis, Redis, Elasticsearch, Vue and Docker

👉 [Online Demo](http://mtm-demo.top) 👈

## Table of Contents

- [Live Demo](#live-demo)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [API Documentation](#api-documentation)
- [Deploy with docker-compose](#deploy-with-docker-compose)
- [Run Locally](#run-locally)
	- [Pre-Requisites](#pre-requisites)
	- [Basic Configuration](#basic-configuration)
		- [Database](#database)
		- [Elasticsearch](#elasticsearch)
	- [Optional Configuration](#optional-configuration)
		- [Email Service](#email-service)
		- [Cache Timeout](#cache-timeout)
	- [Run the Application](#run-the-application)
- [License](#license)

## Live Demo

⭐️ **A working live demo of MTM is available here: [http://mtm-demo.top](http://mtm-demo.top)** ⭐️

## Technology Stack

[Backend](https://github.com/LearnDifferent/mtm)

- [Java](https://www.java.com/)
- [Spring Boot](https://github.com/spring-projects/spring-boot)
- [MySQL](https://www.mysql.com)
- [MyBatis](https://github.com/mybatis/mybatis-3)
- [Redis](https://redis.io/)
- [Elasticsearch](https://github.com/elastic/elasticsearch)
- [Maven](https://github.com/apache/maven)
- [JUnit](https://junit.org/)
- [Mockito](https://site.mockito.org/)
- [Sa-Token](https://github.com/dromara/Sa-Token)
- [Docker](https://www.docker.com/)

[Frontend (mtm-ui)](https://github.com/LearnDifferent/mtm-ui)

- [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript)
- [Vue.js](https://github.com/vuejs/vue)
- [Vuetify](https://github.com/vuetifyjs/vuetify)
- [Moment](https://github.com/moment/moment)

## Features

<details>

<summary>🔖 Bookmark</summary>

- Bookmark and share web pages
- Extract basic information of the bookmarked websites
- Export bookmarks to HTML and import bookmarks from HTML file
- Add websites shared by others to your bookmarks
- Make bookmarks private in order to hide bookmarks
- Count the number of views and comments of bookmarks
- Filter bookmarked sites

</details>

<details>

<summary>🔍 Search</summary>

- Search bookmarked sites (Support English, Chinese and Japanese)
- Search all users and view their bookmarks
- Search all tags using range query
- Discover trending searches
- Ingest data from MySQL into Elasticsearch
- Check the status of data for search

</details>

<details>

<summary>🏠 Home Timeline</summary>

- Displays a stream of paginated bookmarks
- Choose between viewing the latest bookmarks, bookmarks shared by particular user and all bookmarks except those that are shared by specific user in timeline
- Discover most-saved web pages within Home

</details>

<details>

<summary>💬 Comment</summary>

- Leave comments on bookmarks shared by users
- Reply to comments
- Edit comments and replies
- View edit history of comments

</details>

<details>

<summary>🏷️ Folksonomy (Social Tagging)</summary>

- Apply public tags to bookmarks
- View bookmarks associated with a chosen tag
- View tags of a bookmarked website
- Discover popular tags

</details>

<details>

<summary>🔔 Notification</summary>

- Push a notification to a specific user when the user receives new replies or new comments
- User will receive a notification if the user's account has been upgraded or downgraded
- Push a notification to all users when a new user is created, or a new message is sent by users with admin privilege
- System notifications that contains user activity

</details>

<details>

<summary>👨‍🔧 Administrative privileges</summary>

- Upgrade or downgrade accounts
- Push notifications to all users
- List all visited bookmarks and hide the bookmarks
- View system logs
- Delete all data in Elasticsearch

</details>

<details>

<summary>👤 User</summary>

- Token based authentication
- User profile
- Reset password
- Remove an account and all data associated with the account

</details>

<details>

<summary>🍙 Others</summary>

- Support caching
- Scheduled task
- Email service

</details>

## API Documentation

See the API Docs:

- [api-document.md](./api-document.md)

<details>

<summary>More</summary>

> This API Documentation is generated by [smart-doc](https://github.com/smart-doc-group/smart-doc).
>
> You can modify [api-document-config.json](./api-document-config.json) to customize the configuration and use `mvn -Dfile.encoding=UTF-8 smart-doc:markdown` to perform document generation.

</details>

## Deploy with docker-compose

Clone the repository into a local directory:

```bash
# Clone the repository
git clone git@github.com:LearnDifferent/mtm.git
```

Go into the repository:

```bash
cd mtm
```

Deploy with docker-compose:

```bash
docker-compose up -d
```

Deploy the Front End Application:

> Go to [mtm-ui](https://github.com/LearnDifferent/mtm-ui) and deploy the front end application.

Navigate to MTM:

- Visit [localhost:80](http://localhost:80) in your browser.
- The [backend server](https://github.com/LearnDifferent/mtm) is running on [localhost:8080](http://localhost:8080)

<br/>

<details>

<summary>Related files</summary>

- [docker-compose.yml](./docker-compose.yml)
- [Dockerfile](./Dockerfile)
- [.dockerignore](./.dockerignore)
- [mysqlconfig.env](./mysqlconfig.env)
- [esplugins](./esplugins)

</details>

<details>

<summary>More</summary>

> Note that the Maven Docker Image in [Dockerfile](./Dockerfile) is [Maven Docker Image with Aliyun Mirror](https://github.com/AliyunContainerService/maven-image), which will speed up the Maven Build.
>
> You can replace it with the official image by using `FROM maven:3.8.4-jdk-11-slim AS mtm-maven`.

</details>

## Run Locally

### Pre-Requisites

Ensure pre-requisites are installed:

- Java 8+
- Maven 3+
- MySQL 8.0.17
- Redis 5.0.5
- Elasticsearch 7.8.0

Clone the repository:

```bash
# Clone this repository into a local directory
git clone git@github.com:LearnDifferent/mtm.git
```

### Basic Configuration

#### Database

1. Import [init.sql](./init.sql) into MySQL
1. Change the `spring.datasource.password` in [application-dev.yml](./src/main/resources/application-dev.yml) to your MySQL root user password. For example:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mtm?characterEncoding=UTF-8&useTimezone=true&serverTimezone=Asia/Shanghai
    username: root
    password: MyPassword # This means that your MySQL root user password is: MyPassword
```

#### Elasticsearch

Download and Install Elasticsearch Plugins:

1. You can download [IK Analysis](https://github.com/medcl/elasticsearch-analysis-ik) and [Kuromoji](https://github.com/elastic/elasticsearch-analysis-kuromoji) from [here](./esplugins)
2. Install the Plugins

<details>

<summary>Installation Guides</summary>

- [Installing and Using Elasticsearch Plugins](https://www.linode.com/docs/guides/a-guide-to-elasticsearch-plugins/)
- [（Mac）homebrewでElasticsearch 7.8.0とanalysis-kuromojiプラグインのインストール](https://pointsandlines.jp/env-tool/mac/elasticsearch-install)
- [Japanese (kuromoji) Analysis Plugin](https://www.elastic.co/guide/en/elasticsearch/plugins/current/analysis-kuromoji.html)

</details>

### Optional Configuration

#### Email Service

This application has a mail sending function that can send emails containing invitation codes to register admin accounts. However, it's easy to reach the sending limits if the application sends too many emails, so the email service is not configured.

You can configure your own SMTP server if you want to send real emails. You only need to ensure your SMTP server relay feature is turned on and configure [application.yml](./src/main/resources/application.yml) as follows:

```yaml
spring:
  mail:
    username: # Your Email Address. For Instance: abc@email.com
    password: # Your Email Password.
    host: # Your Email Host. For Instance: smtp.xxx.com
```

#### Cache Timeout

The default timeout value of Redis cache is 1 minute. You can set your desired TTL value by modifying `RedisCacheConfiguration` bean in [RedisConfig.java](./src/main/java/com/github/learndifferent/mtm/config/RedisConfig.java).

You can also specify TTL to cached keys. For example, you can configure TTL values of 10 and 20 seconds for *comment:count* and *empty:user:all* respectively by configuring [application.yml](./src/main/resources/application.yml) using the following:

```yaml
custom-redis:
  cache-configs:
    "[comment:count]": 10 # TTL is 10 seconds
    "[empty:user:all]": 20 # TTL is 20 seconds
```

### Run the Application

> Please don't forget to start [mtm-ui](https://github.com/LearnDifferent/mtm-ui), MySQL, Redis and Elasticsearch before running the application

Run the backend service from Maven directly using the Spring Boot Maven plugin:

```bash
./mvnw spring-boot:run -P dev
```

The [backend service](https://github.com/LearnDifferent/mtm) is now running on [localhost:8080](http://localhost:8080) and the [frontend client](https://github.com/LearnDifferent/mtm-ui) is running on [localhost:80](http://localhost:80)

## License

Released under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).
