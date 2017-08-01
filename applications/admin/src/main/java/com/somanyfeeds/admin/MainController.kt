package com.somanyfeeds.admin

import com.somanyfeeds.jetty.JettyController

class MainController : JettyController({

    get("/") { request, response ->
        response.sendRedirect("/feeds")
    }
})
