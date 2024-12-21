package liaison.linkit.chat.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import software.amazon.awssdk.utils.ImmutableMap;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class MessageKafka implements Serializable {
    private String id;

    @NotNull
    private Integer chatNo;

    @NotNull
    private String contentType;

    @NotNull
    private String content;

    private String senderName;

    private Integer senderNo;

    @NotNull
    private Integer saleNo;

    private long sendTime;
    private Integer readCount;
    private String senderEmail;

    public void setSendTimeAndSender(LocalDateTime sendTime, Integer senderNo, String senderName, Integer readCount) {
        this.senderName = senderName;
        this.sendTime = sendTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        this.senderNo = senderNo;
        this.readCount = readCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Chatting convertEntity() {
        return Chatting.builder()
                .senderName(senderName)
                .senderNo(senderNo)
                .chatRoomNo(chatNo)
                .contentType(contentType)
                .content(content)
                .sendDate(Instant.ofEpochMilli(sendTime).atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .readCount(readCount)
                .build();
    }

    // Kafka consumer 설정 클래스를 작성합니다.
    @EnableKafka
    @Configuration
    public class ListenerConfiguration {

        // KafkaListener 컨테이너 팩토리를 생성하는 Bean 메서드
        @Bean
        ConcurrentKafkaListenerContainerFactory<String, MessageKafka> kafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, MessageKafka> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());
            return factory;
        }

        // Kafka ConsumerFactory를 생성하는 Bean 메서드
        @Bean
        public ConsumerFactory<String, MessageKafka> consumerFactory() {
            JsonDeserializer<MessageKafka> deserializer = new JsonDeserializer<>();
            // 패키지 신뢰 오류로 인해 모든 패키지를 신뢰하도록 작성
            deserializer.addTrustedPackages("*");

            // Kafka Consumer 구성을 위한 설정값들을 설정 -> 변하지 않는 값이므로 ImmutableMap을 이용하여 설정
            Map<String, Object> consumerConfigurations =
                    ImmutableMap.<String, Object>builder()
                            .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                            .put(ConsumerConfig.GROUP_ID_CONFIG, "adopt")
                            .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                            .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                            .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                            .build();

            return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
        }
    }
}
