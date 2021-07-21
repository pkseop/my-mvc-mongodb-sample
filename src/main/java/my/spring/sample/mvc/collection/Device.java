package my.spring.sample.mvc.collection;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import my.spring.sample.mvc.enums.OsType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "devices")
public class Device {

    @Id
    private String id;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String osType;

    @Indexed
    private String fcmToken;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    public OsType getOsType() {
        return OsType.get(this.osType);
    }
}
