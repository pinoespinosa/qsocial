package utils.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class PdfEditor {

	private static String TEST_INPUT_FILE_PATH = "resources" + File.separator + "PFB.pdf";
	private static String TEST_OUTPUT_FILE_PATH = "resources" + File.separator + "PFB-Edit.pdf";

	
	/**
	 * 
	 * Removes the pages from PDF file in the the range [fromRange - toRange].
	 * The range to delete includes the pages in the index. So if you had a file
	 * with 5 pages, and remove [2-4], you will have a PDF result with two pages
	 * 1 and 5.
	 * 
	 * @param fileSource
	 * @param fileDestination
	 * @param fromRange
	 * @param toRange
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void removePages(String fileSource, String fileDestination, int fromRange, int toRange)
			throws IOException, DocumentException {

		Document document = new Document();
		document.open();

		PdfReader reader = new PdfReader(fileSource);

		int numPages = reader.getNumberOfPages();

		if (fromRange < 1) {
			System.out.println("You're trying to remeve a page before the first page. File: " + fileSource);
		}

		if (toRange + 1 > numPages) {
			System.out.println("You're trying to remeve a page after the last page");
		}

		if (fromRange <= 1 && toRange >= numPages) {
			System.out.println("You're trying to remeve all pages on the file. The file will not created.");
			return;
		}

		reader.selectPages(processEraseRange(fromRange, toRange, numPages));

		if (!fileDestination.equals(fileSource)) {
			PdfStamper pdfStamper = new PdfStamper(reader, new FileOutputStream(fileDestination));
			pdfStamper.close();
			reader.close();

		} else {
			/*
			 * The library do no support re-write the same file, so this is a
			 * work-around to allow this.
			 */

			String auxName = "_AUX_" + System.currentTimeMillis();
			auxName = fileDestination.replace(".pdf", auxName + ".pdf");

			PdfStamper pdfStamper = new PdfStamper(reader, new FileOutputStream(auxName));
			pdfStamper.close();
			reader.close();

			reader = new PdfReader(auxName);
			pdfStamper = new PdfStamper(reader, new FileOutputStream(fileDestination));
			pdfStamper.close();
			reader.close();

			new File(auxName).delete();

		}

	}

	/**
	 * Removes a page from PDF file.
	 * 
	 * @param fileSource
	 * @param fileDestination
	 * @param page
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void removePage(String fileSource, String fileDestination, int page)
			throws IOException, DocumentException {
		removePages(fileSource, fileDestination, page, page);

	}

	private static String processEraseRange(int startRange, int endRange, int numPages) {

		if (startRange <= 1)
			return (Math.max(endRange + 1, 1)) + "-" + numPages;

		if (endRange >= numPages)
			return (1 + "-" + Math.min(startRange - 1, numPages));

		return (1 + "-" + (startRange - 1) + "," + (endRange + 1) + "-" + numPages);

	}

	@Test
	public void removeTribialRange() {
		try {
			PdfReader readerInput = new PdfReader(TEST_INPUT_FILE_PATH);
			int pagesInput = readerInput.getNumberOfPages();
			readerInput.close();
			removePages(TEST_INPUT_FILE_PATH, TEST_OUTPUT_FILE_PATH, 2, 5);

			PdfReader readerOutput = new PdfReader(TEST_OUTPUT_FILE_PATH);

			assert (pagesInput == readerOutput.getNumberOfPages() + 4);

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void removeFirstPage() {
		try {
			PdfReader readerInput = new PdfReader(TEST_INPUT_FILE_PATH);
			int pagesInput = readerInput.getNumberOfPages();
			readerInput.close();

			removePages(TEST_INPUT_FILE_PATH, TEST_OUTPUT_FILE_PATH, 1, 1);

			PdfReader readerOutput = new PdfReader(TEST_OUTPUT_FILE_PATH);

			assert (pagesInput == readerOutput.getNumberOfPages() + 1);

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void removeMiddlePage() {
		try {

			PdfReader readerInput = new PdfReader(TEST_INPUT_FILE_PATH);
			int pagesInput = readerInput.getNumberOfPages();
			readerInput.close();

			removePages(TEST_INPUT_FILE_PATH, TEST_OUTPUT_FILE_PATH, 25, 25);

			PdfReader readerOutput = new PdfReader(TEST_OUTPUT_FILE_PATH);

			assert (pagesInput == readerOutput.getNumberOfPages() + 1);

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void removeLastPage() {
		try {

			PdfReader readerInput = new PdfReader(TEST_INPUT_FILE_PATH);
			int pagesInput = readerInput.getNumberOfPages();
			readerInput.close();

			removePages(TEST_INPUT_FILE_PATH, TEST_OUTPUT_FILE_PATH, 669, 669);

			PdfReader readerOutput = new PdfReader(TEST_OUTPUT_FILE_PATH);

			assert (pagesInput == readerOutput.getNumberOfPages() + 1);

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void removePagesOutOfRange1() {
		try {

			PdfReader readerInput = new PdfReader(TEST_INPUT_FILE_PATH);
			int pagesInput = readerInput.getNumberOfPages();
			readerInput.close();

			removePages(TEST_INPUT_FILE_PATH, TEST_OUTPUT_FILE_PATH, 1000, 1500);

			PdfReader readerOutput = new PdfReader(TEST_OUTPUT_FILE_PATH);

			assert (pagesInput == readerOutput.getNumberOfPages());

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void removePagesOutOfRange2() {
		try {

			PdfReader readerInput = new PdfReader(TEST_INPUT_FILE_PATH);
			int pagesInput = readerInput.getNumberOfPages();
			readerInput.close();

			removePages(TEST_INPUT_FILE_PATH, TEST_OUTPUT_FILE_PATH, -50, -25);

			PdfReader readerOutput = new PdfReader(TEST_OUTPUT_FILE_PATH);

			assert (pagesInput == readerOutput.getNumberOfPages());

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void removePagesOutOfRange3() {
		try {

			PdfReader readerInput = new PdfReader(TEST_INPUT_FILE_PATH);
			int pagesInput = readerInput.getNumberOfPages();
			readerInput.close();

			removePages(TEST_INPUT_FILE_PATH, TEST_OUTPUT_FILE_PATH, -50, 5);

			PdfReader readerOutput = new PdfReader(TEST_OUTPUT_FILE_PATH);

			assert (pagesInput == readerOutput.getNumberOfPages() + 5);

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	/**
	 * All pages are remove, so non file must be created.
	 */
	public void removeAllPages() {

		try {

			// Remove previous version of output file
			new File(TEST_OUTPUT_FILE_PATH).delete();

			removePages(TEST_INPUT_FILE_PATH, TEST_OUTPUT_FILE_PATH, 1, 669);

			assert (!new File(TEST_OUTPUT_FILE_PATH).exists());

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

	}

}
