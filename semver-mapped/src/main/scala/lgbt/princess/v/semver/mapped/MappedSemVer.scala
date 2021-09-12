package lgbt.princess.v.semver
package mapped

import lgbt.princess.v.semver.Identifiers._

final case class MappedSemVer[P, B](core: Core, preRelease: Option[P], build: Option[B]) {
  import MappedSemVer._

  def unmap(implicit uP: Unmapper[P, PreRelease], uB: Unmapper[B, Build]): SemVer =
    SemVer(core, preRelease.map(uP.unmap), build.map(uB.unmap))

  override def toString: String =
    s"$core${prefixedOption('-', preRelease)}${prefixedOption('+', build)}"
}

object MappedSemVer {
  private[this] final val someNone = Some(None)

  /**
   * The default [[scala.Ordering `Ordering`]] for mapped SemVer versions. This
   * ordering is consistent with the SemVer specification, but not with object
   * equality. If you need an ordering consistent with object equality, use
   * [[ObjectEqualityOrdering.ordering]].
   */
  implicit def ordering[P, B](implicit
      uP: Unmapper[P, PreRelease],
      uB: Unmapper[B, Build]
  ): Ordering[MappedSemVer[P, B]] =
    (x, y) => x.unmap compare y.unmap

  object ObjectEqualityOrdering {

    /**
     * A secondary [[scala.Ordering `Ordering`]] for mapped SemVer versions. This
     * ordering is consistent with object equality, but not with the SemVer
     * specification. If you need an ordering consistent with the SemVer
     * specification, use [[MappedSemVer.ordering]] (which is the default).
     */
    implicit def ordering[P, B](implicit
        uP: Unmapper[P, PreRelease],
        uB: Unmapper[B, Build],
    ): Ordering[MappedSemVer[P, B]] =
      (x, y) => SemVer.ObjectEqualityOrdering.ordering.compare(x.unmap, y.unmap)
  }

  @inline private def prefixedOption(prefix: Char, opt: Option[Any]): Unit =
    opt.fold("")(value => s"$prefix$value")

  def apply[P, B](core: Core): MappedSemVer[P, B] = apply(core, None, None)

  def apply[P, B](core: Core, preRelease: P, build: B): MappedSemVer[P, B] = apply(core, Some(preRelease), Some(build))

  private[this] def traverse[A](opt: Option[Option[A]]): Option[Option[A]] =
    opt match {
      case None       => someNone
      case Some(None) => None
      case opt        => opt
    }

  def map[P, B](sv: SemVer)(implicit mP: Mapper[P, PreRelease], mB: Mapper[B, Build]): Option[MappedSemVer[P, B]] =
    for {
      preRelease <- traverse(sv.preRelease.map(mP.map))
      build      <- traverse(sv.build.map(mB.map))
    } yield apply(sv.core, preRelease, build)

  def unsafeMap[P, B](sv: SemVer)(implicit mP: Mapper[P, PreRelease], mB: Mapper[B, Build]): MappedSemVer[P, B] =
    apply(sv.core, sv.preRelease.map(mP.unsafeMap), sv.build.map(mB.unsafeMap))

  def parse[P, B](
      version: String
  )(implicit mP: Mapper[P, PreRelease], mB: Mapper[B, Build]): Option[MappedSemVer[P, B]] =
    SemVer.parse(version) flatMap map[P, B]

  def unsafeParse[P, B](version: String)(implicit mP: Mapper[P, PreRelease], mB: Mapper[B, Build]): MappedSemVer[P, B] =
    unsafeMap(SemVer.unsafeParse(version))
}
