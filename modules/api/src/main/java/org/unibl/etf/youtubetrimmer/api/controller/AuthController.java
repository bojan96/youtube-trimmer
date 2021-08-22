package org.unibl.etf.youtubetrimmer.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.youtubetrimmer.api.model.request.LoginRequest;
import org.unibl.etf.youtubetrimmer.api.service.AuthService;
import org.unibl.etf.youtubetrimmer.api.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.of(authService.login(loginRequest.getUsername(), loginRequest.getPassword()));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody String token) {

        if (!authService.isTokenValid(token, false)) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        return ResponseEntity.ok(authService.issueToken(token));
    }
}
