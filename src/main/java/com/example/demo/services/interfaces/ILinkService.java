package com.example.demo.services.interfaces;

import com.example.demo.controller.dto.LinkDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface ILinkService {

    List<LinkDTO> getLinks();
    List<LinkDTO> getLinksByUser(Long userId);
    LinkDTO shortenLink(LinkDTO linkDTO);
    String getLink(String url, HttpServletRequest request);
    LinkDTO updateLink(Long id, LinkDTO linkDTO);
    String deleteLink(Long id);
}
