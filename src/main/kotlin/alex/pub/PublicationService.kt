package alex.pub

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class PublicationService  @Autowired constructor(val r:PublicationRepository){

    @Transactional
    fun update(cited:Int, id:Int){
        r.updateCited(cited, id)
    }

}