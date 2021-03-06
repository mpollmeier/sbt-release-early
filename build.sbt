lazy val publishSettings = Seq(
  // This assumes that `publishSigned` is only executed in the CI
  pgpPassphrase := sys.env.get("PGP_PASSWORD").map(_.toArray),
  pgpPublicRing := file("/drone/.gnupg/pubring.asc"),
  pgpSecretRing := file("/drone/.gnupg/secring.asc"),
  // These are the requirements for the sbt-release-early plugin
  publishMavenStyle := true,
  bintrayOrganization := Some("scalacenter"),
  bintrayRepository := "sbt-maven-releases",
  bintrayPackageLabels := Seq("scala", "scalacenter", "plugin", "sbt"),
  publishArtifact in Test := false,
  licenses := Seq(
    // Scala Center license... BSD 3-clause
    "BSD" -> url("http://opensource.org/licenses/BSD-3-Clause")
  ),
  homepage := Some(url("https://github.com/scalacenter/sbt-release-early")),
  autoAPIMappings := true,
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/scalacenter/sbt-release-early"),
      "scm:git:git@github.com:scalacenter/sbt-release-early.git"
    )),
  developers := List(
    Developer("jvican",
              "Jorge Vicente Cantero",
              "jorge@vican.me",
              url("https://github.com/jvican"))
  )
)

lazy val buildSettings = Seq(
  organization := "ch.epfl.scala",
  resolvers += Resolver.jcenterRepo,
  resolvers += Resolver.bintrayRepo("scalaplatform", "tools"),
  updateOptions := updateOptions.value.withCachedResolution(true)
)

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Xlint"
)

lazy val commonSettings = Seq(
  triggeredMessage in ThisBuild := Watched.clearWhenTriggered,
  watchSources += baseDirectory.value / "resources",
  scalacOptions in (Compile, console) := compilerOptions,
  testOptions in Test += Tests.Argument("-oD")
)

lazy val noPublish = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {}
)

lazy val allSettings: Seq[Setting[_]] =
  commonSettings ++ buildSettings ++ publishSettings

lazy val `sbt-release-early` = project
  .in(file("."))
  .settings(allSettings)
  .settings(ScriptedPlugin.scriptedSettings)
  .settings(
    sbtPlugin := true,
    scalaVersion := "2.10.6",
    addSbtPlugin("ch.epfl.scala" % "sbt-bintray" % "0.5.0"),
    addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0"),
    addSbtPlugin("com.dwijnand" % "sbt-dynver" % "1.3.0"),
    scriptedLaunchOpts := Seq(
      "-Dplugin.version=" + version.value,
      "-Xmx1g",
      "-Xss16m"
    ),
    scriptedBufferLog := false,
    fork in Test := true
  )
