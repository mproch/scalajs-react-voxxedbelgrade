package pl.touk.scalajs

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.ext.Ajax
import pl.mproch.openlayers._
import pl.mproch.openlayers.OpenLayers.lonLatToMercator
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.js.annotation.JSExport
import scala.util.Failure
import scalatags.JsDom.all._
import scalaz.OptionT._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalaz.std.scalaFuture.futureInstance


@JSExport
class HelloWorld {

  private def getLocation(name: String): Future[Option[GeoLocation]] =
    optionT(
      getData[List[GeoLocation]](s"http://nominatim.openstreetmap.org/search?q=$name&format=json").map(_.headOption))
    .flatMapF(location =>
      getData[List[Comment]](s"http://localhost:8080/locationComment/${location.name}").map(location.addComments))
    .run


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

    val inputEl = input(`type` := "text", id := "input").render

    val map = OSMMap(target = null, layers = List(TileLayer(source = new OSM)), view = View(zoom = 10,
      center = (0.0, 0.0)))

    def append(): Unit = {
      document.getElementById("value").textContent = inputEl.value
      val search = inputEl.value
      getLocation(search).foreach(_.foreach(location => {
        document.getElementById("value").textContent = location.toString
        map.getView().setCenter(location.lonLat)
      }))
    }

    val content = div(
      inputEl,
      button("Submit", onclick := append _),
      div("The value you entered is: "),
      b(span(id:="value")),
      div(height:="200px", width:="200px", id:="map")

    )

    document.getElementById("content").appendChild(content.render)
    map.setTarget("map")
  }

}
