package com.example.demo.services.interfaces;

import com.example.demo.controller.dto.LinkDTO;
import com.example.demo.entities.LinkEntity;

import java.util.List;

public interface ICacheLinkService {

    List<LinkDTO> getLinks();
    List<LinkDTO> getLinksByUser(Long userId);
    void save(LinkEntity linkEntity);
    void saveAll(List<LinkDTO> linkDTOS);
    LinkDTO getLink(String url);
    LinkDTO update(Long id, LinkDTO linkDTO);
    void delete(Long id);
}
