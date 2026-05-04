package com.example.newsletterapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriptionService {

    private final DynamoDbClient dynamoDbClient;
    private final SesClient sesClient;

    @Value("${aws.dynamodb.tableName}")
    private String tableName;

    @Value("${aws.ses.fromAddress}")
    private String fromAddress;

    public SubscriptionService(DynamoDbClient dynamoDbClient, SesClient sesClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.sesClient = sesClient;
    }

    public void subscribe(String email) {
        saveToDynamo(email);
        sendConfirmationEmail(email);
    }

    private void saveToDynamo(String email) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("email", AttributeValue.builder().s(email).build());
        item.put("subscribedAt", AttributeValue.builder().s(Instant.now().toString()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    private void sendConfirmationEmail(String email) {
        Destination destination = Destination.builder()
                .toAddresses(email)
                .build();

        Content subject = Content.builder()
                .data("Thanks for subscribing!")
                .build();

        Content textBody = Content.builder()
                .data("You are now subscribed to our newsletter.")
                .build();

        Body body = Body.builder()
                .text(textBody)
                .build();

        Message message = Message.builder()
                .subject(subject)
                .body(body)
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .source(fromAddress)
                .destination(destination)
                .message(message)
                .build();

        sesClient.sendEmail(request);
    }
}
