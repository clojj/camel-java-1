package de.jwin;
/*

import de.saly.javamail.mock2.MailboxFolder;
import de.saly.javamail.mock2.MockMailbox;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MailTest extends CamelTestSupport {

    Logger logger = LoggerFactory.getLogger(MailTest.class);

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    protected Session session = null;

    private Properties getProperties() {
        final Properties props = new Properties();
        return props;
    }

    public void prepare() throws Exception {

*/
/*
        final Properties props = getProperties();
        if (logger.isDebugEnabled()) {
            props.setProperty("mail.debug", "true");
        }
        session = Session.getInstance(props);
*//*

        MockMailbox.resetAll();

        final MockMailbox mb = MockMailbox.get("hendrik@unknown.com");
        final MailboxFolder mf = mb.getInbox();

        mf.add(createMessage());
        mf.add(createMessage());
    }

    @Test
    public void testSendAndReceiveMail() throws Exception {
        resultEndpoint.expectedMinimumMessageCount(2);


        resultEndpoint.assertIsSatisfied();
    }

    private MimeMessage createMessage() throws MessagingException {
        final MimeMessage msg = new MimeMessage((Session) null);
        msg.setSubject("Test");
        msg.setFrom(new InternetAddress("from@sender.com"));
        msg.setText("Some text here ...");
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress("hendrik@unknown.com"));
        msg.setFlag(Flags.Flag.SEEN, false);
        return msg;
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() throws Exception {
                prepare();
                from("imaps://localhost:993?username=hendrik@unknown.com&delete=true&unseen=true")
                        .log("\nHEADERS ${headers}")
                        .log("\nBODY ${body}")
                        .to("mock:result");
            }
        };
    }

}
*/
