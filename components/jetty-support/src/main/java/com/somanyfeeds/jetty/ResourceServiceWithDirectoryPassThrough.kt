package com.somanyfeeds.jetty

import org.eclipse.jetty.http.HttpContent
import org.eclipse.jetty.server.ResourceService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ResourceServiceWithDirectoryPassThrough : ResourceService() {

    init {
        gzipEquivalentFileExtensions = listOf(".svgz")
    }

    override fun notFound(request: HttpServletRequest?, response: HttpServletResponse?) {
    }

    override fun sendWelcome(content: HttpContent?, pathInContext: String?, endsWithSlash: Boolean, included: Boolean, request: HttpServletRequest?, response: HttpServletResponse?) {
        val welcomeFile = welcomeFactory.getWelcomeFile(pathInContext)

        if (welcomeFile != null) {
            super.sendWelcome(content, pathInContext, endsWithSlash, included, request, response)
        }

        // do not handle the request at all. pass through.
    }
}
