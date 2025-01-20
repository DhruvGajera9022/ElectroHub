package com.example.swiftmart.Utils;

import com.example.swiftmart.Model.InvoiceModel;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class InvoiceGenerator {

    public void generateInvoice(InvoiceModel invoice, String fileName) throws Exception {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, fileName);

        if (!downloadsDir.exists()) {
            boolean isCreated = downloadsDir.mkdirs();
            if (!isCreated) {
                throw new IOException("Failed to create the Downloads directory.");
            }
        }

        // Initialize PDF writer and document
        PdfWriter writer = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header
        // Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        // headerTable.setWidth(UnitValue.createPercentValue(100));

        Paragraph header = new Paragraph("Electro Hub invoice")
                .setFontSize(34)
                .setBold()
                .setMarginBottom(20);
        document.add(header);
        // headerTable.addCell(new Cell().add(header).setBorder(Border.NO_BORDER));

        // Paragraph headerCompanyName = new Paragraph("INVOICE")
        //         .setFontSize(24)
        //         .setBold()
        //         .setMarginBottom(20);
        // // document.add(headerCompanyName);
        // headerTable.addCell(new Cell().add(headerCompanyName).setBorder(Border.NO_BORDER));
        // document.add(headerTable);

        document.add(new Paragraph("Bill To:").setFontSize(20).setBold());

        document.add(getParagraph("Client Name : ",invoice.getClientName()));
        document.add(getParagraph("Company Name : ",invoice.getCompanyName()));
        document.add(getParagraph("Billing Address : ",invoice.getBillingAddress()));
        document.add(getParagraph("Phone : ",invoice.getPhone()));
        document.add(getParagraph("Email : ",invoice.getEmail()));

        // Service Details Section
        Paragraph serviceHeader = new Paragraph("Service Details:")
                .setBold()
                .setFontSize(14)
                .setMarginTop(20);
        document.add(serviceHeader);

        Table serviceTable = new Table(UnitValue.createPercentArray(new float[]{1, 4, 2, 2, 2}));
        serviceTable.setWidth(UnitValue.createPercentValue(100));
        addHeaderCell(serviceTable,"No");
        addHeaderCell(serviceTable,"Product Name");
        addHeaderCell(serviceTable,"Quantity");
        addHeaderCell(serviceTable,"Per unit Price");
        addHeaderCell(serviceTable,"Total ($)");

        double subtotal = 0;
        List<InvoiceModel.Service> services = invoice.getServices();
        for (int i = 0; i < services.size(); i++) {
            InvoiceModel.Service service = services.get(i);
            double total = service.getQuantity() * service.getRate();
            subtotal += total;

            boolean addColor = i % 2 != 0;
            setCell(serviceTable,String.valueOf(i + 1),addColor);
            setCell(serviceTable,service.getDescription(),addColor);
            setCell(serviceTable,String.valueOf(service.getQuantity()),addColor);
            setCell(serviceTable,String.format("%.2f", service.getRate()),addColor);
            setCell(serviceTable,String.format("%.2f", total),addColor);
        }
        document.add(serviceTable);

        // Subtotal, Tax, and Total Amount
        double tax = subtotal * invoice.getTaxRate() / 100;
        double totalAmount = subtotal + tax;

        // Paragraph totals = new Paragraph()
        //         .add(String.format("Subtotal: %.2f%n", subtotal))
        //         .add(String.format("Tax (%.1f%%): %.2f%n", invoice.getTaxRate(), tax))
        //         .add(String.format("Total Amount Due: %.2f", totalAmount))
        //         .setMarginTop(20);
        // document.add(totals);

        document.add(getParagraph("Subtotal : ", String.format("%.2f%n", subtotal)).setMarginTop(20));
        document.add(getParagraph(String.format("Tax (%.1f%%) : ",invoice.getTaxRate()), String.format("%.2f%n",tax)));
        document.add(getParagraph("Total Amount Due : ",String.format("%.2f", totalAmount)));

        // Payment Information
        Paragraph paymentInfo = new Paragraph("Payment Information:")
                .setBold()
                .setFontSize(14)
                .setMarginTop(20);
        document.add(paymentInfo);

        Paragraph paymentDetails = new Paragraph()
                .add("Payment Method: " + invoice.getPaymentMethod() + "\n")
                .add("Due Date: " + invoice.getDueDate() + "\n")
                .add("Bank Account: " + invoice.getBankAccount() + "\n");
        document.add(paymentDetails);

        // Footer Notes
        Paragraph footer = new Paragraph(invoice.getAdditionalNotes())
                .setMarginTop(30)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(footer);

        // Close the document
        document.close();
    }

    private Paragraph getParagraph(String key,String value)
    {
        Text txtLabel = new Text(key).setBold().setFontSize(12).setFontColor(ColorConstants.BLACK);
        Text txtValue = new Text(value).setFontSize(11).setFontColor(ColorConstants.DARK_GRAY);
        // Combine the text into a single paragraph
        return new Paragraph().add(txtLabel).add(txtValue)
                .setMarginBottom(0)
                .setMarginTop(0);
    }

    private void setCell(Table table,String content,boolean addColor)
    {
        Cell cell = new Cell()
                .add(new Paragraph(content).setFontColor(ColorConstants.DARK_GRAY))
                .setBorder(Border.NO_BORDER);

        if(addColor)
            cell.setBackgroundColor(WebColors.getRGBColor("#DFE1E5"));

        table.addCell(cell);
    }

    private void addHeaderCell(Table table,String content_text)
    {
        // Add Header Row
        Cell headerCell = new Cell()
                .add(new Paragraph(content_text).setBold())
                .setBackgroundColor(WebColors.getRGBColor("#F7C650"))
                .setBorder(Border.NO_BORDER);

        table.addHeaderCell(headerCell);
    }

//    public static void main(String[] args) throws Exception {
//        // Sample Data
//        Invoice invoice = new Invoice();
//        invoice.setClientName("Dhruv gajera");
//        invoice.setCompanyName("ABC Corporation");
//        invoice.setBillingAddress("123 Pramukh park,Amroli");
//        invoice.setPhone("9099235623");
//        invoice.setEmail("dhruv@gmail.com");
//        invoice.setInvoiceNumber("SI2023-001");
//        invoice.setInvoiceDate("September 26, 2030");
//        invoice.setTaxRate(8);
//        invoice.setPaymentMethod("Bank Transfer");
//        invoice.setDueDate("October 15, 2030");
//        invoice.setBankAccount("1234-5678-9012-3456");
//        invoice.setAdditionalNotes("Thanks for shopping from our e-store.");
//
//        // Adding services
//        List<Invoice.Service> services = List.of(
//                new Invoice.Service("iPhone", 1, 100000.00),
//                new Invoice.Service("Tv", 2, 12000.00),
//                new Invoice.Service("earbuds", 1, 800.00),
//                new Invoice.Service("oppo v15", 3, 8000.00)
//        );
//        invoice.setServices(services);
//
//        // Generate Invoice
//        generateInvoice(invoice, "..\\pdfs\\new_gpt_invoice.pdf");
//    }
}
