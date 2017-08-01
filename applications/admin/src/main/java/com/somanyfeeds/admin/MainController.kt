package com.somanyfeeds.admin

import com.somanyfeeds.jetty.JettyController

class MainController : JettyController({

    get("/") { _, response ->
        response.sendRedirect("/feeds")
    }
})
