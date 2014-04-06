package androidtrackerweb

import grails.transaction.Transactional

@Transactional
class LocationService {
    def springSecurityService
    def loadLocationsForUser(params) {
        def principal = springSecurityService.principal
        SecUser secUser = SecUser.findById(principal.id)
        def role = SecUserSecRole.findBySecUser(secUser).getSecRole()
        if (role.authority == "ROLE_ADMIN")
            Location.list(params)
        else
            Location.findAllBySecUser(secUser,params)
    }

    def countLocationsForUser(){
        def principal = springSecurityService.principal
        SecUser secUser = SecUser.findById(principal.id)
        def role = SecUserSecRole.findBySecUser(secUser).getSecRole()
        if (role.authority == "ROLE_ADMIN")
            Location.count()
        else
            Location.findAllBySecUser(secUser).size()
    }
}
