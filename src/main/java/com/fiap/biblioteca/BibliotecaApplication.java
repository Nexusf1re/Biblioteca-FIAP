package com.fiap.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BibliotecaApplication {

    public static void main(String[] args) {
        // Forca UTC pra nao ter dor de cabeca com fuso no calculo das datas de devolucao
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(BibliotecaApplication.class, args);
    }
}
