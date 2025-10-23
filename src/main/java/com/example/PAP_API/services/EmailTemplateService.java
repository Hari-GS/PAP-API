package com.example.PAP_API.services;

import com.example.PAP_API.config.AppProperties;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailTemplateService {

    private final AppProperties appProperties;

    public String getWelcomeEmail(String hrName) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2>Welcome aboard, %s!</h2>
                    <p>We're excited to have you on our platform. 🎉</p>
                    <p>Start exploring your dashboard and manage appraisals seamlessly.</p>
                    <br/>
                    <p>Best Regards,<br/>Team CIT</p>
                </body>
                </html>
                """.formatted(hrName);
    }

    // You can add other templates later
    public String getAppraisalNotification(String employeeName, String appraisalName) {
        return """
                <html><body>
                <p>Hello %s,</p>
                <p>Your new appraisal <b>%s</b> has been created successfully.</p>
                <p>Best regards,<br/>Team CIT</p>
                </body></html>
                """.formatted(employeeName, appraisalName);
    }

    public String getParticipantAddedEmail(String employeeName, String portalUrl) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f8f9fa; padding: 20px;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <tr>
                        <td style="background-color: #0056b3; color: white; padding: 20px; text-align: center; border-top-left-radius: 8px; border-top-right-radius: 8px;">
                            <h2 style="margin: 0;">CIT Internal Performance Appraisal Portal</h2>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 30px;">
                            <p style="font-size: 16px;">Dear <strong>%s</strong>,</p>
                            
                            <p style="font-size: 15px; line-height: 1.6;">
                                You have been successfully added as a <strong>participant</strong> in the <b>CIT Internal Performance Appraisal Portal</b>.
                                This platform helps you manage your appraisals, self-evaluations, and performance goals efficiently.
                            </p>

                            <p style="font-size: 15px; line-height: 1.6;">
                                To get started, please set up your account by clicking the link below:
                            </p>

                            <div style="text-align: center; margin: 25px 0;">
                                <a href="%s" style="background-color: #0056b3; color: white; padding: 12px 20px; border-radius: 5px; text-decoration: none; font-weight: bold;">
                                    Set Your Password
                                </a>
                            </div>

                            <p style="font-size: 15px; line-height: 1.6;">
                                When setting up your account, please use your <strong>Employee ID</strong> and <strong>Email Address</strong> as registered with the HR department.
                            </p>

                            <p style="font-size: 15px; line-height: 1.6;">
                                Once your password is set, you can log in to the portal anytime using your credentials and explore your appraisal dashboard.
                            </p>

                            <p style="margin-top: 25px; font-size: 14px; color: #555;">
                                If you face any issues accessing the portal, please contact your HR Manager.
                            </p>

                            <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">

                            <p style="font-size: 14px; color: #777;">
                                Regards,<br/>
                                <strong>Team CIT</strong><br/>
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(employeeName, portalUrl);
    }

    public String getAppraisalCreatedForParticipantEmail(
            String employeeName,
            String appraisalName,
            String appraisalType,
            String startDate,
            String reportingReviewStartDate,
            String endDate,
            String hrManagerName
    ) {
        return "Hello " + employeeName + ",\n\n" +
                "We are pleased to inform you that a new performance appraisal has been created for you.\n\n" +
                "📋 *Appraisal Details*\n" +
                "• Appraisal Name: " + appraisalName + "\n" +
                "• Appraisal Type: " + appraisalType + "\n" +
                "• Self-Appraisal Opens On: " + startDate + "\n" +
                "• Self-Appraisal Ends and Reporting Review Starts On: " + reportingReviewStartDate + "\n" +
                "• Appraisal Ends On: " + endDate + "\n\n" +
                "You can log in to the Performance Appraisal Portal using the following link:\n" +
                appProperties.getLoginUrl() + "\n\n" +
                "Please ensure that your responses are submitted within the given timeline.\n\n" +
                "If you haven’t created an account yet, please use your Employee ID and Email Address to set your password.\n\n" +
                "If you have any questions, feel free to reach out to your HR Manager, " + hrManagerName + ".\n\n" +
                "Regards,\n" +
                "Team CIT";
    }

    public String getSelfReviewStartEmail(String name, String appraisalTitle, String appraisalType) {
        return "Hello " + name + ",\n\n" +
                "The Self-Review phase for the appraisal '" + appraisalTitle + " - "+ appraisalType +"' has now started.\n" +
                "Please log in to the Performance Appraisal Portal to complete your self-review.\n\n" +
                "Portal Link: " + appProperties.getLoginUrl() + "\n\n" +
                "Best regards,\nTeam CIT";
    }

    public String getReportingReviewStartEmail(String name, String appraisalTitle, String appraisalType) {
        return "Hello " + name + ",\n\n" +
                "The Self-Review phase has ended and the Reporting Review phase for the appraisal '" + appraisalTitle + " - "+ appraisalType + "' has now started.\n" +
                "Please log in to the Performance Appraisal Portal to review your subordinates, if you are assigned as reviewer for any of the participants.\n\n" +
                "Portal Link: " + appProperties.getLoginUrl() + "\n\n" +
                "Best regards,\nTeam CIT";
    }

    public String getAppraisalClosedEmail(String name, String appraisalTitle, String appraisalType) {
        return "Hello " + name + ",\n\n" +
                "The appraisal '" + appraisalTitle + " - "+ appraisalType + "' has been successfully closed.\n" +
                "Thank you for your participation and efforts throughout the process.\n\n" +
                "Best regards,\nTeam CIT";
    }


}
