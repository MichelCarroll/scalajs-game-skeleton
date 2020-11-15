import org.scalajs.sbtplugin.ScalaJSPlugin

enablePlugins(ScalaJSPlugin)

name := "game"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1"
)
    