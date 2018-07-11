package alex.pub

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import khttp.get

@Service
class GoogleScholar {

    val url = "https://scholar.google.fr/scholar"
    val agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36"


   private fun getContent(query: String, cookie:String = ""): String {

       val q = query.replace(" ", "+")
       val u = "$url?q=$q"

       val resp = get(u, headers = mapOf(
               "user-agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36",
               "cookie" to cookie
       ))
       println(u)
       return resp.text

   }

    private  fun parseDocument(content: String) : Int {
        val doc = Jsoup.parse(content)
        val body = doc.body()
        val cited = body
                .getElementById("gs_res_ccl_mid")
                .child(0)
                .getElementsByClass("gs_ri")[0]
                .getElementsByClass("gs_fl")[0]
                .child(2)
                .text()

        if(cited =="")
            return 0
        val c = cited
                .trim()
                .replace("&nbsp", " ")
                .replace("-", " ")
                .split(" ")[1]

        return try {
            c.toInt()
        } catch(e: Exception){
            0
        }

    }



    fun getCitedCount(query:String, cookie: String) :Int{
        val content = getContent(query, cookie)
        try{
            return  parseDocument(content)
        } catch (e: Exception){
            print(query)
            throw e
        }

    }


}
