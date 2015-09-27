package pl.touk.scalajs

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers.`Access-Control-Allow-Origin`
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import upickle.default._

object LocationCommentService extends App {
  implicit val system = ActorSystem("jday")
  implicit val materializer = ActorMaterializer()
  implicit val context = system.dispatcher
  val route = pathPrefix("locationComment") {
    (get & path(Segment)) { location =>
      complete(HttpResponse(200).withHeaders(`Access-Control-Allow-Origin`.*)
        .withEntity(write(comments(location))))
    }
  }
  Http().bindAndHandle(route, "localhost", 8080)


  private def comments(location: String) = List("brilliant", "fantastic").map(c =>
    Comment("anonymous?", s"$location is $c!!"))

}