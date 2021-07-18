package my.spring.sample.mvc.collection;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "devices")
public class Device {



}
