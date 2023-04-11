package wedding.backend.app

import aws.sdk.kotlin.services.kms.KmsClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}

@RestController
class MessageController {
	@GetMapping("/")
	fun index(@RequestParam("name", required = false, defaultValue = "anonymous") name: String) = "Hello there, $name!"

//	@PostMapping("/login")
//	fun login() {
//		KmsClient.
//	}
}
