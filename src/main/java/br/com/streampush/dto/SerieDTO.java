package br.com.streampush.dto;

import br.com.streampush.model.Categoria;

public record SerieDTO(Long id,
		String titulo,
		Integer totalTemporadas,
		Double avaliacao,
		Categoria genero,
		String atores,
		String poster,
		String sinopse) {
}
