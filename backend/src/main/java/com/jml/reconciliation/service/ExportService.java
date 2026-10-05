package com.jml.reconciliation.service;

import com.jml.reconciliation.entity.AuditEvent;
import com.jml.reconciliation.entity.RemediationAction;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String generateRemediationCsv(List<RemediationAction> actions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Action ID,Issue ID,Username,Full Name,Action Type,App Name,Status,Previous Permission,Target Permission,Initiated By,Approved By,Stakeholder Validated,Validated By,Validation Notes,Created At,Executed At,Rationale\n");

        for (RemediationAction a : actions) {
            sb.append(a.getId()).append(",")
              .append(a.getIssueId()).append(",")
              .append(escapeCsv(a.getUsername())).append(",")
              .append(escapeCsv(a.getFullName())).append(",")
              .append(escapeCsv(a.getActionType())).append(",")
              .append(escapeCsv(a.getAppName())).append(",")
              .append(a.getStatus()).append(",")
              .append(a.getPreviousPermissionLevel() != null ? a.getPreviousPermissionLevel() : "").append(",")
              .append(a.getTargetPermissionLevel() != null ? a.getTargetPermissionLevel() : "").append(",")
              .append(escapeCsv(a.getInitiatedBy())).append(",")
              .append(escapeCsv(a.getApprovedBy())).append(",")
              .append(Boolean.TRUE.equals(a.getStakeholderValidated()) ? "YES" : "NO").append(",")
              .append(escapeCsv(a.getValidatedBy())).append(",")
              .append(escapeCsv(a.getValidationNotes())).append(",")
              .append(a.getCreatedAt() != null ? a.getCreatedAt().format(FMT) : "").append(",")
              .append(a.getExecutedAt() != null ? a.getExecutedAt().format(FMT) : "").append(",")
              .append(escapeCsv(a.getRationale())).append("\n");
        }
        return sb.toString();
    }

    public String generateAuditCsv(List<AuditEvent> events) {
        StringBuilder sb = new StringBuilder();
        sb.append("Event ID,Event Type,Actor,Target Entity,Details,Timestamp\n");

        for (AuditEvent e : events) {
            sb.append(e.getId()).append(",")
              .append(escapeCsv(e.getEventType())).append(",")
              .append(escapeCsv(e.getActor())).append(",")
              .append(escapeCsv(e.getTargetEntity())).append(",")
              .append(escapeCsv(e.getDetails())).append(",")
              .append(e.getTimestamp() != null ? e.getTimestamp().format(FMT) : "").append("\n");
        }
        return sb.toString();
    }

    public String generateRemediationPdfHtml(List<RemediationAction> actions) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>JML Access Removal Evidence Trail Report</title>")
          .append("<style>")
          .append("body { font-family: 'Helvetica Neue', Arial, sans-serif; margin: 30px; color: #1e293b; background: #ffffff; }")
          .append(".header { border-bottom: 3px solid #0284c7; padding-bottom: 15px; margin-bottom: 25px; }")
          .append(".title { font-size: 24px; font-weight: bold; color: #0f172a; }")
          .append(".subtitle { font-size: 14px; color: #64748b; margin-top: 5px; }")
          .append(".badge { display: inline-block; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; }")
          .append(".badge-executed { background: #dcfce7; color: #166534; }")
          .append(".badge-validated { background: #e0f2fe; color: #075985; }")
          .append("table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 12px; }")
          .append("th { background: #f8fafc; text-align: left; padding: 10px; border-bottom: 2px solid #cbd5e1; color: #334155; font-weight: 600; }")
          .append("td { padding: 10px; border-bottom: 1px solid #e2e8f0; vertical-align: top; }")
          .append(".footer { margin-top: 40px; border-top: 1px solid #e2e8f0; padding-top: 15px; font-size: 11px; color: #94a3b8; text-align: center; }")
          .append("</style></head><body>")
          .append("<div class='header'>")
          .append("<div class='title'>JML Identity Reconciliation & Access Removal Evidence Trail</div>")
          .append("<div class='subtitle'>Official Audit Record & Compliance Sign-off Certificate</div>")
          .append("</div>")
          .append("<table><thead><tr>")
          .append("<th>ID</th><th>User Details</th><th>Action / Application</th><th>Status</th><th>Governance & Validation</th><th>Timestamp</th>")
          .append("</tr></thead><tbody>");

        for (RemediationAction a : actions) {
            sb.append("<tr>")
              .append("<td>#").append(a.getId()).append("</td>")
              .append("<td><strong>").append(html(a.getFullName())).append("</strong><br><small>").append(html(a.getUsername())).append("</small></td>")
              .append("<td>").append(html(a.getActionType())).append("<br><small>").append(html(a.getAppName())).append("</small></td>")
              .append("<td><span class='badge badge-executed'>").append(a.getStatus()).append("</span></td>")
              .append("<td>Initiated: ").append(html(a.getInitiatedBy())).append("<br>Approved: ").append(html(a.getApprovedBy() != null ? a.getApprovedBy() : "AUTO-RULE"));
            
            if (Boolean.TRUE.equals(a.getStakeholderValidated())) {
                sb.append("<br><span class='badge badge-validated'>Validated by ").append(html(a.getValidatedBy())).append("</span>");
            }
            sb.append("</td>")
              .append("<td>").append(a.getCreatedAt() != null ? a.getCreatedAt().format(FMT) : "").append("</td>")
              .append("</tr>");
        }

        sb.append("</tbody></table>")
          .append("<div class='footer'>Generated by JML Access Reconciliation Engine • Confirmed Auditable Security Trail</div>")
          .append("</body></html>");

        return sb.toString();
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    private String html(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
