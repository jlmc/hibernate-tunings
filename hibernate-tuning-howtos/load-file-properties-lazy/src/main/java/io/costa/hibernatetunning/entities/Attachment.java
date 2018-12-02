package io.costa.hibernatetunning.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "communication", name = "attachment")
public class Attachment {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "message_id", updatable = false, nullable = false)
    private Message message;

    @Column(name = "file_name")
    private String fileName;

    //@LazyGroup("B")
    @Basic(fetch = FetchType.LAZY)
    private String description;

    private int size = 0;


    //@LazyGroup("A")
    //@Lob
    //@Type(type="org.hibernate.type.BinaryType")
    @Basic(fetch = FetchType.LAZY)
    private byte[] file;

    protected Attachment() {
    }

    private Attachment(final Message message, final String fileName, final String description, final byte[] file) {
        this.message = message;
        this.fileName = fileName;
        this.description = description;
        this.file = file;
        this.size = this.file != null ? file.length : 0;
    }

    public static Attachment of(final UUID id, final Message message, final String fileName, final String description, final byte[] file) {
        final Attachment of = of(message, fileName, description, file);
        of.id = id;
        return of;
    }

    public static Attachment of(final Message message, final String fileName, final String description, final byte[] file) {
        return new Attachment(message, fileName, description, file);
    }

    public void setMessage(final Message message) {
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDescription() {
        return description;
    }

    public int getSize() {
        return size;
    }

    public byte[] getFile() {
        return file;
    }
}
