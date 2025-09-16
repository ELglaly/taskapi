package com.example.taskapi.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Setter
@Getter
@AllArgsConstructor
public class ApiResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String message;
    private Object data;
}
