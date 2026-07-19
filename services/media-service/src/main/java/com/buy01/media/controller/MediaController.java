package com.buy01.media.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.buy01.media.dto.ApiMessageResponse;
import com.buy01.media.dto.MediaResponse;
import com.buy01.media.service.MediaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media")
public class MediaController {

        private final MediaService mediaService;

        @PostMapping(value = "/products/{productId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<ApiMessageResponse> upload(
                        @PathVariable String productId,
                        @RequestParam("images") List<MultipartFile> images,
                        Principal principal) {

                mediaService.upload(images, productId, principal.getName());

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(new ApiMessageResponse("Media uploaded successfully"));
        }

        @GetMapping("/{mediaId}")
        public ResponseEntity<Resource> getImage(
                        @PathVariable String mediaId) {
                                System.out.println("mediaId: " + mediaId);
                
                Resource resource = mediaService.find(mediaId);

                MediaType mediaType = MediaTypeFactory
                                .getMediaType(resource)
                                .orElse(MediaType.APPLICATION_OCTET_STREAM);

                return ResponseEntity.ok()
                                .contentType(mediaType)
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                ContentDisposition.inline().build().toString())
                                .body(resource);
        }

        @GetMapping("/primary/product/{productId}")
        public ResponseEntity<Resource> getPrimaryImage(
                        @PathVariable String productId) {
                Resource resource = mediaService.findPrimaryImage(productId);

                MediaType mediaType = MediaTypeFactory
                                .getMediaType(resource)
                                .orElse(MediaType.APPLICATION_OCTET_STREAM);

                return ResponseEntity.ok()
                                .contentType(mediaType)
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                ContentDisposition.inline().build().toString())
                                .body(resource);
        }

        @GetMapping("/product/{productId}")
        public ResponseEntity<List<MediaResponse>> getProductMedia(
                        @PathVariable String productId) {
                return ResponseEntity.ok(
                                mediaService.findAllMediaByProductId(productId));
        }

        @PutMapping(value = "/product/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<ApiMessageResponse> updateMedia(
                        @PathVariable String productId,
                        @RequestParam("images") List<MultipartFile> images,
                        Principal principal) {
                mediaService.replaceMedia(images, productId, principal.getName());

                return ResponseEntity.ok(
                                new ApiMessageResponse("Media updated successfully"));
        }

        @DeleteMapping("/product/{productId}")
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<ApiMessageResponse> deleteMedia(
                        @PathVariable String productId,
                        Principal principal) {
                mediaService.deleteAllByProductId(productId, principal.getName());

                return ResponseEntity.ok(
                                new ApiMessageResponse("Media deleted successfully"));
        }
}