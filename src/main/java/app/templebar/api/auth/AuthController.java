package app.templebar.api.auth;

import app.templebar.api.auth.dto.SignInRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/sign-in")
    public void signIn(@Valid @RequestBody SignInRequest request, HttpServletResponse response) {

        String token = authService.signIn(request);

        ResponseCookie cookie = ResponseCookie.from(
                        "access_token",
                        token
                )
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );
    }

    @PostMapping("/auth/sign-out")
    public void signOut(
            HttpServletResponse response
    ) {

        ResponseCookie cookie = ResponseCookie.from(
                        "access_token",
                        ""
                )
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );
    }
}
