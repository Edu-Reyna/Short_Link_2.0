package com.example.demo.controller;

import com.example.demo.controller.dto.LinkDTO;
import com.example.demo.services.interfaces.ILinkService;
import com.example.demo.services.interfaces.IMetricsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
public class LinkController {

    private final ILinkService linkService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LinkDTO>> getLinks(){
        return new ResponseEntity<>(this.linkService.getLinks(), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<LinkDTO>> getLinksByUser(@PathVariable Long userId) {
        return new ResponseEntity<>(this.linkService.getLinksByUser(userId), HttpStatus.OK);
    }

    @PostMapping("/shorten")
    public ResponseEntity<LinkDTO> shortenLink(@RequestBody LinkDTO linkDTO) {
        return new ResponseEntity<>(this.linkService.shortenLink(linkDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LinkDTO> updateLink(@PathVariable Long id, @RequestBody LinkDTO linkDTO) {
        return new ResponseEntity<>(this.linkService.updateLink(id, linkDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteLink(@PathVariable Long id) {
        return new ResponseEntity<>(this.linkService.deleteLink(id), HttpStatus.OK);
    }

    @GetMapping("/{url}")
    public ResponseEntity<Void> redirect(@PathVariable String url,
                                         HttpServletRequest request) {

        String link = this.linkService.getLink(url, request);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(link)).build();
    }

}
