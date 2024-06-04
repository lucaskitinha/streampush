package br.com.streampush.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodio(
		@JsonAlias("Title") String titulo,
		@JsonAlias("Episode") Integer numeroEpisodio,
		@JsonAlias("imdRating") String avaliacao,
		@JsonAlias("Released") String dataLancamento
) {
}