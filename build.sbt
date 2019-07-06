import org.scalajs.sbtplugin.ScalaJSPlugin

enablePlugins(ScalaJSPlugin)

name := "game"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "org.scalaz" %% "scalaz-core" % "7.2.15",
  "com.github.julien-truffaut" %%  "monocle-core"  % "1.4.0",
  "com.github.julien-truffaut" %%  "monocle-macro" % "1.4.0"
)
    