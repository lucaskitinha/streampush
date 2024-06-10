package br.com.streampush.principal;

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
		Optional<Serie> serie = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

		if(serie.isPresent()){
			System.out.println("Dados da série: " + serie.get());
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
}
