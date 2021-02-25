package lgbt.princess.v.semver
package mapped
package scala_builds

object ScalaVersion extends Mapping[Nightly, Nothing] {
  def apply(core: Core): ScalaVersion = MappedSemVer(core)
  def apply(core: Core, nightly: Nightly): ScalaVersion = MappedSemVer(core, Some(nightly), None)

  def unapply(sv: SemVer): Option[(Core, Option[Nightly])] =
    map(sv).map(mv => (mv.core, mv.preRelease))
}
