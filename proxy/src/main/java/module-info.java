module com.abysl.chaos.proxy {
    requires kotlin.stdlib;
    requires ktor.server.netty;
    requires ktor.server.core;
    requires ktor.server.host.common;
    requires ktor.http.jvm;
    exports com.abysl.chaos.proxy;
}
