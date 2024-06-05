package br.com.streampush.principal;

import br.com.streampush.model.DadosEpisodio;
import br.com.streampush.model.DadosSerie;
import br.com.streampush.model.DadosTemporada;
import br.com.streampush.model.Episodio;
import br.com.streampush.service.ConsumoApi;
import br.com.streampush.service.ConverteDados;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

	private Scanner leitura = new Scanner(System.in);
	private ConsumoApi consumoApi = new ConsumoApi();
	private ConverteDados conversor = new ConverteDados();
	private final String ENDERECO = "https://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=94294467";
	public void exibeMenu() throws UnsupportedEncodingException {
		System.out.println("Digite o nome da série para o usuário: ");
		var nomeSerie = leitura.nextLine();
		var url = ENDERECO+ URLEncoder.encode(nomeSerie, StandardCharsets.UTF_8.toString()) +API_KEY;
		consumoApi = new ConsumoApi();
		var json = consumoApi.obterDados(url);
		conversor = new ConverteDados();
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);
		List<DadosTemporada> temporadas = new ArrayList<>();

		for(int i = 1; i<=dados.totalTemporada(); i++) {
			url = ENDERECO + URLEncoder.encode(nomeSerie, StandardCharsets.UTF_8.toString()) + "&season=" + i + API_KEY;
			json = consumoApi.obterDados(url);
			DadosTemporada dadosTemporada = conversor.obterDados(json,DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);
		temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

		List<DadosEpisodio> dadosEpisodios = temporadas.stream().flatMap(t -> t.episodios().stream()).collect(Collectors.toList());

		dadosEpisodios.stream()
				.filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
				.sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed()).limit(5)
				.forEach(System.out::println);

		List<Episodio> episodios = temporadas.stream()
				.flatMap(t -> t.episodios().stream()
						.map(d -> new Episodio(t.numeroTemporada(), d))
				).collect(Collectors.toList());
		episodios.forEach(System.out::println);

		System.out.println("A partir de que ano você deseja ver os episódios");
		var ano = leitura.nextInt();
		leitura.nextLine();

		LocalDate dataBusca = LocalDate.of(ano, 1, 1);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		episodios.stream().filter(e -> e.getDataLancamento()!= null && e.getDataLancamento().isAfter(dataBusca))
				.forEach(e -> System.out.println(
						"Temporada: " + e.getTemporada() +
								" Episódio: " + e.getTitulo() +
								" Data Lançamento: " + e.getDataLancamento().format(formatter)
				));

	}
}
