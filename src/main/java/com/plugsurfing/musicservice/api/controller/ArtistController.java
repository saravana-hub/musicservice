package com.plugsurfing.musicservice.api.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import com.plugsurfing.musicservice.api.exception.ServiceError;
import com.plugsurfing.musicservice.api.model.artist.ArtistDetails;
import com.plugsurfing.musicservice.api.services.ArtistService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/musify")
public class ArtistController {
    @Autowired
    private ArtistService artistService;

    @Operation(
            summary = "Generate Artist details",
            description =
                    "Generate Artist details for the given MusicBrainz Identifier")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Generated Artist details successfully",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = ArtistDetails.class))
                            }),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = ServiceError.class))
                            })
            })
    @GetMapping(path = "/music-artist/details/{mbid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ArtistDetails> getArtistDetailsByMBId(@PathVariable String mbid) {
        return artistService.getArtistDetails(mbid);
    }

    @ExceptionHandler(value = WebClientResponseException.NotFound.class)
    @ResponseStatus(NOT_FOUND)
    public ServiceError handleException(WebClientResponseException.NotFound e) {
        return new ServiceError(NOT_FOUND, "mbid does not exist");
    }

    @ExceptionHandler(value = WebClientResponseException.BadRequest.class)
    @ResponseStatus(BAD_REQUEST)
    public ServiceError handleException(WebClientResponseException.BadRequest e) {
        return new ServiceError(BAD_REQUEST, BAD_REQUEST.toString());
    }

    @ExceptionHandler({WebClientResponseException.ServiceUnavailable.class, TimeoutException.class,
            RequestNotPermitted.class, CallNotPermittedException.class})
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public ServiceError handleException(WebClientResponseException e) {
        return new ServiceError(SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE.toString());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ServiceError handleException(Exception e) {
        e.printStackTrace();
        return new ServiceError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.toString());
    }
}
