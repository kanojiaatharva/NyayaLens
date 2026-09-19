package com.nayalens.analysis.rules;

import com.nayalens.document.model.LegalDocument;
import com.nayalens.evidence.model.Claim;

import java.util.List;

public interface LegalRiskRule {
    String ruleId();
    String ruleName();
    List<Claim> evaluate(LegalDocument document);
}
