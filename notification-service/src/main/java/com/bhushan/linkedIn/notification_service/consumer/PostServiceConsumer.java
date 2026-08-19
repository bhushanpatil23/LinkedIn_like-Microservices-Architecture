package com.bhushan.linkedin.notification_service.consumer;

import com.bhushan.linkedin.notification_service.clients.ConnectionsClient;
import com.bhushan.linkedin.notification_service.dto.PersonDto;
import com.bhushan.linkedin.notification_service.entity.Notification;
import com.bhushan.linkedin.notification_service.repository.NotificationRepository;
import com.bhushan.linkedin.notification_service.service.SendNotification;
import com.bhushan.linkedIn.posts_service.event.PostLikedEvent;
import com.bhushan.linkedin.posts_service.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceConsumer {

    private final ConnectionsClient connectionsClient;
    private final SendNotification sendNotification;

    @KafkaListener(topics = "post-created-topic")
    public void handlePostCreated(PostCreatedEvent postCreatedEvent){
        log.info("sending notifications: handlePostCreated {}",postCreatedEvent);

        List<PersonDto> connections = connectionsClient.getFirstConnections(postCreatedEvent.getCreatorId());

        for(PersonDto connection : connections){
            sendNotification.send(connection.getUserId(),"Your connection "+postCreatedEvent.getCreatorId()+" has created a post," +
                    " check it out");
        }
    }

    @KafkaListener(topics = "post-liked-topic")
    public void handlePostLiked(PostLikedEvent postLikedEvent){
        log.info("sending notifications: handlePostLiked {}",postLikedEvent);
        String message = String.format("Your post, %d has been liked by %d", postLikedEvent.getPostId(),
                postLikedEvent.getLikedByUserId());

        sendNotification.send(postLikedEvent.getCreatorId(), message);
    }


}
