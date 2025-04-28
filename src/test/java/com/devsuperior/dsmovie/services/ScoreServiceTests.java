package com.devsuperior.dsmovie.services;

import static org.hamcrest.CoreMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

	@InjectMocks
	private ScoreService service;

	@Mock
	private UserService userService;

	@Mock
	private ScoreRepository scoreRepository;
	@Mock
	private MovieRepository movieRepository;

	private ScoreEntity score;
	private MovieEntity movie;
	private long existingMovie, nonExistingMovie;
	private UserEntity user;

	@BeforeEach
	void SetUpp() {
		movie = MovieFactory.createMovieEntity();
		existingMovie = 1L;
		nonExistingMovie = 2L;
		user = UserFactory.createUserEntity();

		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(existingMovie)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingMovie)).thenReturn(Optional.empty());
		Mockito.when(scoreRepository.saveAndFlush(score)).thenReturn(score);
		Mockito.when(movieRepository.save(Mockito.any())).thenReturn(movie);

	}

	@Test
	public void saveScoreShouldReturnMovieDTO() {
		ScoreDTO scoreDTO = new ScoreDTO(existingMovie, 4.5);

		MovieDTO result = service.saveScore(scoreDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(movie.getScore(), result.getScore());
	}

	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		ScoreDTO scoreDTO = new ScoreDTO(nonExistingMovie, 4.5);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});
	}
}
