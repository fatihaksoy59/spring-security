package com.fatih.auth;

import com.fatih.auth.config.JwtService;
import com.fatih.auth.dto.AuthenticationResponse;
import com.fatih.auth.dto.LoginRequest;
import com.fatih.auth.dto.RegisterRequest;
import com.fatih.auth.user.Role;
import com.fatih.auth.user.User;
import com.fatih.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest registerRequest) {
    User user =
        User.builder()
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .role(Role.ADMIN)
            .build();
    userRepository.save(user);
    String token = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(token).build();
  }

  public AuthenticationResponse login(LoginRequest loginRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(), loginRequest.getPassword()));
    User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
    String token = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(token).build();
  }
}
