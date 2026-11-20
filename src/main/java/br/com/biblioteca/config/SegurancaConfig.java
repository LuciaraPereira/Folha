package br.com.biblioteca.config;

import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
@Configuration
public class SegurancaConfig {
@Bean PasswordEncoder encoder() { return new BCryptPasswordEncoder(); }
@Bean UserDetailsService usuarios(UsuarioRepository repo) { return login -> { Usuario u=repo.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado")); return User.withUsername(u.login).password(u.senha).roles(u.perfil).build(); }; }
@Bean SecurityFilterChain seguranca(HttpSecurity http) throws Exception {
http.authorizeHttpRequests(a -> a.requestMatchers("/login.html", "/style.css", "/login.js", "/api/csrf", "/error").permitAll().requestMatchers("/api/usuarios/**").hasRole("ADMIN").anyRequest().authenticated())
.formLogin(f -> f.loginPage("/login.html").loginProcessingUrl("/login").defaultSuccessUrl("/",true).failureUrl("/login.html?erro").permitAll())
.logout(l -> l.logoutSuccessUrl("/login.html?saida"))
.csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).csrfTokenRequestHandler(new org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler()));
return http.build();
}
@Bean CommandLineRunner adminInicial(UsuarioRepository repo, PasswordEncoder encoder, @Value("${ADMIN_PASSWORD:}") String senha) {
return args -> { if(repo.findByLogin("admin").isEmpty()) { if(senha.isBlank()) throw new IllegalStateException("Defina ADMIN_PASSWORD para criar o administrador inicial"); Usuario u=new Usuario(); u.nome="Administrador"; u.login="admin"; u.perfil="ADMIN"; u.senha=encoder.encode(senha); repo.save(u); } };
}
}