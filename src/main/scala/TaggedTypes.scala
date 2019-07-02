package tagged_types

import io.estatico.newtype.macros.newtype
import shapeless.tag



object TaggedTypes extends App {


  ////////////////// TAGGED TYPES
  {
    import shapeless.tag.@@

    sealed trait NameTag
    sealed trait SurnameTag
    type Name = String @@ NameTag
    type Surname = String @@ SurnameTag

    def fullName(name: Name, surname: Surname) = s"$name $surname"

    val name = tag[NameTag]("Fran")
    val surname = tag[SurnameTag]("Alv")
    println(fullName(name, surname))
  }



  ///////////////// VALUE TYPES
  case class Name(name: String) extends AnyVal
  case class Surname(surname: String) extends AnyVal

  {
    def fullName(name: Name, surname: Surname) = s"${name.name} ${surname.surname}"

    val name = Name("Fran")
    val surname = Surname("Alv")
    println(fullName(name, surname))
  }

  /////////////// NEWTYPE
  {
    def fullName(name: Types.Name, surname: Types.Surname) = s"${name.name} ${surname.surname}"
    val name = Types.Name("Fran")
    val surname = Types.Surname("Alv")
    println(fullName(name, surname))
  }
}

object Types{
  @newtype case class Name(name: String)
  @newtype case class Surname(surname: String)
}





