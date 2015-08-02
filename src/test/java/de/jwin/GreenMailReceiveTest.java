package de.jwin;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
import java.security.Security;

public class GreenMailReceiveTest extends CamelTestSupport {

    Logger logger = LoggerFactory.getLogger(GreenMailReceiveTest.class);

    private static final String USER_PASSWORD = "abcdef123";
    private static final String USER_NAME = "hascode";
    private static final String EMAIL_USER_ADDRESS = "hascode@localhost";
    private static final String EMAIL_TO = "someone@localhost.com";
    private static final String EMAIL_SUBJECT = "Test E-Mail";
    private static final String EMAIL_TEXT = "This is a test e-mail.";


    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    private GreenMail mailServer;


    private void prepare() throws MessagingException {
        // create user on mail server
        GreenMailUser user = mailServer.setUser(EMAIL_USER_ADDRESS, USER_NAME, USER_PASSWORD);
        // use greenmail to store the message
        user.deliver(createMessage());
        user.deliver(createMessage());
        user.deliver(createMessage());
        user.deliver(createMessage());
        user.deliver(createMessage());
        user.deliver(createMessage());
        user.deliver(createMessage());
        user.deliver(createMessage());
        user.deliver(createMessage());
        user.deliver(createMessage());
    }

    private MimeMessage createMessage() throws MessagingException {
        // create an e-mail message using javax.mail ..
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress(EMAIL_TO));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_USER_ADDRESS));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(EMAIL_TEXT);
        message.setFlag(Flags.Flag.SEEN, false);
        return message;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
        mailServer = new GreenMail(ServerSetupTest.IMAPS);
        mailServer.start();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        mailServer.stop();
    }

    @Test
    public void testReceiveMail() throws Exception {
        prepare();
        resultEndpoint.expectedMessageCount(10);

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws MessagingException {
        return new RouteBuilder() {
            public void configure() throws Exception {

                from("imaps://localhost:" + ServerSetupTest.IMAPS.getPort()
                        + "?username=" + USER_NAME + "&password=" + USER_PASSWORD
                        + "&delete=true&closeFolder=false&searchTerm.unseen=true")
/*
                        .log("\nHEADERS ${headers}")
                        .log("\nBODY ${body}")
*/
                        .to("seda:start");

                from("seda:start").threads(5)
                        .log("\nBODY PROCESSED: ${body}")
                        .to("mock:result");

                from("direct:start")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                Thread.sleep(500);
                            }
                        })
                        .log("\nBODY PROCESSED: ${body}")
                        .to("mock:result");
            }
        };
    }

}
