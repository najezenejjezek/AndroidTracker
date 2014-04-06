package androidtrackerweb
import groovy.json.JsonSlurper
class RestController {

    def index() {}

    def addLocation(){
        log.info("receive json")
        def restRequest = request.JSON
        log.info(restRequest.toString())
        def list = new JsonSlurper().parseText( restRequest.toString())

        list.each {
            def instance = new Location(latitude: it.latitude, longitude: it.longitude, time: it.time,
                    provider: it.provider, accuracy: it.accuracy, secUser: SecUser.findByUsername(it.username))
            instance.save(flush:true)  }



    }
}
