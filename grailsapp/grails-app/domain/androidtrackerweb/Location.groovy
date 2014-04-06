package androidtrackerweb

class Location {
    String latitude
    String longitude
    String provider
    Date time
    String accuracy
    SecUser secUser


    static constraints = {
        longitude blank: false, nullable: false,maxSize: 20
        latitude blank: false, nullable: false,maxSize: 20
        time nullable: false
        provider nullable: true
        accuracy nullable: true
        secUser nullable: true
    }

    static mapping = {
        sort time: "desc"
    }
}
