package br.com.streampush.principal;

import br.com.streampush.model.Categoria;
import br.com.streampush.model.DadosSerie;
import br.com.streampush.model.DadosTemporada;
import br.com.streampush.model.Episodio;
import br.com.streampush.model.Serie;
import br.com.streampush.repository.EpisodioRepository;
import br.com.streampush.repository.SerieRepository;
import br.com.streampush.service.ConsumoApi;
import br.com.streampush.service.ConverteDados;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Principal {

	private Scanner leitura = new Scanner(System.in);
	private ConsumoApi consumo = new ConsumoApi();
	private ConverteDados conversor = new ConverteDados();
	private final String ENDERECO = "https://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=94294467";
	private List<DadosSerie> dadosSeries = new ArrayList<>();
	private List<Serie> series = new ArrayList<>();
	private Optional<Serie> serieBusca;

	@Autowired
	private SerieRepository serieRepository;
	@Autowired
	private EpisodioRepository episodioRepository;

	@Transactional
	public void exibeMenu() throws UnsupportedEncodingException {
		var opcao = -1;
		while(opcao != 0){
			var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                4 - Buscar série por titulo
                5 - Buscar séries por ator
                6 - Buscar top 5 séries
                7 - Buscar séries por categoria
                8 - Filtrar séries
                9 - Buscar episódio por trecho
                10 - Buscar top episódios por série
                11 - Buscar episódios a partir de uma data
                
                0 - Sair               
                """;

			System.out.println(menu);
			opcao = leitura.nextInt();
			leitura.nextLine();

			switch (opcao) {
			case 1:
				buscarSerieWeb();
				break;
			case 2:
				buscarEpisodioPorSerie();
				break;
			case 3:
				buscarSeriePorTitulo();
				break;
			case 4:
				listarSeriesBuscadas();
				break;
			case 5:
				buscarSeriesPorAtor();
				break;
			case 6:
				buscarTop5Series();
				break;
			case 7:
				buscarSeriesPorCategoria();
				break;
			case 8:
				filtrarSeriesPorTemporadaEAvaliacao();
				break;
			case 9:
				buscarEpisodioPorTrecho();
				break;
			case 10:
				buscarTopEpisodiosPorSerie();
				break;
			case 11:
				buscarEpisodiosDepoisDeUmaData();
				break;
			case 0:
				System.out.println("Saindo...");
				break;
			default:
				System.out.println("Opção inválida");
			}
		}

	}

	private void listarSeriesBuscadas() {
		series = serieRepository.findAll();

		series.stream()
				.sorted(Comparator.comparing(Serie::getGenero))
				.forEach(System.out::println);
	}

	private void buscarSerieWeb() throws UnsupportedEncodingException {
		DadosSerie dados = getDadosSerie();
		serieRepository.save(new Serie(dados));
		dadosSeries.add(dados);
		System.out.println(dados);
	}

	private DadosSerie getDadosSerie() throws UnsupportedEncodingException {
		System.out.println("Digite o nome da série para busca");
		var nomeSerie = leitura.nextLine();
		var url = ENDERECO+ URLEncoder.encode(nomeSerie, StandardCharsets.UTF_8.toString()) +API_KEY;
		var json = consumo.obterDados(url);
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		return dados;
	}

	private void buscarEpisodioPorSerie() throws UnsupportedEncodingException {
		listarSeriesBuscadas();
		System.out.println("Escolha um episodio por série");
		var nomeSerie = leitura.nextLine();

		var serie = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

		if (serie.isPresent()){
			var serieEncontrada = serie.get();
			List<DadosTemporada> temporadas = new ArrayList<>();

			for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
				var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
				DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
				temporadas.add(dadosTemporada);
			}
			temporadas.forEach(System.out::println);

			var episodios = temporadas.stream()
					.flatMap(d -> d.episodios().stream()
							.map(e -> new Episodio(d.numeroTemporada(),e)))
					.collect(Collectors.toList());
			serieEncontrada.setEpisodios(episodios);
			serieRepository.save(serieEncontrada);
		} else {
			System.out.println("Serie não encontrada");
		}

	}

	private void buscarSeriePorTitulo() {
		System.out.println("Digite o nome da série para busca");
		var nomeSerie = leitura.nextLine();
		serieBusca = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

		if(serieBusca.isPresent()){
			System.out.println("Dados da série: " + serieBusca.get());
		} else {
			System.out.println("Série não encontrada");
		}
	}

	private void buscarSeriesPorAtor() {
		System.out.println("Qual o nome deseja buscar?");
		var nomeAtor = leitura.nextLine();
		System.out.println("Avaliações a partir de que valor? ");
		var avaliacao = leitura.nextDouble();
		List<Serie> series = serieRepository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
		System.out.println("Séries em que " +nomeAtor + " trabalhou:");
		series.forEach(s -> System.out.println(s.getTitulo() + "/ Avaliação: " + s.getAvaliacao()));
	}

	private void buscarTop5Series() {
		List<Serie> series = serieRepository.findTop5ByOrderByAvaliacaoDesc();

		series.forEach(s -> System.out.println(s.getTitulo() + "/ Avaliação: " + s.getAvaliacao()));
	}

	private void buscarSeriesPorCategoria() {
		System.out.println("Deseja buscar séries de que categoria/genêro");
		var genero = leitura.nextLine();
		Categoria categoria = Categoria.fromString(genero);
		List<Serie> series = serieRepository.findByGenero(categoria);
		System.out.println("Séries da categoria: " + genero);
		series.forEach(System.out::println);
	}

	private void filtrarSeriesPorTemporadaEAvaliacao(){
		System.out.println("Filtrar séries até quantas temporadas? ");
		var totalTemporadas = leitura.nextInt();
		leitura.nextLine();
		System.out.println("Com avaliação a partir de que valor? ");
		var avaliacao = leitura.nextDouble();
		leitura.nextLine();
		List<Serie> filtroSeries = serieRepository.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
		System.out.println("*** Séries filtradas ***");
		filtroSeries.forEach(s ->
				System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
	}

	private void buscarEpisodioPorTrecho() {
		System.out.println("Qual o episodio deseja buscar?");
		var trecho = leitura.nextLine();
		List<Episodio> episodios = serieRepository.episodiosPorTrecho(trecho);
	}

	private void buscarTopEpisodiosPorSerie() {
		buscarSeriePorTitulo();

		if(serieBusca.isPresent()) {
			var serie = serieBusca.get();
			List<Episodio> topEpisodios = serieRepository.topEpisodiosPorSerie(serie);
		}
	}

	private void buscarEpisodiosDepoisDeUmaData() {
		buscarSeriePorTitulo();

		if(serieBusca.isPresent()) {
			var serie = serieBusca.get();
			System.out.println("Digite o ano limite de lançamento: ");
			var ano = leitura.nextInt();
			leitura.nextLine();

			List<Episodio> espisodios = serieRepository.episodiosPorserieAno(ano, serie);
		}
	}
}
