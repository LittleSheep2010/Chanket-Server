# Chanket

A Opensource chatroom server based by  Websocket and SpringBoot

## Getting Start!

First, let's prepare the environment first

### Environmental requirements：

* JRE 1.8+
* SQLite 3+
* 1MB+ Network

After preparing the environment, let's create the database!

1. Create a file named `database.sqlite` in the same level as the downloaded Jar file
2. Open this database file using any SQLite tool or terminal
3. Execute the creator SQL script

The Creator SQL Script
```sql
---------------------------------------------
--- Account Table
---------------------------------------------

CREATE TABLE "Accounts" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "username" TEXT NOT NULL,
  "password" TEXT NOT NULL,
  "permission" TEXT NOT NULL DEFAULT "default",
  "prefix" TEXT NOT NULL DEFAULT "Newcomer",
  "uuid" TEXT NOT NULL,
  "state" INTEGER NOT NULL DEFAULT 0,
  "crtime" LONG NOT NULL
);
```

Then use `java -jar <file-name>` to start it!

## How To Use?

**Afterwards, we will create a `client.js` file in this repository for the client demonstration(Completed)**

**Please goto release page download!**

If you want connect server. You need authorization first.

### Authorization

Use `GET` method to apply for a logged in uuid

**Example**

```javascript
axios.get("http://localhost:8080/account/auth?username=example&password=example&auto=true")
```

* `username` Username of the logged-in user

* `password` password

* `auto` True to automatically create non-existent accounts. False to not automatically create non-existent accounts

----

After authorization, let's create a websocket connection!

### Websocket

Use `new Websocket("<url>")` to create a new websocket connection

**Example**

```javascript
const client = new websocket("ws://localhost:8080/chanket/" + uuid)
```

**If you use your uuid to connect to the server without logging in, the websocket connection will be automatically disconnected**

----

After successfully creating the link, let's send the message!

```javascript
client.send('message ${GLOBAL} "Build completed! I am happy! It\'s work!"')
```

* `arguments[0]` command, here `message` send message command
* `argumemts[1]` receive object, `${GLOBAL}` refers to all people online, use `|` to split **(use uuid)**
* `arguments[2]` message, use **double quotes** if there are spaces so that it is not split as other arguments



## Settings

### Language(Chanket.properties)

`chanket.source.language` Set the language environment of the current server

Available language environments.

* `en-US` English environment ---- Official translation
* `cn-ZH` Chinese environment ---- Official translation
