package io.costax.relationships.columntransformer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;

import java.util.UUID;

@Entity
@Table(name = "t_document")
public class Document {

    @Id
    private UUID uuid;
    private String content;

    @ColumnTransformer(
            write = " encrypt('AES', '00', stringtoutf8( ? ) ) ",
            read = " trim(char(0) from utf8tostring(decrypt('AES', '00', signature))) "
    )
    @Column(name = "signature", columnDefinition = "binary(8000)")
    private String signature;

    public Document() {
    }

    public Document(final UUID uuid, final String content, final String signature) {
        this.uuid = uuid;
        this.content = content;
        this.signature = signature;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getContent() {
        return content;
    }

    public String getSignature() {
        return signature;
    }
}
