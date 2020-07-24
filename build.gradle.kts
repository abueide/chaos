plugins {
	kotlin("jvm") version "1.3.72" apply false
}

repositories{
	mavenCentral()
    jcenter()
}

allprojects{
	version = "1.0"
    group = "com.abysl.chaos"
}
subprojects {
}

configure(subprojects.filter {it.name == "manager" || it.name == "proxy"}) {
}
