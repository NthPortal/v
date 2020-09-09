package lgbt.princess.v
package semver

import scala.collection.immutable.ArraySeq
import scala.language.implicitConversions

sealed abstract class Identifiers private[semver] (val values: IndexedSeq[String]) {
  type Self <: Identifiers

  def identifierType: IdentifierType[Self]

  override def hashCode(): Int = identifierType.hashCode() * 43 + values.hashCode()

  override def toString: String = values mkString "."
}

object Identifiers {
  final class PreRelease private[semver] (_values: IndexedSeq[String]) extends Identifiers(_values) {
    type Self = PreRelease

    def identifierType: IdentifierType[PreRelease] = IdentifierType.PreRelease

    override def equals(obj: Any): Boolean =
      obj match {
        case that: PreRelease => this.values == that.values
        case _                => false
      }
  }

  final class Build private[semver] (_values: IndexedSeq[String]) extends Identifiers(_values) {
    type Self = Build

    def identifierType: IdentifierType[Build] = IdentifierType.Build

    override def equals(obj: Any): Boolean =
      obj match {
        case that: Build => this.values == that.values
        case _           => false
      }
  }

  @throws[IllegalArgumentException]
  private def invalidIdentifiers(identifiers: String, tpe: IdentifierType[_]): Nothing =
    throw new IllegalArgumentException(s"invalid series of ${tpe.name} identifiers: '$identifiers'")

  def from[I <: Identifiers](values: Seq[String])(implicit tpe: IdentifierType[I]): Option[I] =
    if (values.forall(tpe.isValidIdentifier)) Some(tpe.uncheckedFrom(values))
    else None

  @throws[IllegalArgumentException]
  def unsafeFrom[I <: Identifiers](values: Seq[String])(implicit tpe: IdentifierType[I]): I = {
    if (values.forall(tpe.isValidIdentifier)) tpe.uncheckedFrom(values)
    else invalidIdentifiers(values.toString, tpe)
  }

  @throws[IllegalArgumentException]
  @inline def apply[I <: Identifiers](values: String*)(implicit tpe: IdentifierType[I]): I =
    unsafeFrom(values)

  @inline private[this] def splitOnDots(identifiers: String): Array[String] =
    identifiers.split("""\.""", -1)

  @inline private[this] def buildIdentifiers[I <: Identifiers](arr: Array[String])(implicit tpe: IdentifierType[I]): I =
    tpe.uncheckedFrom(ArraySeq.unsafeWrapArray(arr))

  def parse[I <: Identifiers](identifiers: String)(implicit tpe: IdentifierType[I]): Option[I] = {
    val arr = splitOnDots(identifiers)
    if (arr forall tpe.isValidIdentifier) Some(buildIdentifiers(arr)) else None
  }

  @throws[IllegalArgumentException]
  def unsafeParse[I <: Identifiers](identifiers: String)(implicit tpe: IdentifierType[I]): I = {
    val arr = splitOnDots(identifiers)
    if (arr.forall(tpe.isValidIdentifier)) buildIdentifiers(arr) else invalidIdentifiers(identifiers, tpe)
  }

  object StringsAsIdentifiers {
    implicit def unsafe[I <: Identifiers: IdentifierType](s: String): I =
      Identifiers.unsafeParse(s)
  }
}
