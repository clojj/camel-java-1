package de.jwin;

import de.saly.javamail.mock2.MailboxFolder;
import de.saly.javamail.mock2.MockMailbox;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailTest extends CamelTestSupport {

    Logger logger = LoggerFactory.getLogger(MailTest.class);

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    protected Session session = null;

    private Properties getProperties() {

        final Properties props = new Properties();
        props.setProperty("mail.imaps.host", "localhost");
        props.setProperty("mail.imaps.port", "993");
        props.setProperty("mail.store.protocol", "imaps");
        return props;
    }

    public void prepare() throws Exception {

        final Properties props = getProperties();
        if (logger.isDebugEnabled()) {
            props.setProperty("mail.debug", "true");
        }
        session = Session.getInstance(props);
        MockMailbox.resetAll();
    }

    @Test
    public void testSendAndReceiveMail() throws Exception {
        resultEndpoint.expectedMessageCount(1);

        final MockMailbox mb = MockMailbox.get("hendrik@unknown.com");
        final MailboxFolder mf = mb.getInbox();

        final MimeMessage msg = new MimeMessage(session);
        msg.setSubject("Test");
        msg.setFrom(new InternetAddress("from@sender.com"));
        msg.setText("Some text here ...");
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress("hendrik@unknown.com"));
        mf.add(msg); // 11
        mf.add(msg); // 12
        mf.add(msg); // 13

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() throws Exception {
                prepare();
                from("imaps://localhost:993?username=hendrik@unknown.com&unseen=false")
                        .log("${headers}")
                        .log("${body}")
                        .to("mock:result");
            }
        };
    }

}
