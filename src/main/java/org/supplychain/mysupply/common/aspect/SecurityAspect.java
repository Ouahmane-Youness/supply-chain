package org.supplychain.mysupply.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.supplychain.mysupply.common.exception.UnauthorizedException;
import org.supplychain.mysupply.user.enums.Role;
import org.supplychain.mysupply.user.model.User;
import org.supplychain.mysupply.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    private final UserRepository userRepository;

    @Before("execution(* org.supplychain.mysupply..controller.*.*(..)) && !execution(* org.supplychain.mysupply.user.controller.*.*(..))")
    public void checkSecurity(JoinPoint joinPoint) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String email = request.getHeader("email");
        String password = request.getHeader("password");

        if (email == null || email.isEmpty()) {
            throw new UnauthorizedException("Email header is missing");
        }

        if (password == null || password.isEmpty()) {
            throw new UnauthorizedException("Password header is missing");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.getPassword().equals(password)) {
            throw new UnauthorizedException("Invalid email or password");
        }

        checkRoleAuthorization(user, joinPoint);

        SecurityContext.setCurrentUser(user);
    }

    private void checkRoleAuthorization(User user, JoinPoint joinPoint) {

        if (user.getRole() == Role.ADMIN) {
            return;
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();

        if (className.contains("Supplier") || className.contains("RawMaterial") || className.contains("SupplyOrder")) {
            List<Role> allowedRoles = Arrays.asList(
                    Role.GESTIONNAIRE_APPROVISIONNEMENT,
                    Role.RESPONSABLE_ACHATS,
                    Role.SUPERVISEUR_LOGISTIQUE
            );

            if (!allowedRoles.contains(user.getRole())) {
                throw new UnauthorizedException("Access denied: Your role does not have permission to access this resource");
            }
        }

        else if (className.contains("Product") || className.contains("ProductionOrder")) {
            List<Role> allowedRoles = Arrays.asList(
                    Role.CHEF_PRODUCTION,
                    Role.PLANIFICATEUR,
                    Role.SUPERVISEUR_PRODUCTION
            );

            if (!allowedRoles.contains(user.getRole())) {
                throw new UnauthorizedException("Access denied: Your role does not have permission to access this resource");
            }
        }

        else if (className.contains("Customer") || className.contains("Order") || className.contains("Delivery")) {
            List<Role> allowedRoles = Arrays.asList(
                    Role.GESTIONNAIRE_COMMERCIAL,
                    Role.RESPONSABLE_LOGISTIQUE,
                    Role.SUPERVISEUR_LIVRAISONS
            );

            if (!allowedRoles.contains(user.getRole())) {
                throw new UnauthorizedException("Access denied: Your role does not have permission to access this resource");
            }
        }
    }
}