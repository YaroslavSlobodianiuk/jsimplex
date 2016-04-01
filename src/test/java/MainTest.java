import org.junit.Test;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class MainTest {

	private String FIXTURES_FOLDER = "src/test/fixtures/";
	private String OUTPUTS_FOLDER = "src/test/outputs/";

	@Test
	public void fileNotFound() {
		assertEquals("Файл не найден: idontexist.txt", Main.perform("idontexist.txt"));
	}

	@Test
	public void defaultTest() throws IOException {
		testDirectory("default");
	}

	@Test
	public void integerTest() throws IOException {
		testDirectory("integer", true, false, false);
	}

	@Test
	public void csvTest() throws IOException {
		testDirectory("csv", false, true, false);
	}

	// @Test
	// public void minimizeTest() throws IOException {
	// 	testDirectory("minimize", false, false, true);
	// }

	private void testDirectory(String dir, boolean integer, boolean csv, boolean minimize) throws IOException {
		File fixturesDir = new File(FIXTURES_FOLDER + dir);
	  	File[] fixtures = fixturesDir.listFiles();

	  	if (fixtures != null)
			for (File fixture : fixtures)
				testFixture(dir, fixture, integer, csv, minimize);
	}

	private void testDirectory(String dir) throws IOException {
		testDirectory(dir, false, false, false);
	}

	private void testFixture(String dir, File fixture, boolean integer, boolean csv, boolean minimize) throws IOException {
		SimplexTable simplexTable = new SimplexTable(Main.createSimplexTable(fixture.getPath(), minimize));
		assertEquals(output(dir, fixture.getName()).trim(), Main.solve(simplexTable).toString(integer, csv).trim());
	}

	private String output(String dir, String fileName) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(OUTPUTS_FOLDER + dir + "/" + fileName));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "";
		}
	}

}
