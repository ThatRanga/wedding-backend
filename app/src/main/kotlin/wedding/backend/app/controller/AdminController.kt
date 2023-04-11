package wedding.backend.app.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/admin")
@RestController
class AdminController {

    @GetMapping("/check")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminCheck() = "Hello Admin!"

}