package pl.touk.scalajs

import derive.key

object model


case class GeoLocation(@key("display_name") name: String,
                       @key("lat") latitude: Double, @key("lon") longitude: Double, comments: List[Comment] = List()) {
  def lonLat = (longitude, latitude)

  def addComments(comments: List[Comment]) = copy(comments = comments)
}

case class Comment(author: String, content:String)


