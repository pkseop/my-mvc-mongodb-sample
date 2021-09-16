package my.spring.sample.mvc.filter;

import com.google.common.base.Strings;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import my.spring.sample.mvc.component.JwtTokenHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenHandler jwtTokenHandler;

    public CustomAuthorizationFilter(JwtTokenHandler jwtTokenHandler) {
        this.jwtTokenHandler = jwtTokenHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.split("\\s+")[1];
            try {
                username = jwtTokenHandler.getUsername(jwtToken);
                // TODO: check logout token by redis
//                String key = AuthService.PREFIX_INVALID_TOKEN + jwtToken;
//                String val = (String) redisCon.getValue(key);
//                if(Strings.isNullOrEmpty(val) == false) {
//                    log.info("[WARN] Access with invalid token [{}]", jwtToken);
//                    username = null; // invalid token.
//                }
            } catch (IllegalArgumentException e) {
                log.info("[INFO] Unable to get JWT Token. From [{}], message: [{}]", new Object[] { request.getRemoteAddr(), e.getMessage() });
            } catch (ExpiredJwtException e) {
                log.info("[INFO] JWT Token has expired. From [{}], message: [{}]", new Object[] { request.getRemoteAddr(), e.getMessage() });
                response.setHeader("jwt-expired", "true");
            } catch (Exception e) {
                log.debug("[INFO] Invalid JWT Token. From [{}], message: [{}]", new Object[] { request.getRemoteAddr(), e.getMessage() });
            }
        } else {
            log.debug("[INFO] JWT Token does not begin with Bearer String. user: [{}]", username);
        }
        // Once we get the token validate it.
        if (Strings.isNullOrEmpty(username) == false && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // if token is valid configure Spring Security to manually set
                // authentication
                if (jwtTokenHandler.validateToken(jwtToken)) {
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } catch (Exception e) {
                log.error("Invalid JWT Token", e);
            }

        }
        filterChain.doFilter(request, response);
    }

}
