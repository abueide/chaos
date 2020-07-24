plugins {
	kotlin("jvm") version "1.3.72" apply false
}

repositories{
	mavenCentral()
}

allprojects{
	version = "1.0"
    group = "com.abysl.chaos"
}
subprojects {
	tasks.register("hello") {
		doLast {
			println("I'm ${this.project.name}")
		}
	}
}

configure(subprojects.filter {it.name == "manager" || it.name == "proxy"}) {
}
