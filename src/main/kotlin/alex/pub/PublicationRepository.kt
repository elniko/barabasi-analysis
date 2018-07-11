package alex.pub

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query


@Repository
interface PublicationRepository : CrudRepository<Publication, Int>{

    fun findBySubjectOrderByDateAsc(subject: String): List<Publication>


    @Modifying
    @Query("update Publication p set p.cited = ?1 where p.id = ?2")
    fun updateCited(cited: Int, id: Int)
}