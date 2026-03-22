package keysson.apis.administration.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import keysson.apis.administration.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class DynamicURLFilter extends OncePerRequestFilter {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Ignorar caminhos públicos
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Se não estiver autenticado, deixa o Spring Security padrão tratar (vai dar 401)
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Administrador pula a validação de URL (acesso total)
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Busca no banco qual módulo protege esta URL
        // (Em produção, o ideal é usar um Cache aqui para não bater no banco em cada request)
        Integer requiredModuloId = findRequiredModulo(path, method);

        // 3. Se a URL não estiver mapeada em nenhum módulo, negamos por segurança (Default Deny)
        if (requiredModuloId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Recurso nao mapeado no sistema de permissoes.");
            return;
        }

        // 4. Verifica se o usuário possui esse modulo_id no Token
        String token = (String) request.getAttribute("CleanJwt");
        List<Map<String, Object>> userModules = jwtUtil.extractModules(token);

        boolean hasAccess = false;
        if (userModules != null) {
            for (Map<String, Object> module : userModules) {
                // Aqui dependemos do api-validacao ter colocado o 'id' do módulo no token.
                // Vou ajustar o api-validacao para garantir que o ID venha junto com nome/chave.
                Object moduleIdObj = module.get("id"); 
                if (moduleIdObj != null && moduleIdObj.toString().equals(requiredModuloId.toString())) {
                    hasAccess = true;
                    break;
                }
            }
        }

        if (hasAccess) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Voce nao tem permissao para acessar este modulo.");
        }
    }

    private boolean isPublicPath(String path) {
        return path.contains("/actuator") || path.contains("/login") || path.contains("/register");
    }

    private Integer findRequiredModulo(String path, String method) {
        // Agora buscamos diretamente na tabela 'modulos' o padrão de URL da API
        String sql = "SELECT id, api_url_pattern FROM modulos WHERE api_url_pattern IS NOT NULL";
        List<Map<String, Object>> modules = jdbcTemplate.queryForList(sql);

        for (Map<String, Object> m : modules) {
            String pattern = (String) m.get("api_url_pattern");

            // Se a URL que o usuário chamou bater com o padrão do módulo (ex: /administracao/**)
            if (pathMatcher.match(pattern, path)) {
                return (Integer) m.get("id");
            }
        }
        return null;
    }
}
