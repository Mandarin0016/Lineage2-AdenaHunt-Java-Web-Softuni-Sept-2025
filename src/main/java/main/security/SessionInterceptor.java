package main.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.model.Player;
import main.service.PlayerService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.UUID;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final PlayerService playerService;

    public static final Set<String> UNAUTHENTICATED_ENDPOINTS = Set.of("/login", "/register", "/", "/error");
    public static final String USER_ID_FROM_SESSION = "user_id";

    public SessionInterceptor(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String endpoint = request.getServletPath();
        if (UNAUTHENTICATED_ENDPOINTS.contains(endpoint)) {
            return true;
        }

        if (request.getSession(false) == null || request.getSession(false).getAttribute(USER_ID_FROM_SESSION) == null) {
            response.sendRedirect("/");
            return false;
        }

        //TODO: Implement class selection page enforcement logic here
        UUID userId = (UUID) request.getSession(false).getAttribute(USER_ID_FROM_SESSION);
        Player player = playerService.getById(userId);

        boolean hasClass = player.getPlayerClass() != null;
        boolean isClassEndpoint = endpoint.contains("class");

        if (hasClass && isClassEndpoint) {
            response.sendRedirect("/lobby");
            return false;
        }

        if (!hasClass && !isClassEndpoint) {
            response.sendRedirect("/class");
            return false;
        }

        return true;
    }
}
