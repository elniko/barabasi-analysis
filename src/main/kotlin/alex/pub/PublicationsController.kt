package alex.pub

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.query.Param
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class PublicationsController @Autowired constructor (
        val pr :PublicationRepository,
        val analizer: BarabasiPageAnalyzer,
        val gs: GoogleScholar,
        val ps: PublicationService
) {



    @GetMapping("/cited")
    @ResponseStatus(HttpStatus.OK)
    fun cited(@RequestParam cookie: String){
        pr.findAll().forEach({
            if(it.cited == null) {
                val cited = gs.getCitedCount(it.title, cookie)
                ps.update(cited, it.id)
            }
        })
    }


    @GetMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    fun updateDatabase() {
        analizer.getPublications().forEach { pub -> pr.save(pub) }
    }


    @GetMapping("/publications")
    fun getPublications() :Pair<List<Node>, List<Link>> {


        val nodes = Subjects.values()
                .mapIndexed({index, value -> Node(value.subject, value.group)})
                .toCollection(mutableListOf())

        val links = mutableListOf<Link>()

        pr.findAll().forEach({

            val index  =  nodes.containsTitle(it.title)
            val group = Subjects.getGroupBySubject(it.subject)
            if (index == -1) {
                nodes.add(Node(it.title, 0, it.abstract, it.subject, it.year, cited = it.cited))
                links.add(Link(nodes.getSubjectIndex(it.subject), nodes.lastIndex, group))
            } else
                links.add(Link(nodes.getSubjectIndex(it.subject), index, group))
        })

        return Pair(nodes,  links)
    }


    @GetMapping("/history")
    fun getHistory(): Triple<List<Node>, List<Link>, List<Pair<String, Int>>>? {

       val list =  Subjects.values()
                .map {pr.findBySubjectOrderByDateAsc(it.subject) }
                .sortedBy { it[0].date }
                .map {
                    val branchNodes = mutableListOf<Node>()
                    it.forEach {
                        branchNodes.add(Node(it.title, Subjects.getGroupBySubject(it.subject), it.abstract, it.subject, it.year, it.date, cited=it.cited))
                    }

                    branchNodes
                }.toList()

        return getLinks(list)
    }


    fun getLinks( sortedList : List<List<Node>>) : Triple<List<Node>, List<Link>, List<Pair<String, Int>>>{
        val links = mutableListOf<Link>()
        val nodes = mutableListOf<Node>()

        sortedList.forEach {
            (0 until it.size-1).mapTo(links) {
                i -> Link(i + nodes.size, i+1 + nodes.size, Subjects.getGroupBySubject(it[0].subject ?: ""))
            }
            nodes.addAll(it)
        }

        val relations = mutableListOf<Pair<Node, Node>>()
        for( i in 1 until sortedList.size) {
            val parentRow = sortedList[0]
            val currentRow = sortedList[i]

            relations.add(Pair(findParentPublication(currentRow[0], parentRow), currentRow[0]))
        }

        relations.forEach {
            val parentIndex = nodes.indexOf(it.first)
            val newIndex = nodes.indexOf(it.second)
            links.add(Link(parentIndex, newIndex, Subjects.getGroupBySubject(it.second.subject ?: "")))
        }

        val subjects = Subjects.values().map { Pair(it.subject, it.group) }
        return Triple(nodes, links, subjects)

    }

    fun findParentPublication(startNode: Node, parentRow: List<Node>): Node {
        var minNode = parentRow[0]
        for(i in 1 until parentRow.size){
            val parentDate = parentRow[i].date ?: throw RuntimeException("Date null")

            if(parentDate <= startNode.date )
                minNode = parentRow[i]
            else break
        }

        return minNode
    }




    fun List<Node>.containsTitle(title: String) :Int{
            forEachIndexed ({ i, node ->   if(node.name == title) return i })
        return -1
    }

    fun List<Node>.getSubjectIndex(subject: String) : Int {

        return (0..5).firstOrNull { this[it].name == subject } ?: -1
    }

    data class Node(val name: String, val group: Int, val abstract: String? = null, val subject:String? = null, val year:String? = null, val date: LocalDate?= null, val cited: Int? = null)

    data class Link(val source: Int, val target: Int, val group: Int)

}