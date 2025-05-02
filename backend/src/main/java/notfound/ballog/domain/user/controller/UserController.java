package notfound.ballog.domain.user.controller;

import lombok.RequiredArgsConstructor;
import notfound.ballog.domain.user.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
}
