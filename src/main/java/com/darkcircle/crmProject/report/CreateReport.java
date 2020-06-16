package com.darkcircle.crmProject.report;

import com.darkcircle.crmProject.models.Request;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class CreateReport {

    public void createPDF(ArrayList<Request> requests, Date date1, Date date2, String company, String pathname) {
        try {

            Date fileDate = new Date();

            SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm:ss dd_MM_yyyy");
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd.MM.yyyy");
            OutputStream file = new FileOutputStream(new File(pathname));
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, file);

            //Inserting Image in PDF
            Image image = Image.getInstance("logo.png");
            image.scaleAbsolute(104f, 31f);//image width,height
            image.setAlignment(Element.ALIGN_CENTER);

            // Заголовок

            BaseFont baseFont = BaseFont.createFont("times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font1 = new Font(baseFont, 18);
            Font font2 = new Font(baseFont, 12);
            Font font3 = new Font(baseFont, 10);
            Paragraph title = new Paragraph("Учет выполненных работ " + company + " с " + dateFormat3.format(date1) + " по " + dateFormat3.format(date2), font1);
            title.setAlignment(Element.ALIGN_CENTER);


            //Inserting Table in PDF
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            table.addCell(createColumn("Дата", font2));
            table.addCell(createColumn("Ответственный", font2));
            table.addCell(createColumn("Вид обслуживания", font2));
            table.addCell(createColumn("Сотрудник", font2));
            table.addCell(createColumn("Вид работ", font2));
            table.addCell(createColumn("Время, мин", font2));
            table.addCell(createColumn("Дополнительная информация", font2));

            for (Request request : requests) {

                table.addCell(new Phrase(String.valueOf(request.getRequestDate()), font3));
                table.addCell(new Phrase(request.getResponsible(), font3));
                table.addCell(new Phrase(request.getWorkType(), font3));
                table.addCell(new Phrase(request.getName(), font3));
                table.addCell(new Phrase(request.getWorkList(), font3));
                table.addCell(new Phrase(String.valueOf(request.getWorkDuration()), font3));

            }
            table.setSpacingBefore(30.0f);       // Space Before table starts, like margin-top in CSS
            table.setSpacingAfter(30.0f);        // Space After table starts, like margin-Bottom in CSS


            //Now Insert Every Thing Into PDF Document
            document.open(); //PDF document opened........

            document.add(image);
            document.add(Chunk.NEWLINE);   //Something like in HTML 🙂
            document.add(title);
            document.add(table);

            document.close();

            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PdfPCell createColumn(String columnName, Font font){
        PdfPCell cell = new PdfPCell(new Phrase(columnName, font));
        return cell;
    }

}
