package com.example.demo.services.implementation;

import com.example.demo.controller.dto.LinkDTO;
import com.example.demo.entities.CacheLinkEntity;
import com.example.demo.entities.LinkEntity;
import com.example.demo.repositories.ICacheLinkRepository;
import com.example.demo.services.interfaces.ICacheLinkService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CacheLinkImpl implements ICacheLinkService {

    private final ICacheLinkRepository cacheLinkRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<LinkDTO> getLinks() {
        Iterable<CacheLinkEntity> cacheLinks = this.cacheLinkRepository.findAll();
        return StreamSupport.stream(cacheLinks.spliterator(), false)
                .map(link -> this.modelMapper.map(link, LinkDTO.class))
                .peek(linkDTO -> linkDTO.setShortUrl("http://localhost:8080/links/" + linkDTO.getShortUrl()))
                .toList();
    }

    @Override
    public List<LinkDTO> getLinksByUser(Long userId) {
        Iterable<CacheLinkEntity> cacheLinks = this.cacheLinkRepository.findCacheLinkEntitiesByUserIdAndStatus(userId, true);

        return StreamSupport.stream(cacheLinks.spliterator(), false)
                .map(link -> this.modelMapper.map(link, LinkDTO.class))
                .peek(linkDTO -> linkDTO.setShortUrl("http://localhost:8080/links/" + linkDTO.getShortUrl()))
                .toList();
    }

    @Override
    public void save(LinkEntity linkEntity) {
        LinkDTO linkDTO = this.modelMapper.map(linkEntity, LinkDTO.class);
        CacheLinkEntity cacheLink = this.modelMapper.map(linkDTO, CacheLinkEntity.class);
        this.cacheLinkRepository.save(cacheLink);
    }

    public void saveAll(List<LinkDTO> links) {
        cacheLinkRepository.saveAll(
                links.stream()
                        .map(link -> this.modelMapper.map(link, CacheLinkEntity.class))
                        .toList()
        );
    }

    @Override
    public LinkDTO getLink(String url) {
        CacheLinkEntity cacheLink = this.cacheLinkRepository.findCacheLinkEntityByShortUrl(url).orElse(null);

        if (cacheLink == null) {
            return null;
        }

        return this.modelMapper.map(cacheLink, LinkDTO.class);
    }

    @Override
    public LinkDTO update(Long id, LinkDTO linkDTO) {
        return null;
    }

    @Override
    public void delete(Long id) {
    }
}
