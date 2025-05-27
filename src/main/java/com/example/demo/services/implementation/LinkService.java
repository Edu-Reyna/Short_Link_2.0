package com.example.demo.services.implementation;

import com.example.demo.controller.dto.LinkDTO;
import com.example.demo.entities.LinkEntity;
import com.example.demo.repositories.ILinkRepository;
import com.example.demo.services.interfaces.ILinkService;
import com.example.demo.services.interfaces.IMetricsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService implements ILinkService {

    private final ILinkRepository linkRepository;
    private final CacheLinkImpl cacheLinkImpl;
    private final IMetricsService metricsService;
    private final ModelMapper modelMapper;
    private final ShortService shortService;

    @Override
    public List<LinkDTO> getLinks() {

        List<LinkDTO> links = this.cacheLinkImpl.getLinks();
        if (!links.isEmpty()) {
            log.info("Found links in redis", links.size());
            return links;
        };

        links = this.linkRepository.findAll()
                .stream()
                .map(link -> modelMapper.map(link, LinkDTO.class))
                .toList();

        if (!links.isEmpty()) {
                this.cacheLinkImpl.saveAll(links);
        }
        log.info("Found links in database", links.size());

        return links.stream()
                .peek(linkDTO -> linkDTO.setShortUrl("http://localhost:8080/links/" + linkDTO.getShortUrl()))
                .toList();
    }

    @Override
    public List<LinkDTO> getLinksByUser(Long userId) {
        List<LinkDTO> links = this.cacheLinkImpl.getLinksByUser(userId);

        if (!links.isEmpty()) {
            log.info("Found links in redis", links.size());
            return links;
        };

        links = this.linkRepository.findAllByUserIdAndStatus(userId, true)
                .stream()
                .map(link -> modelMapper.map(link, LinkDTO.class))
                .toList();

        if (!links.isEmpty()) {
            this.cacheLinkImpl.saveAll(links);
        }
        log.info("Found links in database", links.size());

        return links.stream()
                .peek(linkDTO -> linkDTO.setShortUrl("http://localhost:8080/links/" + linkDTO.getShortUrl()))
                .toList();
    }

    @Override
    public LinkDTO shortenLink(LinkDTO linkDTO) {
        LinkEntity linkEntity = this.modelMapper.map(linkDTO, LinkEntity.class);
        linkEntity.setStatus(true);

        String shortUrl;
        do {
         shortUrl = this.shortService.generateShortUrl(8);
        }while (linkRepository.existsByShortUrl((shortUrl)));

        linkEntity.setShortUrl(shortUrl);

        LinkEntity savedLink = this.linkRepository.save(linkEntity);

        this.cacheLinkImpl.save(savedLink);

        savedLink.setShortUrl("http://localhost:8080/links/" + savedLink.getShortUrl());
        return this.modelMapper.map(savedLink, LinkDTO.class);
    }

    @Override
    public String getLink(String url, HttpServletRequest request) {
        LinkDTO linkDTO = this.cacheLinkImpl.getLink(url);

        if (linkDTO != null) {
            log.info("Found link in redis");
            boolean isLinkValid = linkDTO.getExpiresAt() == null || linkDTO.getExpiresAt().toInstant().isAfter(Instant.now());
            if (linkDTO.isStatus() && isLinkValid) {
               LinkEntity save = this.metricsService.createMetrics(this.modelMapper.map(linkDTO, LinkEntity.class), request);
                this.cacheLinkImpl.save(this.modelMapper.map(save, LinkEntity.class));
                return linkDTO.getUrl();
            }
        }

        LinkEntity link = this.linkRepository.findByShortUrl(url).orElseThrow(() -> new RuntimeException("El enlace no existe"));

        boolean isLinkValid = link.getExpiresAt() == null || link.getExpiresAt().toInstant().isAfter(Instant.now());

        if (link.isStatus() && isLinkValid) {
            LinkEntity save = this.metricsService.createMetrics(link, request);
            this.cacheLinkImpl.save(save);
            return link.getUrl();
        }

        throw new RuntimeException("El enlace no está activo o ha expirado");
    }

    @Override
    public LinkDTO updateLink(Long id, LinkDTO linkDTO) {
        LinkEntity link = this.linkRepository.findById(id).orElseThrow(() -> new RuntimeException("El enlace no existe"));
        link.setTitle(linkDTO.getTitle());
        link.setUrl(linkDTO.getUrl());
        link.setExpiresAt(linkDTO.getExpiresAt());

        LinkEntity updatedLink = this.linkRepository.save(link);
        this.cacheLinkImpl.save(updatedLink);
        return this.modelMapper.map(updatedLink, LinkDTO.class);
    }

    @Override
    public String deleteLink(Long id) {
        LinkEntity link = this.linkRepository.findById(id).orElseThrow(() -> new RuntimeException("El enlace no existe"));
        link.setStatus(false);

        this.linkRepository.save(link);
        this.cacheLinkImpl.save(link);
        return "El enlace ha sido eliminado";
    }
}
