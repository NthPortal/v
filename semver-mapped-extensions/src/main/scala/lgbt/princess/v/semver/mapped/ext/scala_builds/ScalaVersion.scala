package lgbt.princess.v.semver
package mapped
package ext.scala_builds

object ScalaVersion extends Mapping[PreRelease, Nothing] {
  def apply(core: Core): ScalaVersion                         = MappedSemVer(core)
  def apply(core: Core, preRelease: PreRelease): ScalaVersion = MappedSemVer(core, Some(preRelease), None)

  def unapply(sv: SemVer): Option[(Core, Option[PreRelease])] =
    map(sv).map(mv => (mv.core, mv.preRelease))
}
