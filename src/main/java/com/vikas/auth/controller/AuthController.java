package com.vikas.auth.controller;

import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vikas.auth.dto.LoginRequest;
import com.vikas.auth.dto.LoginResponse;
import com.vikas.auth.dto.RegisterRequest;
import com.vikas.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Class      : AuthController
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
	    name = "Auth APIs",
	    description = "APIs for ragister and login users"
	)
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register User", description = "register new user and return back response with jwt token")
  	@ApiResponses({ @ApiResponse(responseCode = "201", description = "User Added"),
  					@ApiResponse(responseCode = "400", description = "Invalid request data"),
  					@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
		LoginResponse register = authService.register(request);
		return Objects.nonNull(register) ? 
								ResponseEntity.ok(register) : 
								ResponseEntity.internalServerError().build();
	}

    @PostMapping(value = "/login",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login User", description = "check user credentials and return back response with jwt token")
  	@ApiResponses({ @ApiResponse(responseCode = "201", description = "User Added"),
  					@ApiResponse(responseCode = "400", description = "Invalid request data"),
  					@ApiResponse(responseCode = "500", description = "Internal server error") })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse login = authService.login(request);
		return Objects.nonNull(login) ? 
								ResponseEntity.ok(login) : 
								ResponseEntity.internalServerError().build();
	}
}