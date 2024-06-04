package br.com.streampush;

import br.com.streampush.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreampushApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(StreampushApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Principal principal = new Principal();
		principal.exibeMenu();
	}
}
