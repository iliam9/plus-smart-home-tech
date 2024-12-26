package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.processor.HubEventProcessor;
import ru.yandex.practicum.processor.SnapshotProcessor;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Analyzer {
    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(Analyzer.class, args);




        SnapshotProcessor snapshotProcessor =
                context.getBean(SnapshotProcessor.class);

        final HubEventProcessor hubEventProcessor =
                context.getBean(HubEventProcessor.class);

        Runtime.getRuntime().addShutdownHook(new Thread(snapshotProcessor::stop));
        Runtime.getRuntime().addShutdownHook(new Thread(hubEventProcessor::stop));


        Thread hubEventsThread = new Thread(hubEventProcessor);
        hubEventsThread.setName("HubEventHandlerThread");
        hubEventsThread.start();


        snapshotProcessor.start();
    }
}