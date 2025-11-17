package com.sigclc.backend.Reseñas.Models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoArchivo {
    PNG(".png"),
    DOCX(".docx"),
    PDF(".pdf");

    private final String extension;

    TipoArchivo(String extension) {
        this.extension = extension;
    }

    @JsonValue
    public String getExtension() {
        return extension;
    }

    public static TipoArchivo fromString(String valor) {
        if (valor == null) return null;
        valor = valor.trim().toLowerCase();
        if (!valor.startsWith(".")) valor = "." + valor;
        
        for (TipoArchivo t : values()) {
            if (t.getExtension().equals(valor)) return t;
        }
        throw new IllegalArgumentException("Extensión no válida: " + valor);
    }
}