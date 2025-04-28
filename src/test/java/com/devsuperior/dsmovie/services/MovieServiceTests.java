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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {

	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository movieRepository;

	private PageImpl<MovieEntity> page;
	private MovieEntity movie;
	private Long exisitingId, nonExistingId,dependentId;
	private MovieDTO movieDTO;

	@BeforeEach
	void setUp() throws Exception {

		exisitingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;

		movie = MovieFactory.createMovieEntity();
		movieDTO = new MovieDTO(movie);

		page = new PageImpl<>(List.of(movie));

		Mockito.when(movieRepository.searchByTitle(any(), (Pageable) any())).thenReturn(page);

		Mockito.when(movieRepository.findById(exisitingId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());

		Mockito.when(movieRepository.save(any())).thenReturn(movie);

		Mockito.when(movieRepository.getReferenceById(exisitingId)).thenReturn(movie);
		Mockito.when(movieRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(movieRepository.existsById(exisitingId)).thenReturn(true);
		Mockito.when(movieRepository.existsById(dependentId)).thenReturn(true);
		Mockito.when(movieRepository.existsById(nonExistingId)).thenReturn(false);
		
		Mockito.doNothing().when(movieRepository).deleteById(exisitingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(movieRepository).deleteById(dependentId);

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
		MovieDTO result = service.insert(movieDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(movieDTO.getId(), movie.getId());
		Assertions.assertEquals(movieDTO.getTitle(), movie.getTitle());
	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {

		MovieDTO result = service.update(exisitingId, movieDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), exisitingId);
		Assertions.assertEquals(result.getTitle(), movieDTO.getTitle());

	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, movieDTO);
		});
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() ->{
			service.delete(exisitingId);
		});
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
	}
}
