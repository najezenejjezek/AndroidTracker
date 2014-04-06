class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/addLocation" controller: 'rest', action: 'addLocation'
        "/login/$action?" controller: "login"
        "/logout/$action?" controller: "logout"
        "/roleSwitch" controller: 'content', action: 'roleSwitch'
        //"/"(view:"location/index", controller: "location", action: "index")
        "/"(view:"/mainPage", )
        "500"(view:'/error')
	}
}
