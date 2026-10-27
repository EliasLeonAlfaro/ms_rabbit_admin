package com.pedidos670.ms_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateBindingRequest {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    @Size(
            max = 255,
            message = "El nombre de la cola no puede superar 255 caracteres"
    )
    private String queue;

    @NotBlank(message = "El nombre del exchange es obligatorio")
    @Size(
            max = 255,
            message = "El nombre del exchange no puede superar 255 caracteres"
    )
    private String exchange;

    @Size(
            max = 255,
            message = "La routing key no puede superar 255 caracteres"
    )
    private String routingKey = "";
}