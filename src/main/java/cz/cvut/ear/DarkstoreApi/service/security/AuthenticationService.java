package cz.cvut.ear.DarkstoreApi.service.security;

import cz.cvut.ear.DarkstoreApi.model.User;
import cz.cvut.ear.DarkstoreApi.repository.UserRepository;
import cz.cvut.ear.DarkstoreApi.dto.user.authentication.AuthenticationRequest;
import cz.cvut.ear.DarkstoreApi.dto.user.authentication.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        User user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }
}
