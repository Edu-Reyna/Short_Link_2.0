package com.example.demo.services.implementation;

import com.example.demo.entities.LinkEntity;
import com.example.demo.repositories.ILinkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskSchedule {

    private final ILinkRepository linkRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void changeStatusLinkExpired() {

        OffsetDateTime now = OffsetDateTime.now();

        List<LinkEntity> linkList = linkRepository.findLinkEntitiesByStatusAndExpiresAtBefore(true, now).stream()
                .peek(link -> link.setStatus(false)).toList();

        linkRepository.saveAll(linkList);

    }
}
