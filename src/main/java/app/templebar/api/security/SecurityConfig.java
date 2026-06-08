package app.templebar.api.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)

            .cors(Customizer.withDefaults())

            .formLogin(AbstractHttpConfigurer::disable)

            .httpBasic(AbstractHttpConfigurer::disable)

            .logout(AbstractHttpConfigurer::disable)

            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(
                        (request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                    )

            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class)

            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/auth/sign-in")
                .permitAll()

                .requestMatchers("/auth/sign-out")
                .authenticated()

                .requestMatchers(HttpMethod.GET,"/events")
                .permitAll()

                .requestMatchers(HttpMethod.POST,"/events")
                .authenticated()

                .requestMatchers(HttpMethod.PATCH,"/events/*")
                .authenticated()

                .requestMatchers(HttpMethod.DELETE,"/events/*")
                .authenticated()

                .requestMatchers(HttpMethod.POST,"/users")
                .authenticated()

                .anyRequest().denyAll()
            );

        return http.build();
    }
}
