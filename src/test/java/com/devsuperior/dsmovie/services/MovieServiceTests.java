package com.devsuperior.dsmovie.services;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {

	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository movieRepository;

	private PageImpl<MovieEntity> page;
	private MovieEntity movie;
	private Long exisitingId, nonExistingId;

	@BeforeEach
	void setUp() {

		exisitingId = 1L;
		nonExistingId = 2L;

		movie = MovieFactory.createMovieEntity();

		page = new PageImpl<>(List.of(movie));

		Mockito.when(movieRepository.searchByTitle(any(), (Pageable) any())).thenReturn(page);
		Mockito.when(movieRepository.findById(exisitingId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());

	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Pageable pageable = PageRequest.of(0, 12);
		Assertions.assertNotNull(service.findAll("marvel", pageable));

	}

	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		
		MovieDTO movieDTO = service.findById(exisitingId);
		
		Assertions.assertNotNull(movieDTO);
		Assertions.assertEquals(movieDTO.getId(), exisitingId);
		Assertions.assertEquals(movieDTO.getTitle(), movie.getTitle());
		
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}

	@Test
	public void insertShouldReturnMovieDTO() {
	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
	}
}
