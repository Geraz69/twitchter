package com.danielasfregola.twitter4s.http.clients

import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.danielasfregola.twitter4s.util.Configurations._

trait TwitchterClient extends OAuthClient{

  val consumerToken = ConsumerToken(key = consumerTokenKey, secret = consumerTokenSecret)
  val accessToken = AccessToken(key = accessTokenKey, secret = accessTokenSecret)

  override def withLogRequest = true
  override def withLogRequestResponse = true

}
