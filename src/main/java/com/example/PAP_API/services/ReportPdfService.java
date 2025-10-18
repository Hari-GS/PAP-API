package com.example.PAP_API.services;

import com.example.PAP_API.dto.ParticipantReportDto;
import com.example.PAP_API.dto.ReviewAnswerDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class ReportPdfService {

    public ByteArrayInputStream generateParticipantReport(ParticipantReportDto reportDto) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
            Paragraph title = new Paragraph("Appraisal Report - " + reportDto.getAppraisalTitle(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Employee Details
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph employeeDetails = new Paragraph("Employee Details", subTitleFont);
            employeeDetails.setSpacingAfter(10);
            document.add(employeeDetails);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.addCell("Employee ID");
            infoTable.addCell(reportDto.getEmployeeId());
            infoTable.addCell("Employee Name");
            infoTable.addCell(reportDto.getEmployeeName());
            infoTable.addCell("Designation");
            infoTable.addCell(reportDto.getDesignation());
            infoTable.addCell("Manager");
            infoTable.addCell(reportDto.getManagerName());
            infoTable.addCell("Appraisal Stage");
            infoTable.addCell(reportDto.getAppraisalStage());
            infoTable.setSpacingAfter(20);
            document.add(infoTable);

            // Self Review Section
            Paragraph selfReview = new Paragraph("Self Review", subTitleFont);
            selfReview.setSpacingAfter(10);
            document.add(selfReview);

            PdfPTable selfTable = new PdfPTable(2);
            selfTable.setWidthPercentage(100);
            selfTable.addCell("Question");
            selfTable.addCell("Answer");

            reportDto.getSelfReview().forEach(q -> {
                selfTable.addCell(q.getQuestion());
                selfTable.addCell(q.getAnswer());
            });
            selfTable.setSpacingAfter(20);
            document.add(selfTable);

            // Manager Review Section
            Paragraph managerReview = new Paragraph("Manager Review", subTitleFont);
            managerReview.setSpacingAfter(10);
            document.add(managerReview);

            PdfPTable mgrTable = new PdfPTable(2);
            mgrTable.setWidthPercentage(100);
            mgrTable.addCell("Question");
            mgrTable.addCell("Answer");

            reportDto.getReportingReview().forEach(q -> {
                mgrTable.addCell(q.getQuestion());
                mgrTable.addCell(q.getAnswer());
            });
            document.add(mgrTable);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF report", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateParticipantReportStyled(ParticipantReportDto reportDto) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // ===== HEADER =====
            Paragraph title = new Paragraph("Appraisal Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // ===== BASIC DETAILS =====
            document.add(new Paragraph(reportDto.getEmployeeName() + " (" + reportDto.getEmployeeId() + ")", titleFont));
            document.add(new Paragraph("Designation: " + reportDto.getDesignation(), normalFont));
            document.add(new Paragraph("Manager: " + reportDto.getManagerName(), normalFont));
            document.add(new Paragraph("Appraisal Title: " + reportDto.getAppraisalTitle(), normalFont));
            document.add(new Paragraph("Stage: " + reportDto.getAppraisalStage(), normalFont));
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

            for (int i = 0; i < total; i++) {
                ReviewAnswerDto self = (selfList != null && i < selfList.size()) ? selfList.get(i) : null;
                ReviewAnswerDto manager = (managerList != null && i < managerList.size()) ? managerList.get(i) : null;

                String question = (self != null) ? self.getQuestion() :
                        (manager != null ? manager.getQuestion() : "Question " + (i + 1));
                String selfAns = (self != null && self.getAnswer() != null && !self.getAnswer().isBlank()) ? self.getAnswer() : "-";
                String mgrAns = (manager != null && manager.getAnswer() != null && !manager.getAnswer().isBlank()) ? manager.getAnswer() : "-";

                document.add(new Paragraph("Q" + (i + 1) + ". " + question, normalFont));
                document.add(new Paragraph("\uD83D\uDFE2 Self Review: " + selfAns, normalFont));
                document.add(new Paragraph("\uD83D\uDFE1 Manager Review: " + mgrAns, normalFont));
                document.add(Chunk.NEWLINE);
            }

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
