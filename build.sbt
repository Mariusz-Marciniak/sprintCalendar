name := "sprintCalendar"

version := "1.0"

lazy val `sprintcalendar` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws )

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.8.0"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

