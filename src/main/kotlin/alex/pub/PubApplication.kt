package alex.pub

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class PubApplication {


    @Autowired
    @Bean
    internal fun commandLineRunner(analizer: BarabasiPageAnalyzer, repo: PublicationRepository, @Value("\${app.parse}") parse: String): CommandLineRunner {
        return object : CommandLineRunner {

            override fun run(vararg args: String) {
                println("HI")
                println("parsing: $parse")
                if (parse == "yes")
                    analizer.getPublications().forEach { pub -> repo.save(pub) }
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<PubApplication>(*args)
}
