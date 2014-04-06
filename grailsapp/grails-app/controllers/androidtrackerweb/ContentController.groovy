package androidtrackerweb

class ContentController {

    def index() {
    }
    def roleSwitch() {
        redirect controller: 'location', action: 'index'
    }
}
