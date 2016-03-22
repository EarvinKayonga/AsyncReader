package models

import java.util.UUID
import org.joda.time.DateTime
import models.Gender.Gender

/**
  * Created by earvinkayonga on 21/03/2016.
  */

object Gender extends Enumeration {
  type Gender = Value
  val MALE, FEMALE =  Value
  def fromString(string: String): Gender.Gender ={
    if (string.trim().toUpperCase.substring(1, string.size - 1) == "MALE"){
      Gender.MALE
    }else {
      Gender.FEMALE
    }
  }
}

case class Person (
 id:UUID,
 gender: Gender,
 firstName : String,
 lastName : String,
 email: String,
 date: DateTime
)
