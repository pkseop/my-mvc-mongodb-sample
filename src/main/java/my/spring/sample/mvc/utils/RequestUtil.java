package my.spring.sample.mvc.utils;

import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

    // AWS의 로드밸런서를 사용할 때에도 클라어인트 IP를 가져올 수 있다.
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getOrigin(HttpServletRequest request) {
        return request.getHeader("origin");
    }

    public static String getSubdomainFromOrigin(HttpServletRequest request) {
        return StringUtil.extractSubdomain(request.getHeader("origin"));
    }

    public static void verifyToken(HttpServletRequest request, String tokenOrg) throws RuntimeException {
        final String authorizationHeader = request.getHeader("Authorization");
        if(Strings.isNullOrEmpty(authorizationHeader)) {
            throw new RuntimeException("Not authorized access.");
        }
        String token = authorizationHeader.split("\\s+")[1];
        if(Strings.isNullOrEmpty(token) || tokenOrg.equals(token) == false) {
            throw new RuntimeException("Not authorized access.");
        }
    }
}
