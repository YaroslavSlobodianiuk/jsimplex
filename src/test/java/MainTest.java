import org.junit.Test;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class MainTest {

	@Test
	public void fileNotFound() {
		assertEquals("Файл не найден: idontexist.txt", Main.perform("idontexist.txt"));
	}

	@Test
	public void outputTest() throws IOException {
		File fixturesDir = new File("src/test/fixtures");
	  	File[] fixtures = fixturesDir.listFiles();

	  	if (fixtures != null)
			for (File fixture : fixtures)
				testFixture(fixture);
	}

	private String output(String fileName) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get("src/test/outputs/" + fileName));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "";
		}
	}

	private void testFixture(File fixture) throws IOException {
		SimplexTable simplexTable = new SimplexTable(Main.createSimplexTable(fixture.getPath()));
		assertEquals(output(fixture.getName()).trim(), ((String) Main.solve(simplexTable).get("answer")).trim());
	}

}
