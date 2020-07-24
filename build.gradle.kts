subprojects {
	tasks.register("hello") {
		doLast {
			println("I'm ${this.project.name}")
		}
	}
}

task("test") {
    println("test")
	subprojects.forEach {project ->
        println(project.pluginManager.hasPlugin("cpp-flibrary"))
	}
}

configure(
		subprojects.filter {project ->
            project.plugins.forEach { println(it) }
			true
		})
{
	tasks.named("hello"){
		doLast {
			println("- I'm a cpp project")
		}
	}
}
