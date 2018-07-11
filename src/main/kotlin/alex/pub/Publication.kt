package alex.pub

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import javax.persistence.*

@Entity
data class  Publication(
        val subject: String ="",
        val title: String ="",
        val year: String ="01 January 1950",
        @Lob
        val abstract: String? = null,
        val cited:Int? = null

)
{
    @Id @GeneratedValue
    var id: Int = 0



    var date :LocalDate = LocalDate.now()


   init {
       try {
           when (year.trim().split(" ").size) {
               1 -> date = LocalDate.parse("01 January " + year, DateTimeFormatter.ofPattern("dd MMMM yyyy"))
               2 -> date = LocalDate.parse("01 " + year, DateTimeFormatter.ofPattern("dd MMMM yyyy"))
               3 -> date = LocalDate.parse(year, DateTimeFormatter.ofPattern("dd MMMM yyyy"))


           }
       } catch (e: DateTimeParseException) {
           date = LocalDate.parse("01 January 1970", DateTimeFormatter.ofPattern("dd MMMM yyyy"))
       }
   }


    @ElementCollection
    val authors: MutableList<String> = mutableListOf()

}