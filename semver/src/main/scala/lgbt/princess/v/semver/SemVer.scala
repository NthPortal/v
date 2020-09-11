package lgbt.princess.v
package semver

import java.lang.{StringBuilder => JStringBuilder}

import lgbt.princess.v.semver.Identifiers._

import scala.collection.mutable.{StringBuilder => SStringBuilder}

final case class SemVer(core: Core, preRelease: Option[PreRelease], build: Option[Build]) {
  import SemVer._

  override def toString: String = {
    val sb = new JStringBuilder()
    sb.append(core)
    appendPrefixed(sb, '-', preRelease)
    appendPrefixed(sb, '+', build)
    sb.toString
  }
}

object SemVer {
  private def appendPrefixed(sb: JStringBuilder, prefix: Char, identifiers: Option[Identifiers]): Unit = {
    if (identifiers.isDefined) {
      sb.append(prefix)
      identifiers.get.values.addString(new SStringBuilder(sb), ".")
    }
  }

  def apply(core: Core): SemVer = apply(core, None, None)

  def apply(core: Core, preRelease: PreRelease): SemVer = apply(core, Some(preRelease), None)

  def apply(core: Core, build: Build): SemVer = apply(core, None, Some(build))

  def apply(core: Core, preRelease: PreRelease, build: Build): SemVer =
    apply(core, Some(preRelease), Some(build))

  private[this] def splitVersion(version: String): (String, Option[String], Option[String]) = {
    val plusSplit = version.split("""\+""", 2)
    val dashSplit = plusSplit(0).split("-", 2)

    val core       = dashSplit(0)
    val preRelease = if (dashSplit.length == 2) Some(dashSplit(1)) else None
    val build      = if (plusSplit.length == 2) Some(plusSplit(1)) else None

    (core, preRelease, build)
  }

  def parse(version: String): Option[SemVer] = {
    def parseIdentifiers[I <: Identifiers](factory: Factory[I])(identifiers: Option[String]): Option[Option[I]] =
      identifiers match {
        case None      => Some(None)
        case Some(str) => factory.parse(str).map(Some(_))
      }

    val (coreStr, preReleaseStr, buildStr) = splitVersion(version)

    for {
      core       <- Core parse coreStr
      preRelease <- parseIdentifiers(PreRelease)(preReleaseStr)
      build      <- parseIdentifiers(Build)(buildStr)
    } yield apply(core, preRelease, build)
  }

  @throws[VersionFormatException]
  def unsafeParse(version: String): SemVer = {
    val (core, preRelease, build) = splitVersion(version)

    try {
      apply(
        Core unsafeParse core,
        preRelease map PreRelease.unsafeParse,
        build map Build.unsafeParse
      )
    } catch {
      case e: IllegalArgumentException => throw new VersionFormatException(version, "SemVer version", e)
    }
  }

  def unapply(version: String): Option[(Core, Option[PreRelease], Option[Build])] =
    parse(version) flatMap unapply
}
