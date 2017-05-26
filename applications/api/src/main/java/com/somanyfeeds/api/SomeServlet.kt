package com.somanyfeeds.api

import javax.servlet.*

class SomeServlet: Servlet {
    override fun getServletConfig(): ServletConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun destroy() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun init(config: ServletConfig?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getServletInfo(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun service(req: ServletRequest?, res: ServletResponse?) {
        if (req != null) {
            val map = req.parameterMap

            map?.get("")
        }

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
