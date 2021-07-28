package my.spring.sample.mvc.component;

import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Slf4j
@Component
public class AwsLambdaInvoker {

    @SuppressWarnings("unchecked")
    public String sample() throws JsonProcessingException, UnsupportedEncodingException {
        String env = System.getProperty("spring.profiles.active");
        if (env == null) {
            env = "prod";
        }
        Map<String, Object> map = Map.of(
                "param1", "value1",
                "param2", "value2",
                "param3", "value3"
        );
        ObjectMapper om = new ObjectMapper();
        String payload = om.writeValueAsString(map);

        AWSLambdaAsync client = AWSLambdaAsyncClientBuilder.standard().withRegion("ap-northeast-2").build();
        InvokeRequest invokeRequest = new InvokeRequest();
        invokeRequest.setInvocationType("RequestResponse"); // ENUM RequestResponse or Event
        invokeRequest.withFunctionName("functionName").withPayload(payload).withQualifier(env);
        InvokeResult invoke = client.invoke(invokeRequest);

        // PRINT THE RESPONSE
        String val = new String(invoke.getPayload().array(), "UTF-8");
        log.info("Response==> [{}]", val);
        Map<String, Object> ret = om.readValue(val, Map.class);

        return (String)ret.get("body");
    }
}
