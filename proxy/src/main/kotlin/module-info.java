module com.abysl.chaos.proxy {
    requires kotlin.stdlib;
    requires ktor.io.jvm;
    requires ktor.utils.jvm;
    requires kotlinx.coroutines.core;
    requires ktor.network;
    requires org.bouncycastle.provider;
    exports com.abysl.chaos.proxy;
}
