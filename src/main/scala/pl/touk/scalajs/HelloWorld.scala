package pl.touk.scalajs

import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

import org.scalajs.dom.document

@JSExport
class HelloWorld {

  @JSExport
  def run(): Unit = {
    println("hello world")

    val inputEl = input(`type` := "text", id := "input").render

    def append(): Unit = {
      document.getElementById("value").textContent = inputEl.value
    }

    val content = div(
      inputEl,
      button("Submit", onclick := append _),
      div("The value you entered is: "),
      b(span(id:="value"))
    )

    document.getElementById("content").appendChild(content.render)
  }

}
