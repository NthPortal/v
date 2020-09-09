package lgbt.princess.v.semver

sealed trait IdentifierType[I <: Identifiers] {
  private[semver] def isValidIdentifier(identifier: String): Boolean

  private[semver] def uncheckedFrom(values: Seq[String]): I

  private[semver] def name: String
}

object IdentifierType {
  private[semver] final val baseIdentifierRegex = "[0-9A-Za-z-]+".r
  private[semver] final val leadingZerosRegex   = "0[0-9]+".r

  type PreRelease = IdentifierType[Identifiers.PreRelease]
  type Build      = IdentifierType[Identifiers.Build]

  case object PreRelease extends IdentifierType[Identifiers.PreRelease] {
    private[semver] def isValidIdentifier(identifier: String) =
      baseIdentifierRegex.matches(identifier) && !leadingZerosRegex.matches(identifier)

    private[semver] def uncheckedFrom(values: Seq[String]) = new Identifiers.PreRelease(values.toIndexedSeq)

    private[semver] def name = "pre-release"
  }

  case object Build extends IdentifierType[Identifiers.Build] {
    private[semver] def isValidIdentifier(identifier: String) =
      baseIdentifierRegex matches identifier

    private[semver] def uncheckedFrom(values: Seq[String]) = new Identifiers.Build(values.toIndexedSeq)

    private[semver] def name = "build"
  }

  implicit def preRelease: PreRelease = PreRelease
  implicit def build: Build           = Build
}
