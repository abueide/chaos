plugins {
	kotlin("jvm") version kotlinVersion apply false
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
