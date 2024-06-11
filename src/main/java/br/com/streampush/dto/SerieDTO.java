package br.com.streampush.dto;

import br.com.streampush.model.Categoria;
import br.com.streampush.model.Episodio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

public record SerieDTO(Long id,
		String titulo,
		Integer totalTemporadas,
		Double avaliacao,
		Categoria genero,
		String atores,
		String poster,
		String sinopse) {
}
