package net.objecthunter.timmy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.jempbox.xmp.pdfa.XMPSchemaPDFAId;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;

public class TimeSheetGenerator {

	private static final DateFormat dateFormat = new SimpleDateFormat("d.M.y");

	private final String pathOut;
	private final String pathIn;
	private final String projectName;

	public TimeSheetGenerator(String projectName, String pathIn, String pathOut) {
		super();
		this.pathOut = pathOut;
		this.pathIn = pathIn;
		this.projectName = projectName;
	}

	public void generatePdf() throws Exception {
		List<Timespan> spans = readTimespans();
		checkandEditTimespans(spans);
		createPDF(createData(spans));
	}

	private TimesheetData createData(List<Timespan> spans) {
		TimesheetData.Builder tb = new TimesheetData.Builder();
			tb.contractorName("Frank Asseg Softwareentwicklung")
			.contractorStreet("Feichtmayrstrasse")
			.contractorStreetNumber("37")
			.contractorZip("76646")
			.contractorCity("Bruchsal")
			.customerName("FIZ Karlsruhe")
			.customerStreet("Hermann-von-Helmholtz-Platz")
			.customerStreetNumber("1")
			.customerZip("76344")
			.customerCity("Eggenstein-Leopoldshafen")
			.projectName("SCAPE")
			.projectManager("Matthias Razum")
			.timespans(spans);
		return tb.build();
	}

	private void checkandEditTimespans(List<Timespan> spans) throws IOException {
		for (Timespan span : spans) {
			while (validateSpan(span)) {
				System.out.println("The following entry needs editing, since the pause is invalid.");
				System.out.println("--------------------");
				System.out.println("DATE:\t" + dateFormat.format(span.getDate()));
				System.out.println("BEGIN:\t" + formatMinutes(span.getStartTime()));
				System.out.println("END:\t" + formatMinutes(span.getEndTime()));
				System.out.println("PAUSE:\t" + formatMinutes(span.getPause()));
				System.out.println("AMOUNT:\t" + formatMinutes(span.getAmount()));
				System.out.println("--------------------");
				System.out.println("Which entry do you want to change? [p/b/e]");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				String line = in.readLine().toLowerCase();
				if (line.equals("p")) {
					System.out.println("Please input new break time [hh:mm]");
					line = in.readLine().toLowerCase();
					int newPause = parseMinutes(line);
					System.out.println("Shall I change the end time acoordingly? [y/n]");
					line = in.readLine().toLowerCase();
					if (line.equals("y")) {
						int diff = (span.getPause() - newPause) % (24 * 60);
						int newEnd = span.getEndTime() - diff;
						span.setEndTime(newEnd);
					}
					span.setPause(newPause);
				} else if (line.equals("b")) {
					System.out.println("Please input new start time [hh:mm]");
					line = in.readLine().toLowerCase();
					span.setStartTime(parseMinutes(line));
				} else if (line.equals("e")) {
					System.out.println("Please input new end time [hh:mm]");
					line = in.readLine().toLowerCase();
					span.setEndTime(parseMinutes(line));
				}
				System.out.println("The record was updated: ");
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
				System.out.println("DATE:\t" + dateFormat.format(span.getDate()));
				System.out.println("BEGIN:\t" + formatMinutes(span.getStartTime()));
				System.out.println("END:\t" + formatMinutes(span.getEndTime()));
				System.out.println("PAUSE:\t" + formatMinutes(span.getPause()));
				System.out.println("AMOUNT:\t" + formatMinutes(span.getAmount()));
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
			}
		}
	}

	private String formatMinutes(int minutes) {
		int hours = minutes / 60;
		int mins = minutes % 60;
		return (hours > 9 ? hours : "0" + hours) + ":" + (mins > 9? mins: "0" + mins);
	}

	private boolean validateSpan(Timespan span) {
		return span.getPause() > 12 * 60 || (span.getPause() < 30 && span.getAmount() >= 4 * 60);
	}

	private List<Timespan> readTimespans() throws IOException, ParseException {
		String line;
		List<Timespan> spans = new ArrayList<Timespan>();
		BufferedReader csv = new BufferedReader(new FileReader(this.pathIn));

		while ((line = csv.readLine()) != null) {
			String[] data = line.split(",");
			if (data[0].equals("Date") || data[0].equals("Total")) {
				continue;
			}
			Date date = dateFormat.parse(data[0]);
			int begin = parseMinutes(data[2]);
			int end = parseMinutes(data[3]);
			int pause = parseMinutes(data[4]);
			int amount = parseMinutes(data[5]);
			spans.add(new Timespan(date, begin, end, pause, amount));
		}

		return spans;
	}

	private static int parseMinutes(String time) {
		int posSep = time.indexOf(':');
		return Integer.parseInt(time.substring(0, posSep)) * 60 + Integer.parseInt(time.substring(posSep + 1));
	}
	
	private void createPDF(TimesheetData data) throws Exception {
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		doc.addPage(page);

		InputStream fontStream = TimeSheetGenerator.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/ArialMT.ttf");
		PDFont font = PDTrueTypeFont.loadTTF(doc, fontStream);

		fontStream = TimeSheetGenerator.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/Arial-BoldMT.ttf");
		PDFont fontBold = PDTrueTypeFont.loadTTF(doc, fontStream);

		fontStream = TimeSheetGenerator.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/Arial-BoldItalicMT.ttf");
		PDFont fontBoldItalic = PDTrueTypeFont.loadTTF(doc, fontStream);

		PDPageContentStream pageStream = new PDPageContentStream(doc, page);
		pageStream.beginText();
		pageStream.setFont(fontBoldItalic, 16);
		pageStream.moveTextPositionByAmount(220f, 700f);
		pageStream.drawString("Leistungsnachweis");
		pageStream.setFont(fontBold, 11);
		pageStream.moveTextPositionByAmount(-140f, -60f);
		pageStream.drawString("Leistungserbringer");
		pageStream.setFont(font, 10);
		pageStream.moveTextPositionByAmount(0f, -14f);
		pageStream.drawString(data.getContractorName());
		pageStream.moveTextPositionByAmount(0f, -14f);
		pageStream.drawString(data.getContractorStreet() + " " + data.getContractorStreetNumber());
		pageStream.moveTextPositionByAmount(0f, -14f);
		pageStream.drawString(data.getContractorZip() + " " + data.getContractorCity());

		pageStream.moveTextPositionByAmount(300f, 42f);
		pageStream.setFont(fontBold, 11);
		pageStream.drawString("Leistungsnehmer");
		pageStream.moveTextPositionByAmount(0f, -14f);
		pageStream.setFont(font, 10);
		pageStream.drawString(data.getCustomerName());
		pageStream.moveTextPositionByAmount(0f, -14f);
		pageStream.drawString(data.getCustomerStreet() + " " + data.getCustomerStreetNumber());
		pageStream.moveTextPositionByAmount(0f, -14f);
		pageStream.drawString(data.getCustomerZip() + " " + data.getCustomerCity());

		pageStream.setFont(fontBold, 11);
		pageStream.moveTextPositionByAmount(-300f, -24f);
		pageStream.drawString("Projekt");
		pageStream.setFont(font, 10);
		pageStream.moveTextPositionByAmount(0, -14f);
		pageStream.drawString(data.getProjectName());

		pageStream.setFont(fontBold, 11);
		pageStream.moveTextPositionByAmount(300f, 14f);
		pageStream.drawString("Projektleiter");
		pageStream.setFont(font, 10);
		pageStream.moveTextPositionByAmount(0f, -14f);
		pageStream.drawString(data.getProjectManager());

		pageStream.setFont(fontBold, 11);
		pageStream.moveTextPositionByAmount(-300f, -48f);
		pageStream.drawString("Datum");
		pageStream.moveTextPositionByAmount(100f, 0f);
		pageStream.drawString("Beginn");
		pageStream.moveTextPositionByAmount(90f, 0f);
		pageStream.drawString("Ende");
		pageStream.moveTextPositionByAmount(90f, 0f);
		pageStream.drawString("Pause");
		pageStream.moveTextPositionByAmount(90f, 0f);
		pageStream.drawString("Stunden");

		pageStream.setFont(font, 10);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		for (Timespan p : data.getTimespans()) {
			pageStream.moveTextPositionByAmount(-370f, -14f);
			pageStream.drawString(dateFormat.format(p.getDate()));
			pageStream.moveTextPositionByAmount(100f, 0f);
			pageStream.drawString(formatMinutes(p.getStartTime()));
			pageStream.moveTextPositionByAmount(90f, 0f);
			pageStream.drawString(formatMinutes(p.getEndTime()));
			pageStream.moveTextPositionByAmount(90f, 0f);
			pageStream.drawString(formatMinutes(p.getPause()));
			pageStream.moveTextPositionByAmount(90f, 0f);
			pageStream.drawString(decimalFormat.format((float) p.getAmount() / 60f));
		}
		pageStream.moveTextPositionByAmount(-370f, -40f);
		pageStream.setFont(fontBold, 12);
		pageStream.drawString("Summe");
		pageStream.moveTextPositionByAmount(370f, 0f);
		int hours = (int) data.getSumMinutes() / (int) 60;
		int minutes = data.getSumMinutes() % 60;
		pageStream.drawString(hours + ":" + minutes);
		pageStream.moveTextPositionByAmount(-370f, -14f);
		pageStream.setFont(fontBold, 11);
		pageStream.drawString("Summe Personenstunden");
		pageStream.moveTextPositionByAmount(370f, 0f);
		pageStream.drawString(decimalFormat.format((float) data.getSumMinutes() / 60f));

		pageStream.setFont(font, 10);
		pageStream.moveTextPositionByAmount(-370f, -80f);
		pageStream.drawString("---------------------------------------");
		pageStream.moveTextPositionByAmount(280f, 0f);
		pageStream.drawString("---------------------------------------");
		pageStream.moveTextPositionByAmount(-280f, -14f);
		pageStream.drawString("Datum, Leistungserbringer");
		pageStream.moveTextPositionByAmount(280f, 0f);
		pageStream.drawString("Datum, Projektleiter");

		pageStream.endText();
		pageStream.saveGraphicsState();
		pageStream.close();

		PDDocumentCatalog cat = doc.getDocumentCatalog();
		PDMetadata metadata = new PDMetadata(doc);
		cat.setMetadata(metadata);

		XMPMetadata xmp = new XMPMetadata();
		XMPSchemaPDFAId pdfaid = new XMPSchemaPDFAId(xmp);
		xmp.addSchema(pdfaid);
		pdfaid.setConformance("B");
		pdfaid.setPart(1);
		pdfaid.setAbout("");
		metadata.importXMPMetadata(xmp);

		InputStream colorProfile = TimeSheetGenerator.class.getClassLoader().getResourceAsStream("srgb.icm");
		// create output intent
		PDOutputIntent oi = new PDOutputIntent(doc, colorProfile);
		oi.setInfo("sRGB IEC61966-2.1");
		oi.setOutputCondition("sRGB IEC61966-2.1");
		oi.setOutputConditionIdentifier("sRGB IEC61966-2.1");
		oi.setRegistryName("http://www.color.org");
		cat.addOutputIntent(oi);

		doc.save(pathOut);

		doc.close();
	}
}
