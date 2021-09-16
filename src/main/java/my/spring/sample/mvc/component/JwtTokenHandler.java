package my.spring.sample.mvc.component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import my.spring.sample.mvc.collection.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenHandler {

	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.refresh.secret}")
	private String refreshSecret;
	
	@Value("${jwt.access.token.expiration}")
	private long accessTokenExpiration;
	
	@Value("${jwt.refresh.token.expiration}")
	private long refreshTokenExpiration;

	
	public String getId(String token) throws Exception {
		return (String)getAllClaimsFromToken(token).get("id");
	}
	
	//retrieve username from jwt token
	public String getUsername(String token) throws Exception {
		return (String)getAllClaimsFromToken(token).get("username");
	}
	
	public String getName(String token) throws Exception {
		String urlEnc = (String)getAllClaimsFromToken(token).get("name");
		return URLDecoder.decode(urlEnc, "UTF-8");
	}
	
	//retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getExpiration);
	}
	
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws Exception {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	
	//for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) throws Exception {
		return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
	}
	
	private Claims getAllClaimsFromRefreshToken(String refreshToken) throws Exception {
		return Jwts.parser().setSigningKey(refreshSecret.getBytes()).parseClaimsJws(refreshToken).getBody();
	}
	
	//check if the token has expired
	private Boolean isTokenExpired(String token) throws Exception {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public Map<String, String> generateToken(String origin, String email) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("username", email);
		return doGenerateToken(claims);
	}
	
	public Map<String, String> generateToken(User user) throws UnsupportedEncodingException {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", user.getId());
		claims.put("username", user.getUsername());
		claims.put("name", URLEncoder.encode(user.getName(), "UTF-8"));
		return doGenerateToken(claims);
	}
	
	//while creating the token -
	//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
	//2. Sign the JWT using the HS512 algorithm and secret key.
	//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	//   compaction of the JWT to a URL-safe string 
	private String doGenerateToken(Map<String, Object> claims, String subject, String secret, long tokenValidity) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + tokenValidity * 1000))
			.signWith(SignatureAlgorithm.HS256, secret.getBytes()).compact();
	}
	
	private Map<String, String> doGenerateToken(Map<String, Object> claims) {
		String accessToken = this.doGenerateToken(claims, null, secret, accessTokenExpiration);
		
		Map<String, Object> refreshTokenClaims = Maps.newHashMap();
		refreshTokenClaims.put("accessToken", accessToken);
		
		String refreshToken = this.doGenerateToken(refreshTokenClaims, null, refreshSecret, refreshTokenExpiration);
		
		return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
	}
	
	public Boolean validateToken(String token) throws Exception {
		return !isTokenExpired(token);
	}
	
	public String getAccessToken(String refreshToken) throws Exception{
		return (String)getAllClaimsFromRefreshToken(refreshToken).get("accessToken");
	}
	
	public Boolean validateRefreshToken(String accessToken, String refreshToken) throws Exception {
		if(Strings.isNullOrEmpty(accessToken))
			return false;

		final String accessTokenInToken = getAccessToken(refreshToken);
		if(accessToken.equals(accessTokenInToken)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Map<String, Object> extractClaims(String accessToken) throws JsonMappingException, JsonProcessingException {
		String[] parts = accessToken.split("\\.");
		byte[] data = BaseEncoding.base64().decode(parts[1]);//Base64.getDecoder().decode(parts[1].getBytes());
		String str = new String(data);
		
		ObjectMapper om = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> claims = om.readValue(str, Map.class);
		
		return claims;
	}
	
	private String extractOrigin(String accessToken) throws JsonMappingException, JsonProcessingException {
		Map<String, Object> claims = extractClaims(accessToken);
		return (String)claims.get("origin");
	}
	
	private String extractOriginFromRefreshToken(String refreshToken) throws JsonMappingException, JsonProcessingException {
		Map<String, Object> claims = extractClaims(refreshToken);
		
		String accessToken = (String)claims.get("accessToken");
		return extractOrigin(accessToken);
	}
	
	public Map<String, String> doRefreshToken(String accessToken, String refreshToken) throws JsonParseException, JsonMappingException, Exception {
		Map<String, Object> map = extractClaims(accessToken);

		Map<String, Object> claims = new HashMap<>();
		claims.put("id", map.get("id"));
		claims.put("username", map.get("username"));
		claims.put("name", map.get("name"));

		return doGenerateToken(claims);
	}
}
