package liaison.linkit.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_messages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrawlingInfo {

    @Id
    private String id;
    private String name;
    private Long age;

}
