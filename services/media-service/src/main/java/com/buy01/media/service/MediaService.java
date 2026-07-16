package com.buy01.media.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.buy01.media.client.ProductServiceClient;
import com.buy01.media.dto.MediaResponse;
import com.buy01.media.model.Media;
import com.buy01.media.repository.MediaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaService {

    private static final Path UPLOAD_DIR = Paths.get("uploads")
            .toAbsolutePath()
            .normalize();

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final int MAX_IMAGES_PER_PRODUCT = 5;

    private final MediaRepository mediaRepository;
    private final ProductServiceClient productServiceClient;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/avif");

    public void upload(
            List<MultipartFile> images,
            String productId,
            String userId) {

        checkOwnership(productId, userId);
        validateImages(images);
        saveImages(images, productId);
    }

    public void replaceMedia(
            List<MultipartFile> images,
            String productId,
            String userId) {
        checkOwnership(productId, userId);
        validateImages(images);

        deleteFilesAndRecords(productId);
        saveImages(images, productId);
    }

    public void deleteAllByProductId(
            String productId,
            String userId) {
        checkOwnership(productId, userId);
        deleteFilesAndRecords(productId);
    }

    public Resource find(String mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> notFound("Media not found"));

        Path imagePath = getSafePath(media.getImagePath());

        if (!Files.exists(imagePath)) {
            throw notFound("Image file not found");
        }

        return new FileSystemResource(imagePath);
    }

    public Resource findPrimaryImage(String productId) {
        Media media = mediaRepository.findAllByProductId(productId)
                .stream()
                .findFirst()
                .orElseThrow(() -> notFound(
                        "No media found for product: " + productId));

        return find(media.getId());
    }

    public List<MediaResponse> findAllMediaByProductId(String productId) {
        return mediaRepository.findAllByProductId(productId)
                .stream()
                .map(media -> new MediaResponse(
                        media.getId(),
                        "/api/media/" + media.getId()))
                .toList();
    }

    private void checkOwnership(String productId, String userId) {
        boolean response = productServiceClient.checkOwnership(productId, userId);

        if (!response) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not own this product");
        }
    }

    private void validateImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw badRequest("At least one image is required");
        }

        if (images.size() > MAX_IMAGES_PER_PRODUCT) {
            throw badRequest(
                    "A product can have at most "
                            + MAX_IMAGES_PER_PRODUCT
                            + " images");
        }

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                throw badRequest("Image file is empty");
            }

            if (image.getSize() > MAX_FILE_SIZE) {
                throw badRequest("Image maximum size is 2MB");
            }

            if (!ALLOWED_CONTENT_TYPES.contains(image.getContentType())) {
                throw badRequest(
                        "Only JPG, PNG, WEBP, and AVIF images are allowed");
            }
        }
    }

    private void saveImages(
            List<MultipartFile> images,
            String productId) {
        try {
            Files.createDirectories(UPLOAD_DIR);

            for (MultipartFile image : images) {
                String filename = UUID.randomUUID() + getExtension(image);
                Path targetPath = getSafePath(filename);

                image.transferTo(targetPath);

                mediaRepository.save(
                        Media.builder()
                                .productId(productId)
                                .imagePath(filename)
                                .build());
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not save image");
        }
    }

    private void deleteFilesAndRecords(String productId) {
        List<Media> mediaList = mediaRepository.findAllByProductId(productId);

        try {
            for (Media media : mediaList) {
                Files.deleteIfExists(getSafePath(media.getImagePath()));
            }

            mediaRepository.deleteAllByProductId(productId);
        } catch (IOException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not delete image");
        }
    }

    private String getExtension(MultipartFile image) {
        String filename = image.getOriginalFilename();

        if (filename == null || !filename.contains(".")) {
            throw badRequest("Image filename must include an extension");
        }

        return filename.substring(filename.lastIndexOf("."));
    }

    private Path getSafePath(String filename) {
        if (filename == null || filename.isBlank()) {
            throw badRequest("Invalid image filename");
        }

        Path path = UPLOAD_DIR.resolve(filename).normalize();

        if (!path.startsWith(UPLOAD_DIR)) {
            throw badRequest("Invalid image path");
        }

        return path;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}