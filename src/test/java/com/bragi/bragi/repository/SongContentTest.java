package com.bragi.bragi.repository;

import com.bragi.bragi.model.Song;
import com.bragi.bragi.model.SongContent;
import com.bragi.bragi.service.config.RetryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SongContentTest {


    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private RetryConfig retryConfig;

    private DataAccessService dataAccessService;

    @BeforeEach
    void init(){
        when(retryConfig.getMaxAttempts()).thenReturn(3);
        when(retryConfig.getMinWaitBetweenMillis()).thenReturn(100L);
        when(retryConfig.getMaxWaitBetweenMillis()).thenReturn(200L);

        dataAccessService = new DataAccessService(retryConfig, albumRepository, artistRepository, songRepository);

    }

    @Test
    void when_getSongContentByExternalId_thenSuccess(){
        when(songRepository.findByExternalId(any())).thenReturn(Optional.of(Song.builder()
                .songContent(SongContent.builder()
                        .id(1L)
                        .content(new byte[]{1, 2, 3})
                .build()).build()));

        var songContent= dataAccessService.getSongContentByExternalId(UUID.randomUUID());

        verify(songRepository).findByExternalId(any());

        assertEquals(1L, songContent.getId());


    }

    @Test
    void when_getSongContentByExternalId_retries_thenSuccess(){
        when(songRepository.findByExternalId(any()))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.of(Song.builder()
                .songContent(SongContent.builder()
                        .id(1L)
                        .content(new byte[]{1, 2, 3})
                        .build()).build()));

        var songContent= dataAccessService.getSongContentByExternalId(UUID.randomUUID());

        verify(songRepository, times(2)).findByExternalId(any());
        assertEquals(1L, songContent.getId());

    }

    @Test
    void when_getSongContentByExternalId_fails_thenThrows(){
        when(songRepository.findByExternalId(any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, ()->{
            var songContent= dataAccessService.getSongContentByExternalId(UUID.randomUUID());

        });


        verify(songRepository, times(3)).findByExternalId(any());
    }

    @Test
    void when_getSongContentFromSong_thenSuccess(){
        var songContent = dataAccessService.getSongContentFromSong(Song.builder()
                        .songContent(SongContent.builder()
                                .content(new byte[]{1, 2, 3})
                                .id(1L)
                                .build())
                .build());

        assertEquals(1L, songContent.getId());

    }

    
}
