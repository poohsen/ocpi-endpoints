resolvers ++= Seq(
  "TNM public" at "http://nexus.thenewmotion.com/content/groups/public",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/")

addSbtPlugin("com.thenewmotion" % "sbt-build-seed" % "1.6.0" )

addSbtPlugin("com.github.fedragon" % "sbt-todolist" % "0.6")

