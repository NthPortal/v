package lgbt.princess.v.semver
package mapped

import lgbt.princess.v.semver.Identifiers._

final case class MappedSemVer[P, B](core: Core, preRelease: Option[P], build: Option[B]) {
  import MappedSemVer._

  /** @return whether or not this version is a pre-release */
  @inline def isPreRelease: Boolean = preRelease.isDefined

  def unmap(implicit uP: Unmappable[P, PreRelease], uB: Unmappable[B, Build]): SemVer =
    SemVer(core, preRelease.map(uP.unmap), build.map(uB.unmap))

  override def toString: String =
    s"$core${prefixedOption('-', preRelease)}${prefixedOption('+', build)}"
}

object MappedSemVer {
  private[this] final val someNone = Some(None)

  implicit def ordering[P, B](implicit uP: Unmappable[P, PreRelease], uB: Unmappable[B, Build]): Ordering[MappedSemVer[P, B]] =
    (x, y) => x.unmap compare y.unmap

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

  def map[P, B](sv: SemVer)(implicit mP: Mappable[P, PreRelease], mB: Mappable[B, Build]): Option[MappedSemVer[P, B]] =
    for {
      preRelease <- traverse(sv.preRelease.map(mP.map))
      build      <- traverse(sv.build.map(mB.map))
    } yield apply(sv.core, preRelease, build)

  def unsafeMap[P, B](sv: SemVer)(implicit mP: Mappable[P, PreRelease], mB: Mappable[B, Build]): MappedSemVer[P, B] =
    apply(sv.core, sv.preRelease.map(mP.unsafeMap), sv.build.map(mB.unsafeMap))

  def parse[P, B](
      version: String
  )(implicit mP: Mappable[P, PreRelease], mB: Mappable[B, Build]): Option[MappedSemVer[P, B]] =
    SemVer.parse(version) flatMap map[P, B]

  def unsafeParse[P, B](
      version: String
  )(implicit mP: Mappable[P, PreRelease], mB: Mappable[B, Build]): MappedSemVer[P, B] =
    unsafeMap(SemVer.unsafeParse(version))
}
