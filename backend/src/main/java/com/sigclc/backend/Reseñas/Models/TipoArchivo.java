package com.sigclc.backend.Reseñas.Models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum que define los tipos de archivos permitidos en una reseña.
 * @JsonValue hace que Jackson serialice el enum como la extensión (".pdf", ".png"...)
 */
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

    // Método auxiliar para convertir texto a enum (no es usado para persistir; es auxiliar)
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
