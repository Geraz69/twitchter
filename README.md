# twitchter
By [Gerardo Garcia Mendez](https://twitter.com/Geraz69).

This repo is a series of demo apps written in scala, using Akka actor system and showcasing the Reactive streams framework. The demos use information collected from the Twitter streaming and RESTful API's using components from the Twitter4s project.

## How to use this repo

Install the necesary dependencies:

* [Scala](https://www.scala-lang.org/download/)
* [SBT](http://www.scala-sbt.org/)
* [Redis](https://redis.io/download)

If you are using Mac OS X and brew is your package manager you can do:

```bash
$ brew install scala
$ brew install sbt
$ brew install redis
````

\*note: Assuming java 1.8 is already installed :wink:

Clone this repo and compile dependencies:
```bash
$ cd twitchter
$ sbt compile
````

Create your set of keys to access the Twitter API. Go to https://apps.twitter.com/ login and click on `Create New App` to get the set of four strings.

Modify the file under `src/main/resources/application.conf` to use your own set of keys.

Run each one of the apps on this demo:
```bash
$ sbt "runMain geraz.twitchter.stages.Stage1"
````
\*note: switch the class name to match stages 1 through 5.

You need to run redis on a different command line terminal for stages 3 and 5.

```bash
$ redis-server &
````

Also, you can send Tweet Ids from a file containing them by running the sender program, located on the same package, you just need to specify the location of the file with the Ids:

```bash
$ sbt "runMain geraz.twitchter.stages.Sender /path/to/ids/file"
````

Both the stage programs and the sender will be reading from/writing to the default interface on localhost, port 8090, which should be accessible and not blocked by Firewall or OS settings.



