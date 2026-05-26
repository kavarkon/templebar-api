package app.templebar.api.auth;

import app.templebar.api.auth.dto.SignInRequest;
import app.templebar.api.common.exception.InvalidPasswordException;
import app.templebar.api.common.exception.UserNotFoundException;
import app.templebar.api.security.JwtService;
import app.templebar.api.user.User;
import app.templebar.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String signIn(SignInRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidPasswordException();
        }

        return jwtService.generateToken(user.getId());
    }
}
