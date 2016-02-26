package m

sealed trait ValidationFailure
case class ValueFailure(reason: String) extends ValidationFailure
case class FieldFailure(field: String, reasons: List[String]) extends ValidationFailure
case class ObjectFailure(name: String, reasons: List[ValidationFailure]) extends ValidationFailure

trait Validator[T, F <: ValidationFailure] { self =>
  def validates(o: T): List[F]

  def andAlso(other: Validator[T, F]) = {
    new Validator[T, F] {
      def validates(o: T) = {
        other.validates(o) ++ self.validates(o)
      }
    }
  }
}

object Validation {
  type ValueValidator[T] = Validator[T, ValueFailure]
  type FieldValidator[T] = Validator[T, FieldFailure]
  type ObjectValidator[T] = Validator[T, ObjectFailure]

  def validateValue[T](errorMsg: String)(predicate: T => Boolean): ValueValidator[T] =
    new Validator[T, ValueFailure] {
      def validates(o: T) =
        if (!predicate(o))
          List(ValueFailure(errorMsg))
        else
          List.empty

    }

  def field[O, V](name: String, getter: O => V, validator: ValueValidator[V]): FieldValidator[O] =
    new FieldValidator[O] {
      def validates(obj: O) = {
        val fieldValue = getter(obj)
        val failures = validator.validates(fieldValue)

        if (failures.nonEmpty)
          List(FieldFailure(name, failures.map(_.reason)))
        else
          List.empty
      }
    }

  def validateObject[O](name: String, fieldValidators: FieldValidator[O]*) = new ObjectValidator[O]{
    def validates(obj: O) = {
      val failures = fieldValidators.map(_.validates(obj)).flatten
      if (failures.nonEmpty)
        List(ObjectFailure(name, failures.toList))
      else
        List.empty
    }
  }

  def validate[O, F <: ValidationFailure](v: O)(implicit validator: Validator[O, F]): List[F] = {
    validator.validates(v)
  }

}
