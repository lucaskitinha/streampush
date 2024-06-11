package br.com.streampush.repository;

import br.com.streampush.model.Categoria;
import br.com.streampush.model.Episodio;
import br.com.streampush.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long> {

	Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

	List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

	List<Serie> findTop5ByOrderByAvaliacaoDesc();

	List<Serie> findByGenero(Categoria categoria);

	List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, double avaliacao);

	@Query("select s from Serie s where s.totalTemporadas <= :totalTemporadas and s.avaliacao >= :avaliacao")
	List<Serie> seriesPorTemporadaEAvaliacao(int totalTemporadas, double avaliacao);

	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trecho%")
	List<Episodio> episodiosPorTrecho(String trecho);

	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY s.avaliacao DESC LIMIT 5")
	List<Episodio> topEpisodiosPorSerie(Serie serie);

	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :ano")
	List<Episodio> episodiosPorserieAno(int ano, Serie serie);
}
