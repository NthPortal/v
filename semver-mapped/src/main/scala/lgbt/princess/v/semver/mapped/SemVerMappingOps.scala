package lgbt.princess.v.semver
package mapped

final class SemVerMappingOps(private val self: SemVer) extends AnyVal {
  @inline def mapTo[P, B](mapping: Mapping[P, B]): Option[MappedSemVer[P, B]] = mapping.map(self)

  @inline def unsafeMapTo[P, B](mapping: Mapping[P, B]): MappedSemVer[P, B] = mapping.unsafeMap(self)
}
