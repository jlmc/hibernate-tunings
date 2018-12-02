package io.costa.hibernatetunning.entities;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "communication", name = "image")
public class Image {

    @Id
    @GeneratedValue//(generator = "uuid")
    //@GenericGenerator(name = "uuid", strategy = "uuid")
    private UUID id;

    @ManyToOne//(optional = false)
    @JoinColumn(name = "message_id", nullable = false, updatable = false)
    private Message message;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    //@Column(name = "file", columnDefinition = "oid")
    public byte[] file;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type = Type.TIFF;

    protected Image() {
    }

    private Image(final Type type, final byte[] file) {
        this.file = file;
        this.type = type;
    }

    private static Image of(final Type type, final byte[] file) {
        return new Image(type, file);
    }

    public static Image of(final UUID id, final Type type, final byte[] file) {
        final Image of = of(type, file);
        of.id = id;
        return of;
    }

    public static Image png(final byte[] file) {
        final Image of = of(Type.PNG, file);
        return of;
    }

    public static Image tiff(final byte[] file) {
        final Image of = of(Type.TIFF, file);
        return of;
    }

    public UUID getId() {
        return id;
    }

    public void setMessage(final Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public byte[] getFile() {
        return file;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        PNG, TIFF
    }

}
