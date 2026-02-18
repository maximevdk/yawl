package com.yawl.http;

import com.yawl.annotations.DeleteMapping;
import com.yawl.annotations.GetMapping;
import com.yawl.annotations.PostMapping;
import com.yawl.annotations.PutMapping;
import com.yawl.annotations.WebController;
import com.yawl.beans.ApplicationContext;
import com.yawl.exception.DuplicateRouteException;
import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RouteRegistryTest {

    private RouteRegistry routeRegistry;
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        routeRegistry = new RouteRegistry();
        applicationContext = new ApplicationContext();
    }

    @AfterEach
    void tearDown() {
        applicationContext.clear();
    }

    @Test
    void init_registersRoutesFromOneController() {
        registerController(new AllRouteController());

        routeRegistry.init(applicationContext);

        assertThat(routeRegistry.find("GET", "/api/all")).isPresent();
        assertThat(routeRegistry.find("POST", "/api/create")).isPresent();
        assertThat(routeRegistry.find("PUT", "/api/items/2")).isPresent();
        assertThat(routeRegistry.find("DELETE", "/api/items/2")).isPresent();
        assertThat(routeRegistry.getRoutes()).hasSize(4);
    }

    @Test
    void find_returnsEmptyForUnknownPath() {
        registerController(new AllRouteController());

        routeRegistry.init(applicationContext);

        assertThat(routeRegistry.find("GET", "/unknown")).isEmpty();
    }

    @Test
    void find_returnsEmptyForWrongMethod() {
        registerController(new AllRouteController());

        routeRegistry.init(applicationContext);

        assertThat(routeRegistry.find("POST", "/all")).isEmpty();
    }

    @Test
    void find_matchesRouteWithPathParam() {
        registerController(new AllRouteController());

        routeRegistry.init(applicationContext);

        var result = routeRegistry.find("PUT", "/api/items/42");
        assertThat(result).isPresent();
    }

    @Test
    void init_throwsOnDuplicateRoute() {
        registerController(new DuplicateRoutesController());

        assertThatThrownBy(() -> routeRegistry.init(applicationContext))
                .isInstanceOf(DuplicateRouteException.class);
    }

    @Test
    void init_throwsOnDuplicateRoute_differentControllers() {
        registerController(new DuplicateGetRouteController());
        registerController(new AllRouteController());

        assertThatThrownBy(() -> routeRegistry.init(applicationContext))
                .isInstanceOf(DuplicateRouteException.class);
    }

    @Test
    void getRoutes_returnsAllRegisteredRoutes() {
        registerController(new AllRouteController());
        registerController(new TestCustomResponseController());

        routeRegistry.init(applicationContext);

        assertThat(routeRegistry.getRoutes()).hasSize(5);
    }

    @Test
    void init_respectsCustomProducesAndStatus() {
        registerController(new TestCustomResponseController());

        routeRegistry.init(applicationContext);

        var result = routeRegistry.find("POST", "/custom/create");
        assertThat(result).isPresent();
        assertThat(result.get().responseInfo().contentType()).isEqualTo(ContentType.of("text/plain"));
        assertThat(result.get().responseInfo().status()).isEqualTo(HttpStatus.ACCEPTED);
    }

    private void registerController(Object controller) {
        applicationContext.register(controller.getClass().getSimpleName(), controller);
    }

    @WebController(path = "/api")
    static class AllRouteController {
        @GetMapping(path = "/all")
        public String getAll() {
            return "all";
        }

        @PostMapping(path = "/create")
        public String create() {
            return "created";
        }

        @DeleteMapping(path = "/items/{id}")
        public String deleteItem() {
            return "deleted";
        }

        @PutMapping(path = "/items/{id}")
        public String updateItem() {
            return "updated";
        }
    }

    @WebController(path = "/dup")
    static class DuplicateRoutesController {
        @GetMapping(path = "/same")
        public String first() {
            return "first";
        }

        @GetMapping(path = "/same")
        public String second() {
            return "second";
        }
    }

    @WebController(path = "/api")
    static class DuplicateGetRouteController {
        @GetMapping(path = "/all")
        public String getAll() {
            return "all";
        }
    }

    @WebController(path = "/custom")
    static class TestCustomResponseController {
        @PostMapping(path = "/create", produces = "text/plain", status = HttpStatus.ACCEPTED)
        public String create() {
            return "created";
        }
    }
}
