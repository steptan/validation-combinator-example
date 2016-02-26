package m

object ValueValidators {
  import Validation._
  val validEmail = validateValue[String]("should have an @ sign") { str => str contains "@" }

  val noSpaces = validateValue[String]("can't have spaces") { str => !str.contains(" ") }

  val notBlank = validateValue[String]("can't be blank") { v => v.trim.nonEmpty }
}

case class NewUser(name: String, email: String, password: String)

object Validators {
  import Validation._
  import ValueValidators._

  // define typeclass Validator[NewUser, ObjectFailure], which can be pulled
  // from implicit scope to validate a NewUser
  implicit val validateUser = validateObject[NewUser]("User",
    field("Name", _.name, notBlank),
    field("Email", _.email, validEmail andAlso noSpaces))
}

object Main extends App {
  import Validators._

  val invalidEmail = NewUser(name = "Tim", email = "supergmial.com", password = "los password")
  val validUser = NewUser(name = "Tim", email = "super@gmial.com", password = "los password")
  val missingName = NewUser(name = " ", email = "super@gmial.com", password = "los password")

  println("Invalid Email result:")
  println(Validation.validate(invalidEmail))
  println("Missing name result:")
  println(Validation.validate(missingName))
  println("Valid user result:")
  println(Validation.validate(validUser))
}
