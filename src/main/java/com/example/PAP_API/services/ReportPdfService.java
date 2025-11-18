package com.example.PAP_API.services;

import com.example.PAP_API.dto.ParticipantReportDto;
import com.example.PAP_API.dto.ReviewAnswerDto;
import com.example.PAP_API.dto.UserDto;
import com.example.PAP_API.repository.HRManagerRepository;
import com.example.PAP_API.repository.NewEmployeeRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class ReportPdfService {

    @Autowired
    UserContextService userContextService;

    @Autowired
    NewEmployeeRepository newEmployeeRepository;

    @Autowired
    HRManagerRepository hrManagerRepository;

    class FooterEvent extends PdfPageEventHelper {
        private final Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
        private final Image logo;
        private final String generatedBy;
        private final String employeeId;

        public FooterEvent(String generatedBy, String employeeId) throws Exception {
            // Load from resources folder
            java.net.URL logoUrl = getClass().getResource("/images/CIT_logo_bg_removed.png");
            if (logoUrl == null) {
                throw new RuntimeException("Logo file not found!");
            }
            this.logo = Image.getInstance(logoUrl);
            this.logo.scaleAbsolute(90, 25);

            this.generatedBy = generatedBy != null ? generatedBy : "System";
            this.employeeId = employeeId != null ? employeeId : "—";
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            float left = document.left();
            float right = document.right();
            float bottom = document.bottom();

            // --- Horizontal line above footer ---
            cb.setColorStroke(BaseColor.LIGHT_GRAY);
            cb.moveTo(left, bottom + 15);
            cb.lineTo(right, bottom + 15);
            cb.stroke();

            // --- Company Logo (Left side) ---
            float xLogo = left;
            float yLogo = bottom - 15;
            logo.setAbsolutePosition(xLogo, yLogo);
            try {
                cb.addImage(logo);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            // --- Footer Texts (Right-aligned) ---
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setTotalWidth(300);
            footerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            String footerText =
                    "Generated on: " + java.time.LocalDate.now() +
                            "   |   By " + generatedBy +
                            " (" + employeeId + ")";

            PdfPCell footerCell = new PdfPCell(new Phrase(footerText, footerFont));
            footerCell.setBorder(Rectangle.NO_BORDER);
            footerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            footerCell.setPaddingRight(10);

            footerTable.addCell(footerCell);
            footerTable.writeSelectedRows(0, -1, right - 300, bottom + 10, cb);
        }
    }


    public ByteArrayInputStream generateParticipantReportStyled2(ParticipantReportDto reportDto) {
        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String downloaderName = userContextService.getCurrentUser().getName();

        UserDto userDto= userContextService.getCurrentUser();
        String downloaderEmployeeId = userDto.getRole().equals("hr") ?
                hrManagerRepository.findById(userDto.getId()).get().getEmployeeId() :
                newEmployeeRepository.findById(userDto.getId()).get().getEmployeeId();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new FooterEvent(downloaderName, downloaderEmployeeId));
            document.open();

            // ===== FONT STYLES =====
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new BaseColor(33, 37, 41));
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new BaseColor(52, 73, 94));
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, new BaseColor(33, 37, 41));
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
            Font smallGray = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, new BaseColor(100, 100, 100));
            Font boldGray = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new BaseColor(90, 90, 90));

            // ===== HEADER =====
            Paragraph title = new Paragraph("Appraisal Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);

            // ===== BASIC DETAILS =====
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

            detailsTable.addCell(getCell("Stage:", labelFont, true));
            detailsTable.addCell(getCell(reportDto.getAppraisalStage(), normalFont, true));

            document.add(detailsTable);

            // ===== METRICS SECTION =====
            Paragraph metricsHeader = new Paragraph("Performance Metrics", sectionFont);
            metricsHeader.setSpacingBefore(15);
            metricsHeader.setSpacingAfter(10);
            document.add(metricsHeader);

            PdfPTable metricsTable = new PdfPTable(2);
            metricsTable.setWidthPercentage(60);
            metricsTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            metricsTable.setSpacingBefore(5);

            metricsTable.addCell(getMetricCell("Average Self Score", reportDto.getAverageSelfScore(), normalFont, labelFont));
            metricsTable.addCell(getMetricCell("Average Manager Score", reportDto.getAverageManagerScore(), normalFont, labelFont));
            metricsTable.addCell(getMetricCell("Score Difference", reportDto.getScoreDifference(), normalFont, labelFont));
            metricsTable.addCell(getMetricCell("Agreement Percentage", reportDto.getAgreementPercentage() + "%", normalFont, labelFont));

            document.add(metricsTable);

            // ===== SECTION DIVIDER =====
            document.add(Chunk.NEWLINE);

            // ===== REVIEW SUMMARY SECTION =====
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
            PdfPTable reviewTable = new PdfPTable(5);
            reviewTable.setWidthPercentage(100);
            reviewTable.setWidths(new float[]{0.6f, 2.4f, 1.0f, 2.4f, 1.0f});
            reviewTable.setSpacingBefore(10);
            reviewTable.setHeaderRows(1);

            // Header row
            reviewTable.addCell(getHeaderCell("Qn No."));
            reviewTable.addCell(getHeaderCell("Self Review"));
            reviewTable.addCell(getHeaderCell("Self Score"));
            reviewTable.addCell(getHeaderCell("Manager Comment"));
            reviewTable.addCell(getHeaderCell("Manager Score"));

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

                PdfPCell selfScoreCell = new PdfPCell(new Phrase(
                        self != null && self.getScore() != null ? String.valueOf(self.getScore()) : "-", normalFont));
                selfScoreCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                selfScoreCell.setPadding(6);

                PdfPCell mgrCell = new PdfPCell();
                mgrCell.addElement(new Phrase("🟡 " + mgrAns, normalFont));
                mgrCell.setPadding(6);

                PdfPCell mgrScoreCell = new PdfPCell(new Phrase(
                        manager != null && manager.getScore() != null ? String.valueOf(manager.getScore()) : "-", normalFont));
                mgrScoreCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mgrScoreCell.setPadding(6);

                reviewTable.addCell(qCell);
                reviewTable.addCell(selfCell);
                reviewTable.addCell(selfScoreCell);
                reviewTable.addCell(mgrCell);
                reviewTable.addCell(mgrScoreCell);
            }

            document.add(reviewTable);
            document.add(Chunk.NEWLINE);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

// ===== HELPER CELLS =====

    private PdfPCell getCell(String text, Font font, boolean borderless) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        if (borderless) cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell getHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE)));
        cell.setBackgroundColor(new BaseColor(52, 73, 94));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell getMetricCell(String label, Object value, Font valueFont, Font labelFont) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(6);
        cell.addElement(new Phrase(label + ":", labelFont));
        cell.addElement(new Phrase(String.valueOf(value), valueFont));
        return cell;
    }


}
