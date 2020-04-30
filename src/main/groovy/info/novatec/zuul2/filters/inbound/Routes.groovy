package info.novatec.zuul2.filters.inbound

import com.netflix.zuul.context.SessionContext
import com.netflix.zuul.filters.http.HttpInboundSyncFilter
import com.netflix.zuul.message.http.HttpRequestMessage
import com.netflix.zuul.netty.filter.ZuulEndPointRunner
import groovy.util.logging.Slf4j
import info.novatec.zuul2.filters.endpoint.BadRequestEndpoint
import info.novatec.zuul2.filters.endpoint.NotFoundEndpoint
/**
 * Routing filter on base of HttpInboundSyncFilter.<br/>
 * URIs of the proxied backend services are defined in {@code application.properties}
 */
@Slf4j
class Routes extends HttpInboundSyncFilter {
    @Override
    HttpRequestMessage apply(HttpRequestMessage request) {
        SessionContext context = request.getContext()

        println request.toString()

        log.info "Routing to: {}", request.path
        switch (request.getPath()) {
            case "/users/1":
                context.setEndpoint(ZuulEndPointRunner.PROXY_ENDPOINT_FILTER_NAME)
                context.setRouteVIP("order")
                break
            case "/order/users/1":
                context.setEndpoint(ZuulEndPointRunner.PROXY_ENDPOINT_FILTER_NAME)
                context.setRouteVIP("users")
                break
            case "/healthcheck":
                println("checking health...")
                context.setEndpoint(BadRequestEndpoint.class.getCanonicalName())
            default:
                context.setEndpoint(NotFoundEndpoint.class.getCanonicalName())
        }
        return request
    }

    @Override
    int filterOrder() {
        return 0
    }

    @Override
    boolean shouldFilter(HttpRequestMessage request) {
        return true
    }
}
