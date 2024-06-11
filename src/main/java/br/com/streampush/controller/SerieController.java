package br.com.streampush.controller;

import br.com.streampush.dto.EpisodioDTO;
import br.com.streampush.dto.SerieDTO;
import br.com.streampush.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

	@Autowired
	private SerieService service;

	@GetMapping
	public List<SerieDTO> obterSeries() {
		return service.obterTodasAsSeries();
	}

	@GetMapping("/top5")
	public List<SerieDTO> obterTop5Series() {
		return service.obterTop5Series();
	}

	@GetMapping("/lancamentos")
	public List<SerieDTO> obterLancamentos() {
		return service.obterLancamentos();
	}

	@GetMapping("/{id}")
	public SerieDTO obterSeriePorId(@PathVariable Long id) {
		return service.obterSeriePorId(id);
	}

	@GetMapping("/{id}/temporadas/todas")
	public List<EpisodioDTO> obterTodasTemporadas(@PathVariable Long id) {
		return service.obterTodasTemporadas(id);
	}

	@GetMapping("/{id}/temporadas/{numero}")
	public List<EpisodioDTO> obterTemporadasPorNumero(@PathVariable Long id, @PathVariable Long numero){
		return service.obterTemporadasPorNumero(id, numero);
	}

	@GetMapping("/categoria/{nomeGenero}")
	public List<SerieDTO> obterSeriesPorCategoria(@PathVariable String nomeGenero){
		return service.obterSeriesPorCategoria(nomeGenero);
	}
}
