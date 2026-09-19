package com.nayalens.legalresources.service;

import com.nayalens.legalresources.model.LegalResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LegalResourceService {

    private final List<LegalResource> resources = List.of(
            new LegalResource(
                    "res_indiacode",
                    "India Code - Digital Repository of All Central and State Acts",
                    "STATUTORY_REPOSITORY",
                    "The official digital repository of all enacted Indian legislation, maintained by the Legislative Department, Ministry of Law and Justice, Government of India. Authoritative source for Indian Contract Act 1872, Transfer of Property Act 1882, Specific Relief Act 1963, and DPDP Act 2023.",
                    "https://www.indiacode.nic.in",
                    "OFFICIAL_GOVERNMENT",
                    "Indian Contract Act, Transfer of Property Act, Industrial Disputes Act, DPDP Act 2023"
            ),
            new LegalResource(
                    "res_nalsa",
                    "National Legal Services Authority (NALSA)",
                    "LEGAL_AID",
                    "Statutory body established under the Legal Services Authorities Act, 1987, providing free and competent legal services to the weaker sections of society and organizing Lok Adalats for amicable settlement of disputes.",
                    "https://nalsa.gov.in",
                    "STATUTORY_AUTHORITY",
                    "Legal Services Authorities Act 1987, Free Legal Aid Guidelines"
            ),
            new LegalResource(
                    "res_ecourts",
                    "eCourts Services - Supreme Court e-Committee",
                    "JUDICIAL_PORTAL",
                    "National portal under the e-Courts Project by the Supreme Court of India and Ministry of Law & Justice, enabling citizens to track District, High Court, and Supreme Court case statuses, orders, judgements, and cause lists.",
                    "https://services.ecourts.gov.in",
                    "OFFICIAL_GOVERNMENT",
                    "Civil Procedure Code, Court Management & Filing Protocols"
            ),
            new LegalResource(
                    "res_sci",
                    "Supreme Court of India - Official Judgments Portal",
                    "JUDICIAL_PORTAL",
                    "Official portal providing authenticated digital copies of reported judgments and orders pronounced by the Supreme Court of India under Article 141 of the Constitution (law declared is binding on all courts).",
                    "https://www.sci.gov.in",
                    "CONSTITUTIONAL_BODY",
                    "Constitution of India, Article 141 Binding Precedents"
            ),
            new LegalResource(
                    "res_ncdrc",
                    "National Consumer Disputes Redressal Commission (e-Daakhil)",
                    "REGULATORY",
                    "Online portal under the Consumer Protection Act, 2019, for electronic filing of consumer complaints before District, State, and National Consumer Commissions without physical court presence.",
                    "https://edaakhil.nic.in",
                    "STATUTORY_AUTHORITY",
                    "Consumer Protection Act 2019"
            ),
            new LegalResource(
                    "res_samadhan",
                    "SAMADHAN - Ministry of Labour & Employment Portal",
                    "LEGAL_AID",
                    "Government of India portal for industrial dispute resolution, conciliation proceedings between workmen/employees and employers under the Industrial Disputes Act, 1947.",
                    "https://samadhan.labour.gov.in",
                    "OFFICIAL_GOVERNMENT",
                    "Industrial Disputes Act 1947, Payment of Gratuity Act 1972"
            )
    );

    public List<LegalResource> getAllResources() {
        return resources;
    }

    public Optional<LegalResource> getResourceById(String id) {
        return resources.stream().filter(r -> r.id().equalsIgnoreCase(id)).findFirst();
    }
}
