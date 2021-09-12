package lgbt.princess.v.semver.mapped

import lgbt.princess.v.semver.Identifiers

// Can't be `+A` because, while that does return a valid type, the compiler
//   may select an implicit instance for a subtype of `A` that only maps a
//   subset of valid values for `A`. It's especially problematic because
//   `Nothing` is a subtype of `A` and maps an empty subset of valid values
//   for `A`, and we need an instance for `Nothing` for `Mapping`s without one
//   of the two types of `Identifiers`. Additionally, it causes ambiguous implicits.
// Can be `-I` to support instances that can map from arbitrary `Identifiers`
//   rather than only from one of the two subtypes of `Identifiers`.
trait Mappable[A, -I <: Identifiers] {
  def map(identifiers: I): Option[A]

  /**
   * @param identifiers the identifiers to be mapped
   * @return the result type equivalent to the given identifiers
   * @throws scala.UnsupportedOperationException if the identifiers cannot be mapped
   */
  @throws[UnsupportedOperationException]
  def unsafeMap(identifiers: I): A
}

object Mappable {

  /**
   * A [[Mappable]] that forwards the implementation of
   * [[Mappable.unsafeMap() `unsafeMap`]] to [[Mappable.map() `map`]].
   */
  trait ForwardingUnsafeMap[A, -I <: Identifiers] extends Mappable[A, I] {

    /** A description of the result type of the mapping operation. */
    protected[this] def resultType: String

    final def unsafeMap(identifiers: I): A =
      map(identifiers)
        .getOrElse(throw new UnsupportedOperationException(s"cannot map '$identifiers' to $resultType"))
  }

  implicit object NothingIsMappable extends Mappable[Nothing, Identifiers] {
    def map(identifiers: Identifiers): Option[Nothing] = None
    def unsafeMap(identifiers: Identifiers): Nothing =
      throw new UnsupportedOperationException("cannot map identifiers to nothing")
  }
}
