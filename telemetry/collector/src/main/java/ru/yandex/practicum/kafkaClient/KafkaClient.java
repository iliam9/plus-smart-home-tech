package ru.yandex.practicum.kafkaClient;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

public interface KafkaClient {

    // возвращает настроенный экземпляр продюсера
    Producer<String, SpecificRecordBase> getProducer();

    // завершает работу продюсера
    void stop();
}
