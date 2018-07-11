package alex.pub

import org.springframework.stereotype.Service
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.net.URL
import java.nio.charset.Charset
import javax.xml.parsers.DocumentBuilderFactory

@Service
class BarabasiPageAnalyzer {

    val url = "http://barabasi.com/publications"

//    @PostConstruct
//    fun print() {
//
//        getPublications()
//    }


    private fun getDocument(content: String): Document {

        val startIndex = content.indexOf("<body")
        val endIndex = content.indexOf("</body>", startIndex) + 7

        val table =  "<tag>" +
                "${content
                        .substring(startIndex, endIndex )}</tag>"
        val db = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        return db.parse(table.byteInputStream())
    }

    fun getNodes(url:String): NodeList? {
        val content = URL(url).readText(Charset.forName("UTF-8"))
        val regex:Regex = Regex("[\\x01\\x0c\\x12]")

        val doc = getDocument(
                content
                        .replace("&","&amp;")
                        .replace(regex," ")
        )
        return doc.getElementsByTagName("div").item(0).childNodes
    }


    fun getPublications() :List<Publication>{
        val resultList = mutableListOf<Publication>()
        for(subject in Subjects.values()){
            val nodes = getNodes(subject.link)
            if (nodes != null) {
                resultList.addAll(nodesToPublications(nodes, subject.subject))
            }
        }
        return resultList
    }

    fun nodesToPublications(nodes:NodeList, subject: String): List<Publication> {
        val result = mutableListOf<Publication>()
        for(i in 0 until nodes.length){
            val node = nodes.item(i).childNodes.item(1)
            val data = node.childNodes
            val authors = getAuthors(data.item(0).textContent)
            val title = data.item(1).textContent
            val abstract = data.item(4)?.textContent ?: ""
            val year = getYear(data.item(2).textContent)

            val publication = Publication(subject, title, year, abstract)
            publication.authors.addAll(authors)
            result.add(publication)
        }
        return result
    }

    fun getYear(text: String) :String {
        val first  = text.lastIndexOf("(")
        val last  = text.lastIndexOf (  ")")
        return try {
            text.substring(first+1, last)
        } catch(e : Exception){
            text.substring(first+1)
        }

    }

    fun getAuthors(text: String) : List<String>{
        return text.split(",")
                .map{ it.split(" ").last()}
    }

}

