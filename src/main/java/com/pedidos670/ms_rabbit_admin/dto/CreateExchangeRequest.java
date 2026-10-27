package com.pedidos670.ms_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateExchangeRequest {

    @NotBlank(message = "El nombre del exchange es obligatorio")
    @Size(
            max = 255,
            message = "El nombre del exchange no puede superar 255 caracteres"
    )
    private String name;

    @NotBlank(message = "El tipo de exchange es obligatorio")
    @Pattern(
            regexp = "(?i)direct|topic|fanout",
            message = "El tipo debe ser direct, topic o fanout"
    )
    private String type;

    private boolean durable = true;

    private boolean autoDelete = false;
}