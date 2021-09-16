package my.spring.sample.mvc.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import my.spring.sample.mvc.collection.User;
import my.spring.sample.mvc.component.JwtTokenHandler;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenHandler jwtTokenHandler;
    private ObjectMapper objectMapper;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenHandler jwtTokenHandler) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenHandler = jwtTokenHandler;
    }

    private ObjectMapper getObjectMapper() {
        if(this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
        return this.objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        log.info("User login. username: [{}], password: [{}]", new Object[]{username, password});

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User)authResult.getPrincipal();

        Map<String, String> tokens = jwtTokenHandler.generateToken(user);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        getObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
