package com.example.PAP_API.services;

import com.example.PAP_API.dto.ParticipantReportDto;
import com.example.PAP_API.dto.ReviewAnswerDto;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class ReportPdfService {

    class FooterEvent extends PdfPageEventHelper {
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
        private final Image logo;

        public FooterEvent() throws Exception {
            // Load from resources folder
            java.net.URL logoUrl = getClass().getResource("/images/CIT_logo_bg_removed.png");
            if (logoUrl == null) {
                throw new RuntimeException("Logo file not found!");
            }
            this.logo = Image.getInstance(logoUrl);
            this.logo.scaleAbsolute(90, 25);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            float left = document.left();
            float right = document.right();
            float bottom = document.bottom();

            // --- Draw a horizontal line above footer ---
            cb.setColorStroke(BaseColor.LIGHT_GRAY);
            cb.moveTo(left, bottom + 15);   // starting point (X, Y)
            cb.lineTo(right, bottom + 15);  // ending point (X, Y)
            cb.stroke();

            // --- Left bottom: Company logo ---
            float xLogo = left;
            float yLogo = bottom - 15; // slightly below margin
            logo.setAbsolutePosition(xLogo, yLogo);
            try {
                cb.addImage(logo);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            // --- Right bottom: Footer text ---
            Phrase footer = new Phrase("Generated on: " + java.time.LocalDate.now(), footerFont);
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_RIGHT,
                    footer,
                    right,          // X position — right margin
                    bottom,         // Y position — aligned with logo
                    0
            );
        }
    }

    public ByteArrayInputStream generateParticipantReportStyled2(ParticipantReportDto reportDto) {
        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new FooterEvent());

            document.open();

            // ===== FONT STYLES =====
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            titleFont.setColor(new BaseColor(33, 37, 41)); // dark gray

            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            sectionFont.setColor(new BaseColor(52, 73, 94));

            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            normalFont.setColor(new BaseColor(33, 37, 41));

            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            labelFont.setColor(BaseColor.BLACK);

            Font smallGray = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9);
            smallGray.setColor(new BaseColor(100, 100, 100));

            // ===== HEADER =====
            Paragraph title = new Paragraph("Appraisal Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);

            // ===== BASIC DETAILS TABLE =====
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            detailsTable.setSpacingAfter(15);
            detailsTable.setWidths(new float[]{1.5f, 3f});

            detailsTable.addCell(getCell("Participant Name:", labelFont, true));
            detailsTable.addCell(getCell(reportDto.getEmployeeName() + " (" + reportDto.getEmployeeId() + ")", normalFont, true));

            detailsTable.addCell(getCell("Designation:", labelFont, true));
            detailsTable.addCell(getCell(reportDto.getDesignation(), normalFont, true));

            detailsTable.addCell(getCell("Reporting Manager:", labelFont, true));
            detailsTable.addCell(getCell(reportDto.getManagerName(), normalFont, true));

            detailsTable.addCell(getCell("Appraisal Name:", labelFont, true));
            detailsTable.addCell(getCell(reportDto.getAppraisalTitle() + " - " + reportDto.getAppraisalType(), normalFont, true));

            document.add(detailsTable);

            // ===== SECTION DIVIDER =====
            LineSeparator separator = new LineSeparator();
            separator.setLineColor(new BaseColor(180, 180, 180));
            document.add(separator);
            document.add(Chunk.NEWLINE);

            // ===== REVIEW SUMMARY =====
            Paragraph reviewSummary = new Paragraph("Review Summary", sectionFont);
            reviewSummary.setSpacingAfter(10);
            document.add(reviewSummary);

            List<ReviewAnswerDto> selfList = reportDto.getSelfReview();
            List<ReviewAnswerDto> managerList = reportDto.getReportingReview();

            int total = Math.max(
                    selfList != null ? selfList.size() : 0,
                    managerList != null ? managerList.size() : 0
            );

            // ===== REVIEW TABLE =====
            PdfPTable reviewTable = new PdfPTable(3);
            reviewTable.setWidthPercentage(100);
            reviewTable.setWidths(new float[]{0.7f, 2.3f, 2.3f});
            reviewTable.setSpacingBefore(10);
            reviewTable.setHeaderRows(1);

            // Header row
            reviewTable.addCell(getHeaderCell("Qn No."));
            reviewTable.addCell(getHeaderCell("Self Review"));
            reviewTable.addCell(getHeaderCell("Manager Comment"));

            // Data rows
            for (int i = 0; i < total; i++) {
                ReviewAnswerDto self = (selfList != null && i < selfList.size()) ? selfList.get(i) : null;
                ReviewAnswerDto manager = (managerList != null && i < managerList.size()) ? managerList.get(i) : null;

                String question = (self != null) ? self.getQuestion() :
                        (manager != null ? manager.getQuestion() : "Question " + (i + 1));
                String selfAns = (self != null && self.getAnswer() != null && !self.getAnswer().isBlank()) ? self.getAnswer() : "-";
                String mgrAns = (manager != null && manager.getAnswer() != null && !manager.getAnswer().isBlank()) ? manager.getAnswer() : "-";

                PdfPCell qCell = new PdfPCell(new Phrase("Q" + (i + 1), labelFont));
                qCell.setBackgroundColor(new BaseColor(240, 240, 240));
                qCell.setPadding(6);

                PdfPCell selfCell = new PdfPCell();
                selfCell.addElement(new Phrase(question, smallGray));
                selfCell.addElement(new Phrase("🟢 " + selfAns, normalFont));
                selfCell.setPadding(6);

                PdfPCell mgrCell = new PdfPCell();
//                mgrCell.addElement(new Phrase(question, smallGray));
                mgrCell.addElement(new Phrase("🟡 " + mgrAns, normalFont));
                mgrCell.setPadding(6);

                reviewTable.addCell(qCell);
                reviewTable.addCell(selfCell);
                reviewTable.addCell(mgrCell);
            }

            document.add(reviewTable);
            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // ===== Utility methods =====
    private PdfPCell getCell(String text, Font font, boolean shaded) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setBorder(Rectangle.NO_BORDER);
        if (shaded) cell.setBackgroundColor(new BaseColor(245, 245, 245));
        return cell;
    }

    private PdfPCell getHeaderCell(String text) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        headerFont.setColor(BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setBackgroundColor(new BaseColor(52, 73, 94));
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

}
