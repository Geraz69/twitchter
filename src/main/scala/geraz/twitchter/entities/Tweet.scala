package geraz.twitchter.entities

case class Tweet(id: Long, user: String, hashTags: Seq[String], mentions: Seq[String], urls: Seq[String])