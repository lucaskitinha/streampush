package br.com.streampush.config;

import br.com.streampush.principal.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Autowired
	private Principal principal;

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			principal.exibeMenu();
		};
	}
}