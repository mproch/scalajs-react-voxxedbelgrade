package pl.touk.scalajs

import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, React, ReactComponentB, _}
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.ext.Ajax
import pl.mproch.openlayers.OpenLayers.lonLatToMercator
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.annotation.JSExport
import scala.util.Failure
import scalaz.OptionT._
import scalaz.std.scalaFuture.futureInstance

@JSExport
class HelloWorld {

  private def getLocation(name: String): Future[Option[GeoLocation]] =
    optionT(
      getData[List[GeoLocation]](s"http://nominatim.openstreetmap.org/search?q=$name&format=json").map(_.headOption))
    .flatMapF(location =>
      getData[List[Comment]](s"http://localhost:8080/locationComment/${location.name}").map(location.addComments))
    .run

  class Backend($:BackendScope[Unit, (String, Option[GeoLocation])]) {
    def valueChanged(event: ReactEventI): Unit = {
      $.modState(_.copy(_1 = event.target.value))
    }

    def search(): Unit = {
      getLocation($.state._1).foreach(locationO => $.modState(_.copy(_2 = locationO)))
    }

  }

  val GeoLocationRender = ReactComponentB[Option[GeoLocation]]("GeoLocationWithComments")
    .render(locationO => <.div(
      locationO.isEmpty ?= <.span("Sorry, no results found"),
        locationO.map(location => <.div(<.b(location.name), <.ul(
          location.comments.map(comm => <.li(comm.toString))
          ),
          LayerMarker.component(location.lonLat)
        ))
      ))
    .build

  val LocationSearcher = ReactComponentB[Unit]("LocationSearcher")
    .initialState(("", Option.empty[GeoLocation]))
    .backend(new Backend(_))
    .render((_, S, B) => <.div(
      <.input(^.`type`:="text", ^.value:=S._1, ^.onChange ==> B.valueChanged),
      <.button("Search", ^.onClick --> B.search),
      GeoLocationRender(S._2)
  )).buildU


  private def getData[T: Reader](url: String): Future[T]
  = Ajax.get(url)
    .map(_.responseText)
    .map(upickle.default.read[T])
    .andThen({
        case Failure(ex) => dom.alert(s"Ajax request for $url failed with: $ex")
    })

  @JSExport
  def run(): Unit = {
    println("hello world")
    React.render(LocationSearcher(), document.getElementById("content"))
  }

}
