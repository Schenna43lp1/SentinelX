package com.sentinelx.server.security;

import com.sentinelx.server.domain.entity.Node;
import com.sentinelx.server.repository.NodeRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Validates Bearer tokens from agent requests against known node tokens.
 * Sets a minimal authentication context so agent endpoints can proceed.
 */
@Component
public class AgentTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";

    private final NodeRepository nodeRepository;

    public AgentTokenFilter(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only apply token check to agent API paths
        if (!path.startsWith("/api/v1/agent/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Agent registration doesn't require a node token
        if (path.equals("/api/v1/agent/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(AUTH_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing agent token");
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        Optional<Node> node = nodeRepository.findByAgentToken(token);

        if (node.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid agent token");
            return;
        }

        // Store node ID in request attribute for downstream use
        request.setAttribute("authenticatedNodeId", node.get().getId());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            "agent:" + node.get().getId(),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_AGENT"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
