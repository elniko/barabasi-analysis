package alex.pub

enum class Subjects(val subject: String, val link: String, val group: Int) {

    BIONETWORKS("Biological networks", "http://barabasi.com/publications/25/biological-networks", 1),
    COMPUTER_NETWORKS("Computer networks", "http://barabasi.com/publications/27/computer-networks", 2),
    NETWORK_SCIENCE("Network science", "http://barabasi.com/publications/24/network-science", 3),
    HUMAN_DYNAMICS("Human dynamics", "http://barabasi.com/publications/10/human-dynamics", 4),
    BIOPHYSICS("Biophysics", "http://barabasi.com/publications/12/biophysics", 5),
    STATISTICAL_MEHANICS("Statistical mechanics", "http://barabasi.com/publications/13/statistical-mechanics", 6);


    companion object {
        fun getGroupBySubject(subject: String) : Int {
            for (value in values()) {
                if(value.subject == subject)
                    return value.group
            }
            return -1
        }

    }


}