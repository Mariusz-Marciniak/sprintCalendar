name := "sprintCalendar"

version := "1.0"

lazy val `sprintcalendar` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws,
  "com.github.nscala-time" %% "nscala-time" % "1.8.0",
  "commons-codec" % "commons-codec" % "1.10"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"
