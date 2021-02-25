package lgbt.princess.v.semver
package mapped
package mvn_simple

object MavenVersion extends Mapping[Snapshot, Nothing] {
  private final val someSnapshot = Some(Snapshot)

  def release(core: Core): MavenVersion = MappedSemVer(core)
  def snapshot(core: Core): MavenVersion = MappedSemVer(core, someSnapshot, None)

  def unapply(sv: SemVer): Option[(Core, Option[Snapshot])] =
    map(sv).map(mv => (mv.core, mv.preRelease))
}
