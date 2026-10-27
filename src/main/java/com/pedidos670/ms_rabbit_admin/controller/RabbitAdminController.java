package com.pedidos670.ms_rabbit_admin.controller;

import com.pedidos670.ms_rabbit_admin.dto.ApiResponse;
import com.pedidos670.ms_rabbit_admin.dto.CreateBindingRequest;
import com.pedidos670.ms_rabbit_admin.dto.CreateExchangeRequest;
import com.pedidos670.ms_rabbit_admin.dto.CreateQueueRequest;
import com.pedidos670.ms_rabbit_admin.service.RabbitAdminService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rabbit-admin")
@RequiredArgsConstructor
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    @PostMapping("/queues")
    public ResponseEntity<ApiResponse> createQueue(
            @Valid
            @RequestBody
            CreateQueueRequest request
    ) {

        rabbitAdminService.createQueue(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse(
                                "Cola creada correctamente"
                        )
                );
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<ApiResponse> deleteQueue(
            @PathVariable
            String name
    ) {

        boolean deleted =
                rabbitAdminService.deleteQueue(name);

        if (!deleted) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new ApiResponse(
                                    "La cola no existe"
                            )
                    );
        }

        return ResponseEntity.ok(
                new ApiResponse(
                        "Cola eliminada correctamente"
                )
        );
    }

    @PostMapping("/exchanges")
    public ResponseEntity<ApiResponse> createExchange(
            @Valid
            @RequestBody
            CreateExchangeRequest request
    ) {

        rabbitAdminService.createExchange(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse(
                                "Exchange creado correctamente"
                        )
                );
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<ApiResponse> deleteExchange(
            @PathVariable
            String name
    ) {

        boolean deleted =
                rabbitAdminService.deleteExchange(name);

        if (!deleted) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new ApiResponse(
                                    "El exchange no existe"
                            )
                    );
        }

        return ResponseEntity.ok(
                new ApiResponse(
                        "Exchange eliminado correctamente"
                )
        );
    }

    @PostMapping("/bindings")
    public ResponseEntity<ApiResponse> createBinding(
            @Valid
            @RequestBody
            CreateBindingRequest request
    ) {

        rabbitAdminService.createBinding(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse(
                                "Binding creado correctamente"
                        )
                );
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<ApiResponse> deleteBinding(
            @Valid
            @RequestBody
            CreateBindingRequest request
    ) {

        rabbitAdminService.deleteBinding(request);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Binding eliminado correctamente"
                )
        );
    }
}