package com.freelanceos.freelanceappback.infrastructure.persistence.adapter.pdf;

import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;
import com.freelanceos.freelanceappback.domain.model.mission.MissionSummaryForInvoice;
import com.freelanceos.freelanceappback.domain.ports.out.pdf.InvoicePdfGenerator;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

@Component
public class OpenPdfInvoicePdfGenerator implements InvoicePdfGenerator {
    private static final Locale LOCALE = Locale.FRANCE;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color BRAND_COLOR = new Color(27, 57, 84);
    private static final Color TABLE_HEADER_COLOR = new Color(236, 240, 244);

    @Override
    public byte[] generate(Invoice invoice) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 42, 42, 48, 42);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addHeader(document, invoice);
            addClientBlock(document, invoice);
            addLineItems(document, invoice);
            addTotals(document, invoice);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Unable to generate invoice PDF", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to write invoice PDF", ex);
        }
    }

    private void addHeader(Document document, Invoice invoice) throws DocumentException {
        PdfPTable header = new PdfPTable(new float[]{2, 1});
        header.setWidthPercentage(100);

        PdfPCell titleCell = withoutBorder();
        Paragraph title = new Paragraph("Facture", font(24, Font.BOLD, BRAND_COLOR));
        titleCell.addElement(title);
        titleCell.addElement(new Paragraph(invoice.number(), font(12, Font.NORMAL, Color.DARK_GRAY)));
        header.addCell(titleCell);

        PdfPCell datesCell = withoutBorder();
        datesCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        datesCell.addElement(right("Date d'emission : " + invoice.issueDate().format(DATE_FORMATTER)));
        datesCell.addElement(right("Date d'echeance : " + invoice.dueDate().format(DATE_FORMATTER)));
        header.addCell(datesCell);

        document.add(header);
        document.add(spacer(18));
    }

    private void addClientBlock(Document document, Invoice invoice) throws DocumentException {
        MissionSummaryForInvoice mission = invoice.mission();

        PdfPTable block = new PdfPTable(new float[]{1, 1});
        block.setWidthPercentage(100);

        PdfPCell providerCell = borderedCell();
        providerCell.addElement(new Paragraph("Emetteur", font(11, Font.BOLD, BRAND_COLOR)));
        providerCell.addElement(new Paragraph("FreelanceOS", font(10, Font.NORMAL, Color.DARK_GRAY)));
        providerCell.addElement(new Paragraph("Facturation freelance", font(10, Font.NORMAL, Color.DARK_GRAY)));
        block.addCell(providerCell);

        PdfPCell clientCell = borderedCell();
        clientCell.addElement(new Paragraph("Client", font(11, Font.BOLD, BRAND_COLOR)));
        clientCell.addElement(new Paragraph(mission.client().name(), font(10, Font.NORMAL, Color.DARK_GRAY)));
        clientCell.addElement(new Paragraph("Mission : " + mission.title(), font(10, Font.NORMAL, Color.DARK_GRAY)));
        block.addCell(clientCell);

        document.add(block);
        document.add(spacer(22));
    }

    private void addLineItems(Document document, Invoice invoice) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{4, 1.2f, 1.2f, 1.4f});
        table.setWidthPercentage(100);

        addHeaderCell(table, "Prestation");
        addHeaderCell(table, "HT");
        addHeaderCell(table, "TVA");
        addHeaderCell(table, "TTC");

        addBodyCell(table, invoice.mission().title(), Element.ALIGN_LEFT);
        addBodyCell(table, amount(invoice.totalHt(), invoice.mission().currency()), Element.ALIGN_RIGHT);
        addBodyCell(table, percentage(invoice.vatRate()), Element.ALIGN_RIGHT);
        addBodyCell(table, amount(invoice.totalTtc(), invoice.mission().currency()), Element.ALIGN_RIGHT);

        document.add(table);
        document.add(spacer(18));
    }

    private void addTotals(Document document, Invoice invoice) throws DocumentException {
        BigDecimal vatAmount = invoice.totalTtc().subtract(invoice.totalHt()).setScale(2, RoundingMode.HALF_UP);

        PdfPTable totals = new PdfPTable(new float[]{3, 1});
        totals.setWidthPercentage(45);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);

        addTotalRow(totals, "Total HT", amount(invoice.totalHt(), invoice.mission().currency()), false);
        addTotalRow(totals, "TVA " + percentage(invoice.vatRate()), amount(vatAmount, invoice.mission().currency()), false);
        addTotalRow(totals, "Total TTC", amount(invoice.totalTtc(), invoice.mission().currency()), true);

        document.add(totals);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font(10, Font.BOLD, BRAND_COLOR)));
        cell.setBackgroundColor(TABLE_HEADER_COLOR);
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font(10, Font.NORMAL, Color.DARK_GRAY)));
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String value, boolean highlight) {
        Font rowFont = highlight ? font(11, Font.BOLD, BRAND_COLOR) : font(10, Font.NORMAL, Color.DARK_GRAY);
        PdfPCell labelCell = new PdfPCell(new Phrase(label, rowFont));
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setBorderColor(Color.LIGHT_GRAY);
        labelCell.setPadding(7);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, rowFont));
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setBorderColor(Color.LIGHT_GRAY);
        valueCell.setPadding(7);
        table.addCell(valueCell);
    }

    private PdfPCell withoutBorder() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private PdfPCell borderedCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setPadding(10);
        return cell;
    }

    private Paragraph right(String text) {
        Paragraph paragraph = new Paragraph(text, font(10, Font.NORMAL, Color.DARK_GRAY));
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        return paragraph;
    }

    private Paragraph spacer(int height) {
        Paragraph paragraph = new Paragraph(" ");
        paragraph.setSpacingAfter(height);
        return paragraph;
    }

    private Font font(int size, int style, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, size, style, color);
    }

    private String amount(BigDecimal amount, String currencyCode) {
        NumberFormat format = NumberFormat.getCurrencyInstance(LOCALE);
        try {
            format.setCurrency(Currency.getInstance(currencyCode));
        } catch (IllegalArgumentException ignored) {
            return amount.setScale(2, RoundingMode.HALF_UP) + " " + currencyCode;
        }
        return format.format(amount);
    }

    private String percentage(BigDecimal rate) {
        return rate.stripTrailingZeros().toPlainString() + " %";
    }
}
