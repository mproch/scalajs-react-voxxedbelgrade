enablePlugins(ScalaJSPlugin)

name := "voxxedScala"

scalaVersion := "2.11.7"

workbenchSettings

bootSnippet := "document.getElementById(\"content\").innerHTML = \"\"; new pl.touk.scalajs.HelloWorld().run();"

updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.0",
  "com.lihaoyi" %%% "scalatags" % "0.5.2"

)

