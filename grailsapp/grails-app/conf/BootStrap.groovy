import androidtrackerweb.Location
import androidtrackerweb.SecRole
import androidtrackerweb.SecUser
import androidtrackerweb.SecUserSecRole


class BootStrap {

    def init = { servletContext ->
        securityData()
        //TimeZone.setDefault(TimeZone.getTimeZone("Europe/Prague"))
        /*for (int i = 0; i < 12; i++) {
            def location = new Location(latitude: "50.32",longitude: "14.21",time: new Date(),secUser: SecUser.findByUsername("john")).save()
        }*/
    }
    def securityData(){
        def roleAdmin = new SecRole(authority: 'ROLE_ADMIN').save()
        def roleUser = new SecRole(authority: 'ROLE_USER').save()
        def admin = new SecUser(username: 'john',password: 'doe',enabled: true).save()
        SecUserSecRole.create admin, roleAdmin
    }
    def destroy = {
    }
}
