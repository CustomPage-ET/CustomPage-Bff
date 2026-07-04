package custompage.bff;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
// 👈 Deshabilita la validación directamente en la inicialización de la prueba
@TestPropertySource(properties = "spring.cloud.compatibility-verifier.enabled=false")
class BffApplicationTests {

	@Test
	void contextLoads() {
		// Valida que el contexto cargue de forma limpia
	}
}