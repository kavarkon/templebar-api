package app.templebar.api.user;

import app.templebar.api.user.dto.CreateUserRequest;
import app.templebar.api.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest request) {

        boolean userExists = userRepository
                .findByEmail(request.email())
                .isPresent();

        if (userExists) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();

        user.setEmail(request.email());

        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail()
        );
    }
}
