package com.bankasia.toc.config;

import com.bankasia.toc.am.domain.*;
import com.bankasia.toc.am.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/** First-run data for the Application Management module (empty database only). */
@Component
public class AmDataSeeder implements CommandLineRunner {

    // day | type (W working, H holiday, S saturday duty, A all present) | roster | additional | replacement
    private static final String[] NOV_2026 = {
        "1|W|SA,SM|MS|MS,SH",  "2|W|RI,SH|AR|SA,SM",  "3|W|SA,AR|MS|ZI,AT",  "4|W|ZI,AT|SH|SA,AR",
        "5|W|SS,SH|OF|SA,AR",  "6|H",  "7|S|SH",
        "8|W|ZI,AR|AT|SS,SH",  "9|W|SS,SH|MS|ZI,AR",  "10|W|RI,SM|AR|ZI,SH", "11|W|ZI,SH|SM|RI,SM",
        "12|W|SA,AT|OF|ZI,SH", "13|H",  "14|S|RI",
        "15|W|SS,OF|SM|SA,SM", "16|W|SA,SM|MS|SS,OF", "17|W|ZI,OF|AR|MS,AR", "18|W|RI,AR|MS|ZI,OF",
        "19|W|SS,AT|OF|SA,SM", "20|H",  "21|S|OF",
        "22|W|SA,AR|AT|SS,OF", "23|W|SS,OF|MS|SA,AR", "24|W|SA,AT|MS|ZI,SM", "25|W|ZI,SM|OF|SA,AT",
        "26|W|RI,OF|AR|SS,SM", "27|H",  "28|S|SA",
        "29|W|ZI,SM|MS|SA,AT", "30|A"
    };

    // Branch groups: label ; primary ; secondary ; "code name" entries separated by '|'. A '-' code means no code.
    private static final String[][] GROUPS = {
        {"Group 1", "ZI", "AR", "003 PRINCIPAL OFFICE BRANCH|008 MCB DILKUSHA BRANCH|037 KONABARI BRANCH|041 SAVAR BRANCH|043 DONIA BRANCH|059 MADHOBDI BRANCH|067 HEMAYETPUR BRANCH|080 KALATIA BRANCH|089 MUKSUDPUR BRANCH|162 BALIGAON SME/AGRI BRANCH|601 NANGOLKOT BRANCH|607 GOPALGONJ BRANCH|608 MAUJDEE COURT BRANCH|609 FARIDGONJ BRANCH|627 BANK ASIA TOWER|630 SIRAJGONJ BRANCH|635 MANIKGONJ BRANCH|706 BIG BOSS SUB BRANCH|711 ROBINTEX SUB BRANCH"},
        {"Group 2", "AR", "ZI", "004 GULSHAN BRANCH|006 MALKHANAGOR BRANCH|014 MITFORD BRANCH|015 UTTARA BRANCH|034 BAHADDERHAT BRANCH|048 NARAYANGONJ BRANCH|061 DOHAZARI BRANCH|076 DINAJPUR BRANCH|079 GAZIPUR BRANCH|153 REKABIBAZAR SME CENTRE|158 CHAKTAI BRANCH|161 OXYGEN MOOR BRANCH|503 PRINCIPAL ISLAMIC WINDOW|604 BRAHMANBARIA BRANCH|617 KALIAKAIR BRANCH|629 PAIRABANDAR BRANCH|701 ROBINTEX SUB BRANCH|702 SHOMAHAR SUB-BRANCH|703 ASHUGONJ SUB-BRANCH"},
        {"Group 3", "OF", "AT", "002 CORPORATE BRANCH|020 LOHAGARA BRANCH|052 BHAIRAB BRANCH|069 SATMOSJID ROAD BRANCH|072 SONAIMURI BRANCH|078 CHANDRAGONJ BRANCH|086 RING ROAD BRANCH|088 RANGPUR BRANCH|154 RAMGONJ SME AGRI BRANCH|155 JURAIN SME CENTRE|611 COMPANIGONJ BRANCH|612 KASBA BRANCH|623 JHENAIDAH BRANCH|626 MATARBARI BRANCH|632 TARAGONJ BRANCH|633 SONARGAON BRANCH|634 SATKHIRA BRANCH"},
        {"Group 4", "AT", "OF", "005 AGRABAD BRANCH|011 TARAIL BRANCH|013 KHATUNGONJ BRANCH|019 SYLHET UPOSHAHAR BRANCH|025 KHULNA BRANCH|028 MOHAKHALI BRANCH|031 ROHITPUR BRANCH|035 SHANTINAGAR BRANCH|036 TONGI BRANCH|045 KAMAL BAZAR BRANCH|047 FARIDPUR BRANCH|056 TEJGAON LINK ROAD BRANCH|057 BARISHAL BRANCH|058 LALDIGHIRPAR BRANCH|060 HAJIGONJ BRANCH|063 ELEPHANT ROAD BRANCH|105 CHITTAGONG ZONAL OFFICE|501 UTTARA ISLAMIC WINDOW|714 SINGRA SUB-BRANCH"},
        {"Group 5", "SH", "SM", "010 SYLHET MAIN BRANCH|016 ASHULIA BRANCH|026 CHATKHIL BRANCH|030 ANDERKILLA BRANCH|046 COXS BAZAR BRANCH|049 PALTAN BRANCH|051 MOULVIBAZAR BRANCH|053 STRAND ROAD BRANCH|068 MOHADEVPUR BRANCH|070 NIMTOLA BRANCH|087 TANGAIL BRANCH|101 CREDIT CARD|108 AGENT BANKING BACK OFFICE|157 RUPNAGAR BRANCH|502 SHANTINAGAR ISLAMIC WINDOW|618 AGANAGAR BRANCH|713 NAZIPUR SUB-BRANCH"},
        {"Group 6", "SM", "SH", "017 NORTH SOUTH ROAD BRANCH|018 CDA AVENUE BRANCH|022 STATION ROAD BRANCH|024 RAJSHAHI BRANCH|027 EPZ BRANCH|032 BOGRA BRANCH|038 BHATIARY BRANCH|054 PATHERHAT BRANCH|064 AGLA BAZAR BRANCH|066 DAKSHINKHAN BRANCH|071 COMILLA BRANCH|074 PARAGRAM BRANCH|077 LICHUBAGAN BRANCH|081 BANK ASIA BHABAN BRANCH|082 KUSHTIA BRANCH|602 RUPSHA BRANCH|606 MYMENSINGH BRANCH|622 CHAPAINAWABGONJ BRANCH|715 CUMILLA EPZ SUB-BRANCH"},
        {"Group 7", "SA", "MS", "009 MCB SK. MUJIB ROAD BRANCH|012 MCB BANANI BRANCH|023 BASHUNDHARA BRANCH|039 PROGOTI SARANI BRANCH|042 BEANI BAZAR|050 JAGANNATHPUR BRANCH|065 KEPZ BRANCH|083 HOLY FAMILY RCMCH BRANCH|084 FATULLAH BRANCH|085 BSMMU BRANCH|108 AGENT BANKING BACK OFFICE|156 BORO BAZAR BRANCH|159 BASHABO SME SERVICE CENTER|504 BANK ASIA BHABAN ISLAMIC WINDOW|605 KAZIPARA BRANCH|615 GULSHAN-2 BRANCH|619 PANGSHA BRANCH|620 BANANI-11 BRANCH|708 BORHANUDDIN SUB-BRANCH"},
        {"Group 8", "MS", "SA", "029 MIRPUR BRANCH|044 MOGHBAZAR BRANCH|062 LALMATIA BRANCH|073 FENI BRANCH|151 SIRAUDIKHAN SME AGRI BRANCH|160 ASHULIA SME AGRICULTURE BRANCH|505 SYLHET UPOSHAHAR ISLAMIC WINDOW|610 HATIRDIA BRANCH|616 GOURIPUR BRANCH|621 PANCHAGARH BRANCH|624 BAMUNDI BRANCH|625 BARURA BRANCH|628 SHUKRABAD BRANCH|631 RAMPURA BRANCH|704 LOUHAJANG SUB-BRANCH|705 SREENAGAR SUB-BRANCH|707 BANCHARAMPUR SUB-BRANCH|710 KISHOREGONJ SUB-BRANCH|712 SHIBGANJ SUB-BRANCH"},
        {"Group 9", "SS", "AR", "007 SCOTIA BRANCH|021 DHANMONDI BRANCH|033 JESSORE BRANCH|040 ISHWARDI BRANCH|055 SHYAMOLI BRANCH|075 SONARGAON-JANAPATH ROAD BRANCH|106 SYLHET ZONAL OFFICE|152 JATRABARI SME SERVICE CENTRE|603 MIRPUR -1 BRANCH|613 CHANDPUR BRANCH|614 BHOLA BRANCH"},
        {"CTSU", "TA", "", "110 CTSU|- All AD branch : Foreign Trade, CTPC"}
    };

    // officer code ; department list separated by '|'
    private static final String[][] DEPARTMENTS = {
        {"SA", "Consumer Credit Department|Agricultural Credit Department|Branch Operations Division"},
        {"AR", "Group Finance|Accounts|Special Asset Management Department"},
        {"SM", "Risk Management Division|Legal & Recovery Department"},
        {"SS", "Treasury Division|Liability Operation Department"},
        {"ZI", "Credit Administration Department|Corporate & Large Loan Division"},
        {"AT", "Small & Medium Enterprise|Research & Strategy|Syndication & Structured Finance Unit"},
        {"MS", "BA Institute Of Training & Development|Logistics & Support Services Division"},
        {"RI", "Anti Money Laundering Department|Human Resource Division"},
        {"FS", "Credit Risk Management Division (Ocas)|Liability Operation Department (DMS)|Alternative Delivery Channel"},
        {"AO", "Remittance|Payment Service Department"},
        {"SH", "Liability Marketing|Internal Control & Compliance Division|International Division"},
        {"TA", "Central Trade Processing Center"}
    };

    private final AmMemberRepository members;
    private final AmDayRepository days;
    private final AmAssignmentRepository assignments;
    private final AmEscalationRepository escalations;
    private final BranchGroupRepository groups;
    private final BranchRepository branches;
    private final AmDepartmentRepository departments;

    public AmDataSeeder(AmMemberRepository members, AmDayRepository days, AmAssignmentRepository assignments,
                        AmEscalationRepository escalations, BranchGroupRepository groups,
                        BranchRepository branches, AmDepartmentRepository departments) {
        this.members = members;
        this.days = days;
        this.assignments = assignments;
        this.escalations = escalations;
        this.groups = groups;
        this.branches = branches;
        this.departments = departments;
    }

    @Override @Transactional
    public void run(String... args) {
        if (members.count() > 0) return;

        Map<String, AmMember> m = new LinkedHashMap<>();
        m.put("SA", new AmMember("Samia Ahmed", "01335222122", "001821", false));
        m.put("ZI", new AmMember("Md. Zamiul Islam Talukdar", "01335222121", "001842", false));
        m.put("AR", new AmMember("Md. Ashiqur Rahman", "01904119254", "001825", true));
        m.put("AT", new AmMember("Md. Atiqul Hasan", "01904119225", "001872", true));
        m.put("OF", new AmMember("Omar Faroque", "01335222124", "001845", false));
        m.put("SM", new AmMember("Md. Sahin Miah", "01930245440", "001873", true));
        m.put("SH", new AmMember("Sayeda Humayrha Mushrat", "01335222125", "001823", false));
        m.put("MS", new AmMember("Masuma Sultana", "01904119246", "001838", false));
        m.put("SS", new AmMember("Ms. Saima Shamsunnaher", "01713750917", "001817", false));
        m.put("RI", new AmMember("Ms. Kh. Rafiza Islam", null, null, false));
        m.put("TA", new AmMember("Md. Taaha", "01904119253", "001827", false));
        m.put("FS", new AmMember("Farhan Sabbir", null, null, false));
        m.put("AO", new AmMember("Ashiqur Rahman Opu", null, null, false));
        members.saveAll(m.values());

        escalations.saveAll(List.of(
                new AmEscalation(1, "Concern support personnel", null, null),
                new AmEscalation(2, "Zamiul Islam Talukder", null, "01335222121"),
                new AmEscalation(3, "Md. Asaduzzaman", null, "01913603728"),
                new AmEscalation(4, "Muntasir Rahman", "CIO", "01708497388")));

        seedRoster(m);
        seedBranches(m);
        for (String[] d : DEPARTMENTS)
            for (String name : d[1].split("\\|")) departments.save(new AmDepartment(name, m.get(d[0])));
    }

    private void seedRoster(Map<String, AmMember> m) {
        List<AmAssignment> batch = new ArrayList<>();
        for (String row : NOV_2026) {
            String[] p = row.split("\\|");
            LocalDate date = LocalDate.of(2026, 11, Integer.parseInt(p[0]));
            DayType type = switch (p[1]) {
                case "H" -> DayType.HOLIDAY;
                case "S" -> DayType.SATURDAY_DUTY;
                case "A" -> DayType.ALL_PRESENT;
                default -> DayType.WORKING;
            };
            if (type != DayType.WORKING) days.save(new AmDay(date, type));
            if (p.length > 2) add(batch, m, date, AmSlot.ROSTER, p[2]);
            if (p.length > 3) add(batch, m, date, AmSlot.ADDITIONAL, p[3]);
            if (p.length > 4) add(batch, m, date, AmSlot.REPLACEMENT, p[4]);
        }
        assignments.saveAll(batch);
    }

    private void add(List<AmAssignment> out, Map<String, AmMember> m, LocalDate d, AmSlot s, String codes) {
        for (String c : codes.split(",")) out.add(new AmAssignment(d, s, m.get(c.trim())));
    }

    private void seedBranches(Map<String, AmMember> m) {
        for (String[] g : GROUPS) {
            BranchGroup group = groups.save(new BranchGroup(g[0], m.get(g[1]), g[2].isEmpty() ? null : m.get(g[2])));
            List<Branch> list = new ArrayList<>();
            for (String entry : g[3].split("\\|")) {
                int sp = entry.indexOf(' ');
                String code = entry.substring(0, sp);
                list.add(new Branch("-".equals(code) ? null : code, entry.substring(sp + 1), group));
            }
            branches.saveAll(list);
        }
    }
}
