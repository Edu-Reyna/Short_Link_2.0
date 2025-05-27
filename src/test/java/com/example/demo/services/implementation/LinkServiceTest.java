package com.example.demo.services.implementation;

import com.example.demo.DataProvider;
import com.example.demo.controller.dto.LinkDTO;
import com.example.demo.entities.LinkEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.ILinkRepository;
import com.example.demo.services.interfaces.IMetricsService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private ILinkRepository linkRepository;

    @Mock
    private CacheLinkImpl cacheLinkImpl;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ShortService shortService;

    @Mock
    private IMetricsService metricsService;

    @InjectMocks
    private LinkService linkService;

    @Test
    void getLinksTest() {

        //When
        List<LinkEntity> linksE = DataProvider.links();
        when(cacheLinkImpl.getLinks()).thenReturn(List.of());
        when(linkRepository.findAll()).thenReturn(linksE);

        linksE.forEach(linkEntity -> {
            when(modelMapper.map(linkEntity, LinkDTO.class))
                    .thenReturn(LinkDTO.builder()
                            .shortUrl(linkEntity.getShortUrl())
                            .url(linkEntity.getUrl())
                            .build());
        });

        List<LinkDTO> links = linkService.getLinks();

        //Then
        assertNotNull(links);
        assertFalse(links.isEmpty());
        assertEquals(2, links.size());
    }

    @Test
    void getLinksByUserTest() {
        //Given
        List<LinkEntity> linksE = DataProvider.links();
        when(cacheLinkImpl.getLinksByUser(1L)).thenReturn(List.of());
        when(linkRepository.findAllByUserIdAndStatus(1L, true)).thenReturn(linksE);

        linksE.forEach(linkEntity -> {
            when(modelMapper.map(linkEntity, LinkDTO.class))
                    .thenReturn(LinkDTO.builder()
                            .shortUrl(linkEntity.getShortUrl())
                            .url(linkEntity.getUrl())
                            .build());
        });

        //When
        List<LinkDTO> links = linkService.getLinksByUser(1L);

        //Then
        assertNotNull(links);
        assertFalse(links.isEmpty());
        assertEquals(2, links.size());
    }

    @Test
    void shortenLinkTest() {
        //Given
        LinkDTO linkDTO = DataProvider.linkDTO();
        LinkEntity mockLinkEntity = LinkEntity.builder()
                .url(linkDTO.getUrl())
                .user(this.modelMapper.map(linkDTO.getUser(), UserEntity.class))
                .build();
        ;

        when(this.modelMapper.map(linkDTO, LinkEntity.class)).thenReturn(mockLinkEntity);
        when(this.linkRepository.save(any(LinkEntity.class))).thenReturn(mockLinkEntity);
        when(this.shortService.generateShortUrl(8)).thenReturn("abc123");
        when(this.linkRepository.existsByShortUrl("abc123")).thenReturn(false);

        //When
        this.linkService.shortenLink(linkDTO);

        //Then
        ArgumentCaptor<LinkEntity> captor = ArgumentCaptor.forClass(LinkEntity.class);
        verify(this.linkRepository).save(captor.capture());

        assertEquals(linkDTO.getUrl(), captor.getValue().getUrl());
    }

    @Test
    void getLinkTest() {
        //Given
        Optional<LinkEntity> linkE = Optional.ofNullable(DataProvider.links().getFirst());
        MockHttpServletRequest request = DataProvider.request();

        when(linkRepository.findByShortUrl(linkE.get().getShortUrl())).thenReturn(linkE);
        //When
        String link = linkService.getLink(linkE.get().getShortUrl(), request);

        //Then
        assertNotNull(link);
        assertEquals(linkE.get().getUrl(), link);
    }

    @Test
    void updateLinkTest() {
        //Given
        LinkDTO linkDTO = DataProvider.linkDTO();
        LinkEntity linkEntity = DataProvider.links().getFirst();

        when(linkRepository.findById(linkEntity.getId())).thenReturn(Optional.of(linkEntity));
        when(modelMapper.map(linkEntity, LinkDTO.class)).thenReturn(linkDTO);
        when(linkRepository.save(any(LinkEntity.class))).thenReturn(linkEntity);

        //When
        LinkDTO updatedLink = linkService.updateLink(linkEntity.getId(), linkDTO);

        //Then
        assertNotNull(updatedLink);
        assertEquals(linkDTO.getUrl(), updatedLink.getUrl());
    }

    @Test
    void deleteLinkTest() {
        //Given
        LinkEntity linkEntity = DataProvider.links().getFirst();
        when(linkRepository.findById(linkEntity.getId())).thenReturn(Optional.of(linkEntity));
        when(linkRepository.save(any(LinkEntity.class))).thenReturn(linkEntity);

        //When
        String response = linkService.deleteLink(linkEntity.getId());

        //Then
        assertNotNull(response);
        assertEquals("El enlace ha sido eliminado", response);
    }
}