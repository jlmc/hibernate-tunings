package io.costa.hibernatetunning.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(schema = "communication", name = "message")
public class Message {

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, updatable = false)
    private UUID id;

    private String transmissor;
    private String receptor;
    private String subject;
    private String body;

    @OneToMany(mappedBy = "message", orphanRemoval = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "message", orphanRemoval = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private List<Image> images = new ArrayList<>();

    public Message() {
    }

    private Message(final String transmissor, final String receptor, final String subject, final String body) {
        this.transmissor = transmissor;
        this.receptor = receptor;
        this.subject = subject;
        this.body = body;
    }

    public static Message of(final String transmissor, final String receptor, final String subject, final String body) {
        return new Message(transmissor, receptor, subject, body);
    }

    public List<Attachment> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public List<Image> getImages() {
        return Collections.unmodifiableList(images);
    }

    public void addImage(final Image image) {
        this.images.add(image);
        image.setMessage(this);
    }

    public void addAttachment(final Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setMessage(this);
    }
}
