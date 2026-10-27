package com.pedidos670.ms_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateQueueRequest {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    @Size(
            max = 255,
            message = "El nombre de la cola no puede superar 255 caracteres"
    )
    private String name;

    private boolean durable = true;

    private boolean exclusive = false;

    private boolean autoDelete = false;
}