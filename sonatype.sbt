Global / pomExtra := {
  <scm>
    <connection>scm:git:git@github.com:NewMotion/ocpi-endpoints.git</connection>
    <developerConnection>scm:git:git@github.com:NewMotion/ocpi-endpoints.git</developerConnection>
    <url>git@github.com:NewMotion/ocpi-endpoints.git</url>
  </scm>
  <developers>
    <developer>
      <id>kiequoo</id>
      <name>Dan Brooke</name>
      <url>https://github.com/kiequoo</url>
    </developer>
    <developer>
      <id>poohsen</id>
      <name>Christoph Zwirello</name>
      <url>https://github.com/poohsen</url>
    </developer>
  </developers>
}

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
