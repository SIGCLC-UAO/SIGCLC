package com.sigclc.backend.Reuniones.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Vista de archivo en GridFS asociada a una reunión (NO se persiste en la colección).
 * Solo transporta metadata hacia los DTOs/respuestas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReunionArchivoView {
    private ObjectId fileId;     // fs.files._id
    private String filename;     // nombre original (si lo guardas en metadata)
    private String extension;    // ".png", ".ppt", ".pptx", ".docx"
    private String mime;         // "image/png", etc.
    private long length;         // bytes
    private Date uploadDate;     // fecha de subida GridFS
    private String sha256;       // hash (si lo guardas en metadata)
    private ObjectId uploaderId; // usuario que subió (si lo guardas en metadata)
}

