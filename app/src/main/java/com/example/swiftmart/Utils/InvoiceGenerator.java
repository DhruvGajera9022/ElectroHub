package com.example.swiftmart.Utils;

import com.example.swiftmart.Model.InvoiceModel;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.Border;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class InvoiceGenerator {

    public void generateInvoice(InvoiceModel invoice, String fileName) throws Exception {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, fileName);

        if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
            throw new IOException("Failed to create the Downloads directory.");
        }

        PdfWriter writer = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Invoice")
                .setFontSize(30)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15)
                .setFontColor(WebColors.getRGBColor("#F7C650")));

        Table mainTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        mainTable.addCell(createSectionTable("Bill To", new String[][]{
                {"Client Name:", invoice.getClientName()},
                {"Company Name:", invoice.getCompanyName()},
                {"Billing Address:", invoice.getBillingAddress()},
                {"Phone:", invoice.getPhone()},
                {"Email:", invoice.getEmail()}
        }));

        mainTable.addCell(createSectionTable("Invoice Info", new String[][]{
                {"Invoice ID:", invoice.getInvoiceId()},
                {"Order ID:", invoice.getOrderId()},
                {"Order Date:", invoice.getOrderDate()},
                {"Invoice Date:", invoice.getInvoiceDate()}
        }));

        document.add(mainTable);
        document.add(new Paragraph("Product Details:").setBold().setFontSize(16).setMarginTop(15));

        Table serviceTable = new Table(UnitValue.createPercentArray(new float[]{1, 4, 2, 2, 2})).useAllAvailableWidth();
        addTableHeaders(serviceTable, new String[]{"No", "Product Name", "Quantity", "Unit Price", "Total ($)"});

        double subtotal = 0;
        List<InvoiceModel.Service> services = invoice.getServices();
        for (int i = 0; i < services.size(); i++) {
            InvoiceModel.Service service = services.get(i);
            double total = service.getQuantity() * service.getRate();
            subtotal += total;

            boolean addColor = i % 2 != 0;
            addRow(serviceTable, new String[]{String.valueOf(i + 1), service.getDescription(), String.valueOf(service.getQuantity()), String.format("%.2f", service.getRate()), String.format("%.2f", total)}, addColor);
        }
        document.add(serviceTable);

        double tax = subtotal * invoice.getTaxRate() / 100;
        double totalAmount = subtotal + tax;
        document.add(getKeyValueParagraph("Subtotal: ", String.format("%.2f", subtotal)));
        document.add(getKeyValueParagraph("Tax (" + invoice.getTaxRate() + "%): ", String.format("%.2f", tax)));
        document.add(getKeyValueParagraph("Total Amount Due: ", String.format("%.2f", totalAmount)).setBold());

        document.add(new Paragraph("Payment Information:").setBold().setFontSize(14).setMarginTop(15));
        document.add(new Paragraph("Payment Method: " + invoice.getPaymentMethod() + "\nDue Date: " + invoice.getDueDate() + "\nBank Account: " + invoice.getBankAccount()));

        document.add(new Paragraph(invoice.getAdditionalNotes()).setMarginTop(30).setItalic().setHorizontalAlignment(HorizontalAlignment.CENTER));
        document.close();
    }

    private Table createSectionTable(String title, String[][] data) {
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth().setMarginBottom(10);
        table.addCell(new Cell(1, 2).add(new Paragraph(title).setBold().setFontSize(16)).setBorder(Border.NO_BORDER));
        for (String[] row : data) {
            table.addCell(createCell(row[0], true));
            table.addCell(createCell(row[1], false));
        }
        return table;
    }

    private void addTableHeaders(Table table, String[] headers) {
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(WebColors.getRGBColor("#F7C650"))
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }

    private void addRow(Table table, String[] data, boolean addColor) {
        for (String content : data) {
            Cell cell = new Cell().add(new Paragraph(content).setFontColor(ColorConstants.DARK_GRAY))
                    .setPadding(5)
                    .setBorder(Border.NO_BORDER);
            if (addColor) cell.setBackgroundColor(WebColors.getRGBColor("#DFE1E5"));
            table.addCell(cell);
        }
    }

    private Cell createCell(String content, boolean isBold) {
        Paragraph paragraph = new Paragraph(content).setFontSize(12);
        if (isBold) paragraph.setBold();
        return new Cell().add(paragraph).setBorder(Border.NO_BORDER).setPadding(5);
    }

    private Paragraph getKeyValueParagraph(String key, String value) {
        return new Paragraph().add(new Text(key).setBold().setFontSize(12))
                .add(new Text(value).setFontSize(11).setFontColor(ColorConstants.DARK_GRAY));
    }
}
