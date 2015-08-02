package de.jwin;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

public class GreenMailSendTest extends CamelTestSupport {

    Logger logger = LoggerFactory.getLogger(GreenMailSendTest.class);

    private static final String USER_PASSWORD = "abcdef123";
    private static final String USER_NAME = "hascode";
    private static final String EMAIL_REPLY_ADDRESS = "no-reply@abc.com";

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    private GreenMail mailServer;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        mailServer = new GreenMail(ServerSetupTest.SMTP);
        mailServer.start();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        mailServer.stop();
    }

    @Test
    public void testSendAndReceiveMail() throws Exception {
        Map<String, Object> headers = new HashMap() {{
            put("from", EMAIL_REPLY_ADDRESS);
        }};
        template.sendBodyAndHeaders("Test abc...", headers);

        MimeMessage mimeMessage = mailServer.getReceivedMessages()[0];
        assertEquals(EMAIL_REPLY_ADDRESS, mimeMessage.getFrom()[0].toString());
        assertEquals("Test abc...", GreenMailUtil.getBody(mimeMessage));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws MessagingException {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start")
                        .to("smtp://localhost:" + ServerSetupTest.SMTP.getPort()
                                + "?username=" + USER_NAME + "&password=" + USER_PASSWORD);
            }
        };
    }

}
