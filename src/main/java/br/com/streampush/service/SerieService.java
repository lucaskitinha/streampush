package br.com.streampush.service;

import br.com.streampush.dto.EpisodioDTO;
import br.com.streampush.dto.SerieDTO;
import br.com.streampush.model.Categoria;
import br.com.streampush.model.Serie;
import br.com.streampush.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

	@Autowired
	private SerieRepository repository;

	public List<SerieDTO> obterTodasAsSeries() {
		return paraDto(repository.findAll());
	}

	public List<SerieDTO> obterTop5Series() {
		return paraDto(repository.findTop5ByOrderByAvaliacaoDesc());
	}

	public List<SerieDTO> obterLancamentos() {
		return paraDto(repository.encontrarEpisodiosMaisRecentes());
	}

	private List<SerieDTO> paraDto(List<Serie> lista) {
		return lista.stream()
				.map(s -> new SerieDTO(s.getId(),s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPoster(),s.getSinopse()))
				.collect(Collectors.toList());
	}

	public SerieDTO obterSeriePorId(Long id) {
		Optional<Serie> serie = repository.findById(id);
		if(serie.isPresent()) {
			Serie s = serie.get();
			return new SerieDTO(s.getId(),s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPoster(),s.getSinopse());
		} else {
			return null;
		}
	}

	public List<EpisodioDTO> obterTodasTemporadas(Long id) {
		Optional<Serie> serie = repository.findById(id);

		if (serie.isPresent()) {
			Serie s = serie.get();
			return s.getEpisodios().stream()
					.map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
					.collect(Collectors.toList());
		}
		return null;
	}

	public List<EpisodioDTO> obterTemporadasPorNumero(Long id, Long numero) {
		return repository.obterEpisodiosPorTemporada(id, numero)
				.stream()
				.map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
				.collect(Collectors.toList());

	}

	public List<SerieDTO> obterSeriesPorCategoria(String nomeGenero) {
		Categoria categoria = Categoria.fromString(nomeGenero);
		return paraDto(repository.findByGenero(categoria));
	}
}
