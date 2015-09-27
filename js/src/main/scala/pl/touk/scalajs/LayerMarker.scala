package pl.touk.scalajs

import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import org.scalajs.dom.Element
import pl.mproch.openlayers._

import scala.scalajs.js.JSConverters._

object LayerMarker {

  val initialCoordinates = OpenLayers.lonLatToMercator((20, 40))

  case class State(view: View, circle: Circle)

  class Backend($:BackendScope[OpenLayers.Coordinates, State]) {

    def updateWithNewCoordinates(newCoords: OpenLayers.Coordinates): Unit = {
      $.state.circle.setCenter(newCoords)
      $.state.view.setCenter(newCoords)
    }

    def initialize(node: Element) : Unit = {
      val color = List[Double](240, 15, 0, 0.6).toJSArray
      val style = Style(stroke = Stroke(color = color, width = 5))

      val vector = VectorLayer(source = VectorSource(features = List(new Feature($.state.circle))), style = style)
      val tiles = TileLayer(source = new OSM)

      OSMMap(target = node, controls = List(new ZoomSlider), layers = List(tiles, vector),view = $.state.view)

      updateWithNewCoordinates($.props)
    }

  }

  val component = ReactComponentB[OpenLayers.Coordinates]("LayerWithMarker")
    .initialState(State(View(zoom = 9, center = initialCoordinates), new Circle(initialCoordinates, 10000)))
    .backend(new Backend(_))
    .render((P, S, B) => <.div(^.width:="300px", ^.height:="300px"))
    .componentWillReceiveProps((scope, newProps) => scope.backend.updateWithNewCoordinates(newProps))
    .componentDidMount(scope => scope.backend.initialize(scope.getDOMNode()))
    .build

}
