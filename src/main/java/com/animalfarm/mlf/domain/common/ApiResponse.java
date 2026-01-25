package com.animalfarm.mlf.domain.common;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private String message;
    private T payload;
}