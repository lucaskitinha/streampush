package br.com.streampush.principal;

import br.com.streampush.model.DadosSerie;
import br.com.streampush.model.DadosTemporada;
import br.com.streampush.model.Serie;
import br.com.streampush.service.ConsumoApi;
import br.com.streampush.service.ConverteDados;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

	private Scanner leitura = new Scanner(System.in);
	private ConsumoApi consumo = new ConsumoApi();
	private ConverteDados conversor = new ConverteDados();
	private final String ENDERECO = "https://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=94294467";
	private List<DadosSerie> dadosSeries = new ArrayList<>();
	public void exibeMenu() throws UnsupportedEncodingException {
		var opcao = -1;
		while(opcao != 0){
			var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                
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
				listarSeriesBuscadas();
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

		List<Serie> series = new ArrayList<>();
		series = dadosSeries.stream().map(d -> new Serie(d)).collect(Collectors.toList());

		series.stream()
				.sorted(Comparator.comparing(Serie::getGenero))
				.forEach(System.out::println);
	}

	private void buscarSerieWeb() throws UnsupportedEncodingException {
		DadosSerie dados = getDadosSerie();
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
		DadosSerie dadosSerie = getDadosSerie();
		List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
			var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);
	}
}
