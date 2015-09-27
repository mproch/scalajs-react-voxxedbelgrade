import com.lihaoyi.workbench.Plugin._
import spray.revolver.RevolverPlugin.Revolver

enablePlugins(ScalaJSPlugin)

name := "voxxedScalaJS"

scalaVersion := "2.11.7"

lazy val root = project.in(file(".")).
  aggregate(voxxedScalaJSJS, voxxedScalaJSJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val voxxedScalaJS = crossProject.in(file(".")).
  settings(
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.3.6",
      "com.lihaoyi" %%% "scalatags" % "0.5.2"
    ),
    scalaVersion := "2.11.7"
  ).
  jvmSettings(
    Revolver.settings.settings ++
      (libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
      "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0",
      "com.typesafe.akka" %% "akka-http-experimental" % "1.0"
    )) : _*
  ).
  jsSettings(
    workbenchSettings ++ Seq(
      libraryDependencies ++= Seq(
        "com.github.japgolly.scalajs-react" %%% "core" % "0.9.2",
        "com.github.japgolly.fork.scalaz" %%% "scalaz-core" % "7.1.2",
        "pl.mproch" %%% "scalajs-openlayers" % "0.1-SNAPSHOT"
      ),
      jsDependencies ++= Seq(
        "org.webjars" % "react" % "0.12.2" / "react-with-addons.js" commonJSName "React",
        "org.webjars" % "openlayers" % "3.8.2" / "ol-debug.js" commonJSName "OpenLayers"
      ),
      bootSnippet := "document.getElementById(\"content\").innerHTML = \"\"; new pl.touk.scalajs.HelloWorld().run();",
      updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)) : _*
  )


lazy val voxxedScalaJSJS = voxxedScalaJS.js
lazy val voxxedScalaJSJVM = voxxedScalaJS.jvm

