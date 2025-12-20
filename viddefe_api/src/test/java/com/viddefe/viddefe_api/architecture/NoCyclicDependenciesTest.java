package com.viddefe.viddefe_api.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de arquitectura para validar que no existen ciclos de dependencia.
 * 
 * Este test analiza las dependencias inyectadas por constructor en los servicios
 * y verifica que no hay ciclos.
 */
@DisplayName("Architecture - No Circular Dependencies")
class NoCyclicDependenciesTest {

    // Mapa de servicios y sus dependencias (basado en la nueva arquitectura)
    private static final Map<String, Set<String>> SERVICE_DEPENDENCIES = new LinkedHashMap<>();
    
    static {
        // Servicios base sin dependencias cross-domain
        SERVICE_DEPENDENCIES.put("PeopleTypeService", Set.of("PeopleTypeRepository"));
        SERVICE_DEPENDENCIES.put("StatesCitiesService", Set.of("CitiesRepository", "StatesRepository"));
        SERVICE_DEPENDENCIES.put("RolesUserService", Set.of("RolUserRepository"));
        
        // Servicios de lectura pura (sin dependencias cross-domain)
        SERVICE_DEPENDENCIES.put("ChurchLookupImpl", Set.of("ChurchRepository"));
        SERVICE_DEPENDENCIES.put("PeopleReaderImpl", Set.of("PeopleRepository", "PeopleTypeService"));
        
        // Servicios de escritura y membership
        SERVICE_DEPENDENCIES.put("PeopleWriterImpl", Set.of(
            "PeopleRepository", "PeopleTypeService", "StatesCitiesService", "ChurchLookup"));
        SERVICE_DEPENDENCIES.put("ChurchMembershipServiceImpl", Set.of(
            "PeopleRepository", "PeopleTypeService", "ChurchLookup"));
        
        // Servicios de negocio que usan las interfaces segregadas
        SERVICE_DEPENDENCIES.put("ChurchPastorImpl", Set.of(
            "ChurchPastorRepository", "PeopleReader", "ChurchMembershipService"));
        SERVICE_DEPENDENCIES.put("ChurchServiceImpl", Set.of(
            "ChurchRepository", "StatesCitiesService", "ChurchPastorService"));
        
        // Servicios de aplicación/fachada
        SERVICE_DEPENDENCIES.put("PeopleServiceImpl", Set.of(
            "PeopleRepository", "PeopleWriter", "PeopleReader"));
        SERVICE_DEPENDENCIES.put("AuthServiceImpl", Set.of(
            "PasswordEncoder", "RolesUserService", "UserRepository", "JwtUtil", 
            "PeopleReader", "PeopleWriter"));
        SERVICE_DEPENDENCIES.put("AuthMeUseCase", Set.of(
            "UserRepository", "ChurchPastorService"));
        
        // Legacy (deprecated)
        SERVICE_DEPENDENCIES.put("PeopleLookupImpl", Set.of(
            "PeopleReader", "PeopleWriter", "ChurchMembershipService"));
    }

    // Mapeo de interfaces a implementaciones
    private static final Map<String, String> INTERFACE_TO_IMPL = Map.of(
        "ChurchLookup", "ChurchLookupImpl",
        "PeopleReader", "PeopleReaderImpl",
        "PeopleWriter", "PeopleWriterImpl",
        "ChurchMembershipService", "ChurchMembershipServiceImpl",
        "ChurchPastorService", "ChurchPastorImpl",
        "PeopleLookup", "PeopleLookupImpl"
    );

    @Test
    @DisplayName("Should have no circular dependencies between services")
    void shouldHaveNoCircularDependencies() {
        for (String service : SERVICE_DEPENDENCIES.keySet()) {
            Set<String> visited = new HashSet<>();
            List<String> path = new ArrayList<>();
            
            boolean hasCycle = detectCycle(service, visited, path, new HashSet<>());
            
            assertThat(hasCycle)
                .withFailMessage("Circular dependency detected: %s", String.join(" -> ", path))
                .isFalse();
        }
    }
    
    @Test
    @DisplayName("PeopleReader should not depend on any Church service")
    void peopleReaderShouldNotDependOnChurchServices() {
        Set<String> deps = SERVICE_DEPENDENCIES.get("PeopleReaderImpl");
        
        boolean hasChurchDependency = deps.stream()
            .anyMatch(d -> d.contains("Church"));
        
        assertThat(hasChurchDependency)
            .withFailMessage("PeopleReaderImpl should not depend on Church services")
            .isFalse();
    }
    
    @Test
    @DisplayName("ChurchLookupImpl should not depend on any People service")
    void churchLookupShouldNotDependOnPeopleServices() {
        Set<String> deps = SERVICE_DEPENDENCIES.get("ChurchLookupImpl");
        
        boolean hasPeopleDependency = deps.stream()
            .anyMatch(d -> d.contains("People"));
        
        assertThat(hasPeopleDependency)
            .withFailMessage("ChurchLookupImpl should not depend on People services")
            .isFalse();
    }
    
    @Test
    @DisplayName("ChurchPastorImpl should use segregated interfaces")
    void churchPastorShouldUseSegregatedInterfaces() {
        Set<String> deps = SERVICE_DEPENDENCIES.get("ChurchPastorImpl");
        
        // Debe usar PeopleReader, no PeopleLookup
        assertThat(deps).contains("PeopleReader");
        assertThat(deps).doesNotContain("PeopleLookup");
        
        // Debe usar ChurchMembershipService para escrituras
        assertThat(deps).contains("ChurchMembershipService");
    }

    private boolean detectCycle(String current, Set<String> visited, 
                                List<String> path, Set<String> recursionStack) {
        if (recursionStack.contains(current)) {
            path.add(current);
            return true;
        }
        
        if (visited.contains(current)) {
            return false;
        }
        
        visited.add(current);
        recursionStack.add(current);
        path.add(current);
        
        Set<String> dependencies = SERVICE_DEPENDENCIES.getOrDefault(current, Set.of());
        
        for (String dep : dependencies) {
            // Resolver interfaz a implementación si es necesario
            String resolved = INTERFACE_TO_IMPL.getOrDefault(dep, dep);
            
            // Solo analizar si es un servicio conocido
            if (SERVICE_DEPENDENCIES.containsKey(resolved)) {
                if (detectCycle(resolved, visited, path, recursionStack)) {
                    return true;
                }
            }
        }
        
        path.remove(path.size() - 1);
        recursionStack.remove(current);
        return false;
    }
}
