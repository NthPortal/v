package lgbt.princess.v.semver
package mapped

import lgbt.princess.v.semver.Identifiers.{Build, PreRelease}

abstract class Mapping[P, B](implicit pM: Mappable[P, PreRelease], bM: Mappable[B, Build]) {
  final type Mapped = MappedSemVer[P, B]

  final def map(sv: SemVer): Option[Mapped] = MappedSemVer.map[P, B](sv)
  final def unsafeMap(sv: SemVer): Mapped   = MappedSemVer.unsafeMap[P, B](sv)

  final def parse(version: String): Option[Mapped] = MappedSemVer.parse[P, B](version)
  final def unsafeParse(version: String): Mapped   = MappedSemVer.unsafeParse[P, B](version)
}
