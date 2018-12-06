package io.costa.hibernatetunning;

import io.costa.hibernatetunning.entities.Attachment;
import io.costa.hibernatetunning.entities.Message;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

public class CertificationInsertITest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void createAraCertificate() {

        provider.beginTransaction();

        Message message = Message.of("BF", "JC", "Certeficare", "certificate");
        provider.em().persist(message);

        Attachment attachment = Attachment.of(null, message, "Ara Cc Cerificates", "ara certificate", FileContentReader.readAllBytes("certificate-pkcs8.der"));



        message.addAttachment(attachment);



        provider.commitTransaction();
    }
}
