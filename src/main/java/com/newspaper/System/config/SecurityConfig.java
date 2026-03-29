    package com.newspaper.System.config;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.web.cors.CorsConfigurationSource;

    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity
    public class SecurityConfig {

        @Autowired
        private JwtFilter jwtFilter;

        @Autowired
        private CorsConfigurationSource corsConfigurationSource;

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

            http
                    .cors(cors -> cors.configurationSource(corsConfigurationSource))
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authorizeHttpRequests(auth -> auth
                            // ✅ Allow ALL preflight requests
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                            // ✅ Explicitly allow register + login
                            .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                            .requestMatchers("/api/auth/**").permitAll()

                            .requestMatchers("/api/public/**").permitAll()

                            .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                            .requestMatchers("/api/vendor/**").hasAuthority("ROLE_VENDOR")
                            .requestMatchers("/api/user/**").hasAuthority("ROLE_USER")

                            .requestMatchers("/api/payment/all").hasAuthority("ROLE_ADMIN")
                            .requestMatchers("/api/payment/**").hasAuthority("ROLE_USER")
                            .requestMatchers("/api/invoices/generate-test").permitAll()
                            .requestMatchers("/api/profile/**").hasAuthority("ROLE_USER")

                            .anyRequest().authenticated()
                    );

            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

    }