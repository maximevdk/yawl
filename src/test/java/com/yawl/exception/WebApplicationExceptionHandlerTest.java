package com.yawl.exception;

import com.yawl.http.model.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WebApplicationExceptionHandlerTest {
    private final WebApplicationExceptionHandler handler = new WebApplicationExceptionHandler(List.<ExceptionResolver>of(
            new RouteNotFoundExceptionResolver(), new BadRequestExceptionResolver()
    ));

    @Test
    void handle_supportedException_returnsSpecificResponse() {
        var response = handler.handle(MissingRequiredParameterException.of("test"));

        assertThat(response).isNotNull().isEqualTo(HttpResponse.badRequest("Required parameter 'test' not found"));
    }

    @Test
    void handle_noSupportedException_returnsGenericResponse() {
        var response = handler.handle(new Throwable("boom"));

        assertThat(response).isNotNull().isEqualTo(HttpResponse.internal("Something went wrong"));
    }
}