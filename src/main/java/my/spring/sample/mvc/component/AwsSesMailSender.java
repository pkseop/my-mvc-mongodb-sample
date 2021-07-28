package my.spring.sample.mvc.component;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Slf4j
@Component
public class AwsSesMailSender {

    @Autowired
    private AmazonSimpleEmailService awsSes;

    @Async("threadPoolTaskExecutor")
    public void sendMail(String to, String subject, String templateName, Map<String, String> data) throws IOException {
        try {
            String content = readTemplate(templateName);
            if (templateName.equals("default-form") == false) {
                String defaultForm = readTemplate("default-form");
                content = defaultForm.replaceAll("\\{content\\}", content);
            }
            for (String key : data.keySet()) {
                content = content.replaceAll("\\{" + key + "\\}", data.get(key));
            }
            subject = subject != null ? subject : "email.com";
            content = content.replaceAll("\\{subject\\}", subject);

            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(to) // 받는 사람
                    )
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(content))) // HTML 양식의 본문
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(subject)))     // 제목
                    .withSource("no-reply@email.com")
                    .withReplyToAddresses("no-reply@email.com");  // Verify된 Email

            awsSes.sendEmail(request);
        } catch (Exception e) {
            log.error("Failed to send e-mail", e);
        }
    }

    private String readTemplate(String templateName) throws IOException {
        String template = "/mail/templates/" + templateName + ".txt";
        Resource resource = new ClassPathResource(template);

        String content = null;
        try (InputStream is = resource.getInputStream();
             Reader reader = new InputStreamReader(is);
        ){
            content = CharStreams.toString(reader);
        }
        return content;
    }
}
