package my.spring.sample.mvc.component;

import lombok.extern.slf4j.Slf4j;
import my.spring.sample.mvc.model.MsgModel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class RabbitMQMsgSender {

    @Value("${resource.rabbitmq.exchange}")
    private String exchange;

    @Value("${resource.rabbitmq.routingKey}")
    private String routingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${render.server.token}")
    private String renderServerToken;

    private static String ENV = null;

    @PostConstruct
    private void init() {
        ENV = System.getProperty("spring.profiles.active");
        if(ENV == null) {
            ENV = "dev";
        }
    }

    @Async("threadPoolTaskExecutor")
    public void sendMsgForRender(String sceneId, String projectId) {
        MsgModel msg = new MsgModel();
        msg.setEnv(ENV);
        msg.setParam1(sceneId);
        msg.setParam2(projectId);

        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
    }
}
