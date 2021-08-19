package org.unibl.etf.youtubetrimmer.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.youtubetrimmer.api.model.request.TokenRequest;
import org.unibl.etf.youtubetrimmer.api.service.AuthService;
import org.unibl.etf.youtubetrimmer.api.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<String> getToken(@RequestBody TokenRequest tokenRequest) {

        String token = tokenRequest.getToken();
        if (token == null) {
            String uid = userService.createNewUser();
            return ResponseEntity.ok(authService.generateTokenForUser(uid));
        }
        if(!authService.isTokenValid(token))
            return ResponseEntity.badRequest().body("Invalid token");

        return ResponseEntity.ok(authService.issueToken(token));
    }
}
