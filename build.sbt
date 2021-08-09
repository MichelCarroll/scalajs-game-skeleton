import org.scalajs.sbtplugin.ScalaJSPlugin

enablePlugins(ScalaJSPlugin)

name := "game"

version := "1.0"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "1.1.0"
)

scalaJSUseMainModuleInitializer := true